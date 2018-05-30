package uia.tmd;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import uia.tmd.TaskExecutorListener.TaskExecutorEvent;
import uia.tmd.TaskExecutorListener.TaskExecutorEvent.DatabaseType;
import uia.tmd.model.xml.ExecutorType;
import uia.tmd.model.xml.SourceSelectType;
import uia.tmd.model.xml.TargetUpdateType;
import uia.tmd.model.xml.TaskType;
import uia.utils.dao.ColumnType;
import uia.utils.dao.TableType;
import uia.utils.dao.where.Where;

/**
 * Task executor.
 *
 * @author Kyle K. Lin
 *
 */
public class SimpleTaskExecutor extends TaskExecutor {

    SimpleTaskExecutor(TaskFactory factory, ExecutorType executor) throws Exception {
        super(factory, executor);
    }

    @Override
    protected boolean runTask(final TaskType task, String where, final String parentPath) throws SQLException {
        if (this.tableRows.containsKey(task.getName())) {
            this.tableRows.put(task.getName(), new ArrayList<String>());
        }

        // <sourceSelect>
        SourceSelectType sourceSelect = task.getSourceSelect();
        TableType sourceTable = this.sourceAccessor.getDatabase().selectTable(sourceSelect.getTable(), false);
        List<ColumnType> sourcePk = sourceTable.selectPk();
        if (sourcePk.size() == 0) {
            throw new SQLException("Table:" + sourceSelect.getTable() + " without primary key.");
        }
        List<ColumnType> sourceColumns = sourceTable.getColumns();
        String sourceSql = sourceTable.generateSelectSQL();
        List<Map<String, Object>> sourceRows = where == null
                ? this.sourceAccessor.select(sourceSql, null)
                : this.sourceAccessor.select(sourceSql + " WHERE " + where, null);

        raiseSourceSelected(new TaskExecutorEvent(task, parentPath, sourceSql, where, sourceRows.size(), DatabaseType.SOURCE));

        source2Target(task, parentPath, sourceTable, sourceColumns, sourceRows);

        if (sourceSelect.isDeleted()) {
            // TODO:
        }

        return true;

    }

    @Override
    protected boolean runTask(final TaskType task, Where where, final String parentPath) throws SQLException {
        if (this.tableRows.containsKey(task.getName())) {
            this.tableRows.put(task.getName(), new ArrayList<String>());
        }

        // <sourceSelect>
        SourceSelectType sourceSelect = task.getSourceSelect();
        TableType sourceTable = this.sourceAccessor.getDatabase().selectTable(sourceSelect.getTable(), false);
        List<ColumnType> sourcePk = sourceTable.selectPk();
        if (sourcePk.size() == 0) {
            throw new SQLException("Table:" + sourceSelect.getTable() + " without primary key.");
        }
        List<ColumnType> sourceColumns = sourceTable.getColumns();
        String sourceSql = sourceTable.generateSelectSQL();
        List<Map<String, Object>> sourceRows = this.sourceAccessor.select(sourceSql, where);

        raiseSourceSelected(new TaskExecutorEvent(task, parentPath, sourceSql, where.generate(), sourceRows.size(), DatabaseType.SOURCE));

        source2Target(task, parentPath, sourceTable, sourceColumns, sourceRows);

        if (sourceSelect.isDeleted()) {
            // TODO:
        }

        return true;
    }

    private void source2Target(TaskType task, String parentPath, TableType sourceTable, List<ColumnType> sourceColumns, List<Map<String, Object>> sourceRows) throws SQLException {
        // <targetUpdate>
        Map<String, String> mapping = null;
        TargetUpdateType targetUpdate = task.getTargetUpdate();
        TableType targetTable;
        List<ColumnType> targetColumns;
        if (targetUpdate.getTable() == null) {
            targetTable = sourceTable;
            targetColumns = sourceColumns;
        }
        else if (targetUpdate.getColumns() == null || targetUpdate.getColumns().getColumn().isEmpty()) {
            targetTable = this.targetAccessor.getDatabase().selectTable(targetUpdate.getTable(), false);
            targetColumns = sourceColumns;
        }
        else {
            mapping = targetUpdate.getColumns().getColumn()
                    .stream()
                    .collect(Collectors.toMap(c -> c.getValue(), c -> c.getSource()));
            targetTable = this.targetAccessor.getDatabase().selectTable(targetUpdate.getTable(), false);
            targetColumns = targetTable.getColumns();
        }

        ArrayList<List<Object>> targetRows = new ArrayList<List<Object>>();
        if (mapping == null) {
            // target == source
            for (Map<String, Object> sourceRow : sourceRows) {
                ArrayList<Object> targetRow = new ArrayList<Object>();
                for (ColumnType targetColumn : targetColumns) {
                    targetRow.add(sourceRow.get(targetColumn.getColumnName()));
                }
                targetRows.add(targetRow);
            }
        }
        else {
            // target != source
            for (Map<String, Object> sourceRow : sourceRows) {
                ArrayList<Object> targetRow = new ArrayList<Object>();
                for (ColumnType targetColumn : targetColumns) {
                    String sourceName = mapping.get(targetColumn.getColumnName());
                    targetRow.add(sourceRow.get(sourceName));
                }
                targetRows.add(targetRow);
            }
        }

        String targetSql = targetTable.generateInsertSQL();
        this.targetAccessor.getDatabase().executeBatch(targetSql, targetRows);
        this.raiseTargetInserted(new TaskExecutorEvent(task, parentPath, targetSql, null, targetRows.size(), DatabaseType.TARGET));
    }
}
