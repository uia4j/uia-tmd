package uia.tmd;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import uia.tmd.TaskExecutorListener.TaskExecutorEvent;
import uia.tmd.TaskExecutorListener.TaskExecutorEvent.OperationType;
import uia.tmd.model.xml.ColumnType;
import uia.tmd.model.xml.CriteriaType;
import uia.tmd.model.xml.ExecutorType;
import uia.tmd.model.xml.PlanType;
import uia.tmd.model.xml.SourceSelectType;
import uia.tmd.model.xml.TargetUpdateType;
import uia.tmd.model.xml.TaskType;

/**
 * Task executor.
 *
 * @author Kyle K. Lin
 *
 */
public class SimpleTaskExecutor extends TaskExecutor {

    /**
     * Constructor.
     *
     * @param task Task definition.
     * @param factory Task factory.
     * @throws Exception
     */
    SimpleTaskExecutor(TaskFactory factory, ExecutorType executor) throws Exception {
        super(factory, executor);
    }

    @Override
    protected boolean runTask(final TaskType task, String where, final String parentPath) throws SQLException {
        if (!this.tableRows.containsKey(task.getName())) {
            this.tableRows.put(task.getName(), new ArrayList<String>());
        }

        String statement = null;
        try {
            final SourceSelectType sourceSelect = task.getSourceSelect();

            final List<ColumnType> sourceColumns = this.sourceAccessor.prepareColumns(sourceSelect.getTable());
            final List<ColumnType> sourcePK = findPK(sourceColumns);
            if (sourcePK.size() == 0) {
                throw new SQLException("Table:" + sourceSelect.getTable() + " without primary key.");
            }

            // source: select
            statement = AbstractDataAccessor.sqlSelect(sourceSelect.getTable(), sourceColumns, where);
            List<Map<String, Object>> sourceResult = this.sourceAccessor.select(statement);
            raiseSourceSelected(new TaskExecutorEvent(
                    task,
                    parentPath,
                    statement,
                    sourceResult.size(),
                    OperationType.SOURCE));

            // target: insert
            runTaskToTarget(task, sourcePK, sourceColumns, sourceResult, parentPath);
            
            // source: delete
            deleteSource(task, sourcePK, sourceResult, parentPath);
            return true;
        }
        catch (SQLException ex) {
            ex.printStackTrace();
            raiseExecuteFailure(
                    new TaskExecutorEvent(task, parentPath, statement, 0, OperationType.SOURCE),
                    ex);
            throw ex;
        }
    }

    @Override
    protected boolean runTask(final TaskType task, Where[] wheres, final String parentPath) throws SQLException {
        if (!this.tableRows.containsKey(task.getName())) {
            this.tableRows.put(task.getName(), new ArrayList<String>());
        }

        String statement = null;
        try {
            final SourceSelectType sourceSelect = task.getSourceSelect();

            final List<ColumnType> sourceColumns = this.sourceAccessor.prepareColumns(sourceSelect.getTable());
            final List<ColumnType> sourcePK = findPK(sourceColumns);
            if (sourcePK.size() == 0) {
                throw new SQLException("Table:" + sourceSelect.getTable() + " without primary key.");
            }

            // source: select
            statement = AbstractDataAccessor.sqlSelect(sourceSelect.getTable(), sourceColumns, wheres);
            List<Map<String, Object>> sourceResult = this.sourceAccessor.select(statement, wheres);
            raiseSourceSelected(new TaskExecutorEvent(
                    task,
                    parentPath,
                    statement,
                    sourceResult.size(),
                    OperationType.SOURCE));

            // target: insert
            runTaskToTarget(task, sourcePK, sourceColumns, sourceResult, parentPath);
            
            // source: delete
            deleteSource(task, sourcePK, sourceResult, parentPath);
            return true;
        }
        catch (SQLException ex) {
            ex.printStackTrace();
            raiseExecuteFailure(
                    new TaskExecutorEvent(task, parentPath, statement, 0, OperationType.SOURCE),
                    ex);
            throw ex;
        }
    }

    private boolean runTaskToTarget(final TaskType task, List<ColumnType> sourcePK, List<ColumnType> sourceColumns, List<Map<String, Object>> sourceResult, final String parentPath) throws SQLException {
        String statement = null;
        try {
            final SourceSelectType sourceSelect = task.getSourceSelect();
            final TargetUpdateType targetUpdate = task.getTargetUpdate();

            // prepare target 
            final String targetTableName = targetUpdate.getTable() == null ? sourceSelect.getTable() : targetUpdate.getTable();
            final List<ColumnType> targetColumns;
            if (targetUpdate.getColumns() == null || targetUpdate.getColumns().getColumn().size() == 0) {
                targetColumns = sourceColumns;
            }
            else {
                targetColumns = this.targetAccessor.prepareColumns(targetUpdate.getTable());
                // TODO: fix
                List<ColumnType> mappingColumns = targetUpdate.getColumns().getColumn();
                for (ColumnType mappingColumn : mappingColumns) {
                    for (ColumnType targetColumn : targetColumns) {
                        if (targetColumn.getValue().equals(mappingColumn.getValue())) {
                            targetColumn.setSource(mappingColumn.getSource());
                            break;
                        }
                    }
                }
            }
            if (targetColumns.size() == 0) {
                throw new SQLException("Table:" + targetTableName + " without columns.");
            }
            final List<ColumnType> targetPK = findPK(targetColumns);
            if (targetPK.size() == 0) {
                throw new SQLException("Table:" + targetTableName + " without primary key.");
            }

            // delete and insert
            if (task.getNext() == null) {
            	// batch
                handleBatch(task, targetTableName, targetColumns, targetPK, parentPath, sourceResult);
            }
            else {
            	// one by one
                for (Map<String, Object> row : sourceResult) {
                    Map<String, Object> sourcePKvalues = filterData(row, sourcePK);
                    int rc = handleOne(task, sourcePKvalues.toString(), targetTableName, targetColumns, targetPK, parentPath, row);
                    // next plans
                    if (rc > 0 && !runNext(task, row, parentPath)) {
                        return false;
                    }
                }
            }
            return true;
        }
        catch (SQLException ex) {
            ex.printStackTrace();
            raiseExecuteFailure(
                    new TaskExecutorEvent(task, parentPath, statement, 0, OperationType.TARGET),
                    ex);
            throw ex;
        }
    }
    
    private void deleteSource(TaskType task, List<ColumnType> sourcePK, List<Map<String, Object>> sourceResult, String parentPath) throws SQLException {
        final SourceSelectType sourceSelect = task.getSourceSelect();
        if (sourceSelect.isDeleted()) {
            String statement = AbstractDataAccessor.sqlDelete(sourceSelect.getTable(), sourcePK);
            for (Map<String, Object> row : sourceResult) {
                Map<String, Object> sourcePKvalues = filterData(row, sourcePK);
                int count = this.sourceAccessor.execueUpdate(statement, sourcePKvalues);
                raiseSourceDeleted(new TaskExecutorEvent(
                        task,
                        parentPath,
                        statement,
                        count,
                        OperationType.SOURCE));
            }
        }
    }

    private int handleOne(TaskType task, String keyString, String targetTableName, List<ColumnType> targetColumns, List<ColumnType> targetPK, String parentPath, Map<String, Object> row) throws SQLException {
        // check if sync already
        List<String> kss = this.tableRows.get(task.getName());
        if (kss.contains(keyString)) {
            return 0;
        }
        kss.add(keyString);

        String statement = null;
        Map<String, Object> statementParams = null;
        try {
            if (this.deleteTarget) {
                statement = AbstractDataAccessor.sqlDelete(targetTableName, targetPK);
                statementParams = filterData(row, targetPK);
                int count = this.targetAccessor.execueUpdate(statement, statementParams);
                raiseTargetDeleted(new TaskExecutorEvent(task, parentPath, statement, count, OperationType.TARGET));
            }

            statement = AbstractDataAccessor.sqlInsert(targetTableName, targetColumns);
            statementParams = filterData(row, targetColumns);
            int uc = this.targetAccessor.execueUpdate(statement, statementParams);
            raiseTargetInserted(new TaskExecutorEvent(task, parentPath, statement, uc, OperationType.TARGET));

            return uc;
        }
        catch (SQLException ex) {
            raiseExecuteFailure(new TaskExecutorEvent(task, parentPath, statement, 0, OperationType.TARGET), ex);
            throw ex;
        }
    }

    private boolean runPlan(PlanType plan, Map<String, Object> master, String parentPath) throws SQLException {
        if (plan.getRule() != null && plan.getRule().getCriteria().size() > 0) {
            for (CriteriaType criteria : plan.getRule().getCriteria()) {
                Object v = master.get(criteria.getColumn());
                if (v == null) {
                    return true;
                }

                boolean accept = true;
                if (criteria.getValidate().equalsIgnoreCase("STARTSWITH")) {
                    accept = v.toString().startsWith(criteria.getValue());
                }
                else {
                    accept = v.toString().equals(criteria.getValue());
                }
                if (!accept) {
                    return true;
                }
            }
        }

        TaskType task = this.factory.tasks.get(plan.getTaskName());
        Map<String, Object> whereValues = filterData(master, plan.getJoin().getColumn());

        if (plan.getWhere() != null && plan.getWhere().getCriteria() != null) {
            List<CriteriaType> where = plan.getWhere().getCriteria();
            for (CriteriaType criteria : where) {
                if (criteria.isEmplyIsNull() && (criteria.getValue() == null || "".equals(criteria.getValue()))) {
                    whereValues.put(criteria.getColumn(), null);
                }
                else {
                    whereValues.put(criteria.getColumn(), criteria.getValue());
                }
            }
        }

        ArrayList<Where> ws = new ArrayList<Where>();
        for (Map.Entry<String, Object> e : whereValues.entrySet()) {
            ws.add(new WhereEq(e.getKey(), e.getValue()));
        }

        return runTask(task, ws.toArray(new Where[0]), parentPath);
    }

    private int handleBatch(TaskType task, String targetTableName, List<ColumnType> targetColumns, List<ColumnType> targetPK, String parentPath, List<Map<String, Object>> rows) throws SQLException {
        String statement = null;
        int count = 0;

        try {
            if (this.deleteTarget) {
                statement = AbstractDataAccessor.sqlDelete(targetTableName, targetPK);
                ArrayList<Map<String, Object>> targetPKvalues = new ArrayList<Map<String, Object>>();
                for (Map<String, Object> row : rows) {
                    targetPKvalues.add(filterData(row, targetPK));
                }
                count = this.targetAccessor.execueBatch(statement, targetPKvalues);
                raiseTargetDeleted(new TaskExecutorEvent(task, parentPath, statement, count, OperationType.TARGET));
            }

            statement = AbstractDataAccessor.sqlInsert(targetTableName, targetColumns);
            count = this.targetAccessor.execueBatch(statement, rows);
            raiseTargetInserted(new TaskExecutorEvent(task, parentPath, statement, count, OperationType.TARGET));

            return count;
        }
        catch (SQLException ex) {
            raiseExecuteFailure(new TaskExecutorEvent(task, parentPath, statement, 0, OperationType.TARGET), ex);
            throw ex;
        }
    }

    protected boolean runNext(TaskType task, Map<String, Object> row, String parentPath) throws SQLException {
        if (task.getNext() == null) {
            return true;
        }

        for (PlanType p : task.getNext().getPlan()) {
            if (!runPlan(p, row, parentPath + task.getName() + "/")) {
                return false;
            }
        }
        return true;
    }

    protected Map<String, Object> filterData(Map<String, Object> data, List<ColumnType> columns) throws SQLException {
        LinkedHashMap<String, Object> result = new LinkedHashMap<String, Object>();
        for (ColumnType column : columns) {
            if (column.getSource() == null) {
                result.put(column.getValue(), data.get(column.getValue()));
            }
            else {
                result.put(column.getValue(), data.get(column.getSource()));
            }
        }
        return result;
    }

    protected List<ColumnType> findPK(List<ColumnType> columns) {
        ArrayList<ColumnType> pk = new ArrayList<ColumnType>();
        for (ColumnType column : columns) {
            if (column.isPk()) {
                pk.add(column);
            }
        }
        return pk;
    }
}
