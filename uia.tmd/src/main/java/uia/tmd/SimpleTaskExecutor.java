package uia.tmd;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import uia.tmd.TaskExecutorListener.TaskExecutorEvent;
import uia.tmd.TaskExecutorListener.TaskExecutorEvent.OperationType;
import uia.tmd.model.xml.ColumnType;
import uia.tmd.model.xml.ExecutorType;
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
    protected boolean runTask(final TaskType task, Where[] wheres, final String parentPath) throws SQLException {
        if (this.tableRows.containsKey(task.getName())) {
            this.tableRows.put(task.getName(), new ArrayList<String>());
        }

        String statement = null;
        Map<String, Object> statementParams = null;
        OperationType operationTarget = OperationType.SOURCE;
        try {
            final SourceSelectType sourceSelect = task.getSourceSelect();
            final TargetUpdateType targetUpdate = task.getTargetUpdate();

            // source: select
            final List<ColumnType> sourceColumns = this.sourceAccessor.prepareColumns(sourceSelect.getTable());
            final List<ColumnType> sourcePK = findPK(sourceSelect.getTable(), sourceColumns);
            if (sourcePK.size() == 0) {
                throw new SQLException("Table:" + sourceSelect.getTable() + " without primary key.");
            }

            statement = AbstractDataAccessor.sqlSelect(sourceSelect.getTable(), sourceColumns, wheres);
            List<Map<String, Object>> sourceResult = this.sourceAccessor.select(statement, wheres);
            raiseSourceSelected(new TaskExecutorEvent(
                    task,
                    parentPath,
                    statement,
                    wheres,
                    sourceResult.size(),
                    operationTarget));

            String sourceDelete = null;
            if (sourceSelect.isDeleted()) {
                sourceDelete = AbstractDataAccessor.sqlDelete(sourceSelect.getTable(), sourcePK);
            }

            // target: delete & insert
            operationTarget = OperationType.TARGET;
            final String targetTableName = targetUpdate.getTable() == null ? sourceSelect.getTable() : targetUpdate.getTable();
            final List<ColumnType> targetColumns;
            if (targetUpdate.getColumns() == null || targetUpdate.getColumns().getColumn().size() == 0) {
                targetColumns = sourceColumns;
            }
            else {
                targetColumns = this.targetAccessor.prepareColumns(targetUpdate.getTable());
                // TODO: fix
                List<ColumnType> temp = targetUpdate.getColumns().getColumn();
                for (ColumnType temp0 : temp) {
                    for (ColumnType temp2 : targetColumns) {
                        if (temp2.getValue().equals(temp0.getValue())) {
                            temp2.setSource(temp0.getSource());
                            break;
                        }
                    }
                }
            }
            if (targetColumns.size() == 0) {
                throw new SQLException("Table:" + targetTableName + " without columns.");
            }
            final List<ColumnType> targetPK = findPK(targetTableName, targetColumns);
            if (targetPK.size() == 0) {
                throw new SQLException("Table:" + targetTableName + " without primary key.");
            }

            // BATCH if no children
            if (task.getNext() == null) {
                handleBatch(task, targetTableName, targetColumns, targetPK, parentPath, sourceResult);
            }
            else {
                for (Map<String, Object> row : sourceResult) {
                    Map<String, Object> sourcePKvalues = filterData(row, sourcePK);

                    operationTarget = OperationType.TARGET;
                    int rc = handle(task, sourcePKvalues.toString(), targetTableName, targetColumns, targetPK, parentPath, row);

                    // next plans
                    if (rc > 0 && !runNext(task, row, parentPath)) {
                        return false;
                    }

                    // delete source
                    operationTarget = OperationType.SOURCE;
                    if (sourceSelect.isDeleted()) {
                        statement = sourceDelete;
                        statementParams = sourcePKvalues;
                        int count = this.sourceAccessor.execueUpdate(statement, statementParams);
                        raiseSourceDeleted(new TaskExecutorEvent(
                                task,
                                parentPath,
                                sourceDelete,
                                sourcePKvalues,
                                count,
                                operationTarget));
                    }
                }
            }

            return true;
        }
        catch (SQLException ex) {
            ex.printStackTrace();
            raiseExecuteFailure(
                    new TaskExecutorEvent(task, parentPath, statement, statementParams, 0, operationTarget),
                    ex);
            throw ex;
        }
    }
}
