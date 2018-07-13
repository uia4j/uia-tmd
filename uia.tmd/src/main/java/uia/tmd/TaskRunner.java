package uia.tmd;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import uia.tmd.TaskListener.TaskEvent;
import uia.tmd.model.xml.ParamType;
import uia.tmd.model.xml.PlanType;
import uia.tmd.model.xml.SourceSelectType;
import uia.tmd.model.xml.TargetUpdateType;
import uia.tmd.model.xml.TaskType;
import uia.utils.dao.ColumnType;
import uia.utils.dao.TableType;
import uia.utils.dao.where.Where;

public final class TaskRunner {

    private final JobRunner jobRunner;

    public TaskRunner(JobRunner executor) {
        this.jobRunner = executor;
    }

    public void run(final TaskType task, final String parentPath, String where, SourceSelectFilter filter) throws SQLException {
        // <sourceSelect>
        SourceSelectType sourceSelect = task.getSourceSelect();
        TableType sourceTable = this.jobRunner.sourceAccess.prepareTable(sourceSelect.getTable());
        List<Map<String, Object>> sourceRows = this.jobRunner.sourceAccess.select(sourceSelect.getTable(), where);
        if (filter != null) {
            sourceRows = filter.accept(sourceRows);
        }

        this.jobRunner.raiseSourceSelected(new TaskEvent(
                task.getName(),
                parentPath,
                "SOURCE",
                sourceSelect.getTable(),
                where == null || where.trim().isEmpty()
                        ? "select * from " + sourceSelect.getTable()
                        : "select * from " + sourceSelect.getTable() + " where " + where,
                sourceRows.size(),
                TmdUtils.generateKeys(sourceTable, sourceRows)));

        // <targetUpdate>
        source2Target(task, parentPath, sourceTable, sourceTable.getColumns(), sourceRows);

        // <next>
        runNext(task, parentPath, sourceRows);

        // <executor sourceDelete='true'>
        // <sourceSelect delete='true'>
        sourceDelete(task, parentPath, sourceSelect, sourceRows);
    }

    public void run(final TaskType task, final String parentPath, String where, List<Object> paramValues, SourceSelectFilter filter) throws SQLException {
        if (where == null || where.trim().isEmpty()) {
            run(task, parentPath, null, filter);
        }

        // <sourceSelect>
        SourceSelectType sourceSelect = task.getSourceSelect();
        TableType sourceTable = this.jobRunner.sourceAccess.prepareTable(sourceSelect.getTable());
        List<Map<String, Object>> sourceRows = this.jobRunner.sourceAccess.select(sourceSelect.getTable(), where, paramValues);
        if (filter != null) {
            sourceRows = filter.accept(sourceRows);
        }
        String sql = Where.toString(where, paramValues);

        TaskEvent evt = new TaskEvent(
                task.getName(),
                parentPath,
                "SOURCE",
                sourceSelect.getTable(),
                "select * from " + sourceSelect.getTable() + " where " + sql,
                sourceRows.size(),
                TmdUtils.generateKeys(sourceTable, sourceRows));
        this.jobRunner.raiseSourceSelected(evt);

        // <targetUpdate>
        source2Target(task, parentPath, sourceTable, sourceTable.getColumns(), sourceRows);

        // <next>
        runNext(task, parentPath, sourceRows);

        // <executor sourceDelete='true'>
        // <sourceSelect delete='true'>
        sourceDelete(task, parentPath, sourceSelect, sourceRows);
    }

    private void runNext(final TaskType task, final String parentPath, final List<Map<String, Object>> sourceRows) throws SQLException {
        if (sourceRows.isEmpty() || task.getNext() == null || task.getNext().getPlan() == null) {
            return;
        }

        // <next>
        List<PlanType> plans = task.getNext().getPlan();
        plans.parallelStream().forEach(plan -> {
            try {
                runPlan(task, parentPath, plan, sourceRows);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void runPlan(final TaskType task, final String parentPath, PlanType plan, final List<Map<String, Object>> sourceRows) throws SQLException {
        TaskType nextTask = this.jobRunner.factory.getTask(plan.getTaskName());
        if (nextTask == null) {
            throw new SQLException("TASK:" + plan.getTaskName() + " not found");
        }

        String where = plan.getWhere();
        if (where == null || where.trim().isEmpty()) {
            throw new SQLException("PLAN:" + plan.getTaskName() + " WHERE not found");
        }

        String nextPath = parentPath + "/" + task.getName();
        for (Map<String, Object> sourceRow : sourceRows) {
            ArrayList<Object> paramValues = new ArrayList<Object>();
            for (ParamType param : plan.getParam()) {
                Object text = "";
                if (param.getSourceColumn() != null) {
                    text = sourceRow.get(param.getSourceColumn());
                }
                else {
                    text = param.getText();
                }

                if (param.getPrefix() != null) {
                    text = param.getPrefix() + text;
                }

                if (param.getPostfix() != null) {
                    text = "" + text + param.getPostfix();
                }

                paramValues.add(text);

            }
            run(nextTask, nextPath, where, paramValues, null);
        }
    }

    private void sourceDelete(TaskType task, String parentPath, SourceSelectType sourceSelect, List<Map<String, Object>> sourceRows) {
        // <executor sourceDelete='true'>
        // <sourceSelect delete='true'>

        boolean delete = this.jobRunner.isSourceDelete();
        if (sourceSelect.isDelete() != null) {
            delete = sourceSelect.isDelete().booleanValue();
        }

        if (delete) {
            this.jobRunner.txPool.delete(sourceSelect.getTable(), sourceRows);/**
                                                                             this.sourceAccess.delete(sourceSelect.getTable(), sourceRows);
                                                                             raiseSourceDeleted(new TaskExecutorEvent(
                                                                                     task.getName(),
                                                                                     parentPath,
                                                                                     "SOURCE",
                                                                                     sourceSelect.getTable(),
                                                                                     "delete from " + sourceSelect.getTable().toUpperCase(),
                                                                                     sourceRows.size()));
                                                                              */
        }
    }

    private void source2Target(TaskType task, String parentPath, TableType sourceTable, List<ColumnType> sourceColumns, List<Map<String, Object>> sourceRows) throws SQLException {
        if (sourceRows.isEmpty()) {
            return;
        }

        // <targetUpdate>
        Map<String, String> mapping = null;

        TableType targetTable;
        List<ColumnType> targetColumns;

        TargetUpdateType targetUpdate = task.getTargetUpdate();
        if (targetUpdate.getTable() == null) {
            targetTable = sourceTable;
            targetColumns = sourceColumns;
        }
        else if (targetUpdate.getColumnMapping() == null || targetUpdate.getColumnMapping().getColumn().isEmpty()) {
            targetTable = this.jobRunner.targetAccess.prepareTable(targetUpdate.getTable());
            targetColumns = sourceColumns;
        }
        else {
            mapping = targetUpdate.getColumnMapping().getColumn()
                    .stream()
                    .collect(Collectors.toMap(c -> c.getValue().toUpperCase(), c -> c.getSource().toUpperCase()));
            targetTable = this.jobRunner.targetAccess.prepareTable(targetUpdate.getTable());
            targetColumns = targetTable.getColumns();
        }

        ArrayList<Map<String, Object>> targetRows = new ArrayList<Map<String, Object>>();
        if (mapping == null) {
            // target == source
            for (Map<String, Object> sourceRow : sourceRows) {
                LinkedHashMap<String, Object> targetRow = new LinkedHashMap<String, Object>();
                for (ColumnType targetColumn : targetColumns) {
                    String key = targetColumn.getColumnName().toUpperCase();
                    targetRow.put(key, sourceRow.get(key));
                }
                targetRows.add(targetRow);
            }
        }
        else {
            // target != source
            for (Map<String, Object> sourceRow : sourceRows) {
                LinkedHashMap<String, Object> targetRow = new LinkedHashMap<String, Object>();
                for (ColumnType targetColumn : targetColumns) {
                    String key = targetColumn.getColumnName().toUpperCase();
                    String sourceName = mapping.get(key);
                    targetRow.put(key, sourceRow.get(sourceName));
                }
                targetRows.add(targetRow);
            }
        }

        this.jobRunner.txPool.insert(targetTable.getTableName(), targetRows);
        /**
        String sql = this.targetAccess.prepareTable(targetTable.getTableName()) != null
                ? this.targetAccess.prepareTable(targetTable.getTableName()).generateInsertSQL()
                : "insert into " + targetTable.getTableName().toUpperCase();
        this.targetAccess.insert(targetTable.getTableName(), targetRows);
        raiseTargetInserted(new TaskExecutorEvent(
                task.getName(),
                parentPath,
                "TARGET",
                targetTable.getTableName(),
                sql,
                targetRows.size()));
         */
    }
}
