package uia.tmd;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import uia.tmd.TaskExecutorListener.TaskExecutorEvent;
import uia.tmd.model.xml.ColumnType;
import uia.tmd.model.xml.DbServerType;
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
public class TaskExecutor {

    private final ArrayList<TaskExecutorListener> listeners;

    private final TaskType task;

    private final DbServerType source;

    private final DbServerType target;

    private final DataAccessor sourceAccessor;

    private final DataAccessor targetAccessor;

    /**
     * Constructor.
     *
     * @param task Task definition.
     * @param source Source.
     * @param target Target.
     * @throws Exception
     */
    TaskExecutor(TaskType task, DbServerType source, DbServerType target) throws Exception {
        this.listeners = new ArrayList<TaskExecutorListener>();
        this.task = task;
        this.source = source;
        this.target = target;
        this.sourceAccessor = createMSSQL(this.source);
        this.targetAccessor = createMSSQL(this.target);
    }

    public void addListener(TaskExecutorListener listener) {
        this.listeners.add(listener);
    }

    /**
     * Run this task.
     * @param whereValues Criteria used to select data from database.
     * @throws SQLException SQL exception.
     */
    public boolean run(Map<String, Object> whereValues) throws SQLException {
        this.sourceAccessor.connect(this.source.getUser(), this.source.getPassword());
        this.targetAccessor.connect(this.target.getUser(), this.target.getPassword());

        String statement = null;
        Map<String, Object> statementParams = null;
        try {
            SourceSelectType sourceSelect = this.task.getSourceSelect();
            TargetUpdateType targetUpdate = this.task.getTargetUpdate();

            // select
            statement = DataAccessor.sqlSelect(sourceSelect.getTable(), sourceSelect.getColumns().getColumn(), sourceSelect.getWhere().getColumn());
            statementParams = prepare(whereValues, sourceSelect.getWhere().getColumn());
            List<Map<String, Object>> table = this.sourceAccessor.select(statement, statementParams);
            raiseSourceSelected(this.source.getId(), this.task.getName(), statement, statementParams, table.size());

            // delete & insert
            for (Map<String, Object> row : table) {
                String tableName = targetUpdate.getTable() == null ? sourceSelect.getTable() : targetUpdate.getTable();

                statement = DataAccessor.sqlDelete(tableName, targetUpdate.getWhere().getColumn());
                statementParams = prepare(row, targetUpdate.getWhere().getColumn());
                int count = this.targetAccessor.execueUpdate(statement, statementParams);
                raiseTargetDeleted(this.target.getId(), this.task.getName(), statement, statementParams, count);

                List<ColumnType> columns = targetUpdate.getColumns() == null || targetUpdate.getColumns().getColumn().size() == 0 ? sourceSelect.getColumns().getColumn() : targetUpdate.getColumns().getColumn();
                statement = DataAccessor.sqlInsert(tableName, columns);
                statementParams = prepareValues(row, columns);
                this.targetAccessor.execueUpdate(statement, statementParams);
                raiseTargetInserted(this.target.getId(), this.task.getName(), statement, statementParams);

                // next plans
                if (this.task.getNexts() != null) {
                    for (PlanType p : this.task.getNexts().getPlan()) {
                        if (!run(p, row)) {
                            return false;
                        }
                    }
                }
            }
            return true;
        }
        catch (SQLException ex) {
            raiseExecuteFailure(this.source.getId(), this.task.getName(), statement, statementParams, ex);
            return false;
        }
        finally {
            this.sourceAccessor.disconnect();
            this.targetAccessor.disconnect();
        }
    }

    private boolean run(PlanType plan, Map<String, Object> master) throws SQLException {
        String statement = null;
        Map<String, Object> statementParams = null;
        try {
            SourceSelectType sourceSelect = plan.getSourceSelect();
            TargetUpdateType targetUpdate = plan.getTargetUpdate();

            // select
            statement = DataAccessor.sqlSelect(sourceSelect.getTable(), sourceSelect.getColumns().getColumn(), sourceSelect.getWhere().getColumn());
            statementParams = prepare(master, sourceSelect.getWhere().getColumn());
            List<Map<String, Object>> sourceTable = this.sourceAccessor.select(statement, statementParams);
            raiseSourceSelected(this.source.getId(), plan.getName(), statement, statementParams, sourceTable.size());

            // delete & insert
            for (Map<String, Object> row : sourceTable) {
                String tableName = targetUpdate.getTable() == null ? sourceSelect.getTable() : targetUpdate.getTable();

                statement = DataAccessor.sqlDelete(tableName, targetUpdate.getWhere().getColumn());
                statementParams = prepare(row, targetUpdate.getWhere().getColumn());
                int count = this.targetAccessor.execueUpdate(statement, statementParams);
                raiseTargetDeleted(this.target.getId(), plan.getName(), statement, statementParams, count);

                List<ColumnType> columns = targetUpdate.getColumns() == null || targetUpdate.getColumns().getColumn().size() == 0 ? sourceSelect.getColumns().getColumn() : targetUpdate.getColumns().getColumn();
                statement = DataAccessor.sqlInsert(tableName, columns);
                statementParams = prepareValues(row, columns);
                this.targetAccessor.execueUpdate(statement, statementParams);
                raiseTargetInserted(this.target.getId(), plan.getName(), statement, statementParams);

                // next plans
                if (plan.getNexts() != null) {
                    for (PlanType p : this.task.getNexts().getPlan()) {
                        if (!run(p, row)) {
                            return false;
                        }
                    }
                }
            }
            return true;
        }
        catch (SQLException ex) {
            raiseExecuteFailure(this.source.getId(), plan.getName(), statement, statementParams, ex);
            return false;
        }
    }

    private DataAccessor createMSSQL(DbServerType dbServer) throws Exception {
        if ("MSSQL".equalsIgnoreCase(dbServer.getDbType())) {
            return new MSSQLAccessor(dbServer.getHost(), dbServer.getPort(), dbServer.getDbName());
        }
        else if ("PGSQL".equalsIgnoreCase(dbServer.getDbType())) {
            return new PGSQLAccessor(dbServer.getHost(), dbServer.getPort(), dbServer.getDbName());
        }

        return null;
    }

    private Map<String, Object> prepareValues(Map<String, Object> row, List<ColumnType> columns) {
        if (columns == null || columns.size() == 0) {
            return row;
        }
        else {
            return prepare(row, columns);
        }
    }

    private Map<String, Object> prepare(Map<String, Object> row, List<ColumnType> columns) {
        LinkedHashMap<String, Object> result = new LinkedHashMap<String, Object>();
        for (ColumnType column : columns) {
            if (column.getSource() == null) {
                result.put(column.getValue(), row.get(column.getValue()));
            }
            else {
                result.put(column.getValue(), row.get(column.getSource()));
            }
        }
        return result;
    }

    private void raiseSourceSelected(String db, String jobName, String sql, Map<String, Object> values, int count) {
        TaskExecutorEvent evt = new TaskExecutorEvent(db, jobName, sql, values);
        for (TaskExecutorListener l : this.listeners) {
            try {
                l.sourceSelected(evt, count);
            }
            catch (Exception ex2) {

            }
        }
    }

    private void raiseTargetInserted(String db, String jobName, String sql, Map<String, Object> values) {
        TaskExecutorEvent evt = new TaskExecutorEvent(db, jobName, sql, values);
        for (TaskExecutorListener l : this.listeners) {
            try {
                l.targetInserted(evt);
            }
            catch (Exception ex2) {

            }
        }
    }

    private void raiseTargetDeleted(String db, String jobName, String sql, Map<String, Object> values, int count) {
        TaskExecutorEvent evt = new TaskExecutorEvent(db, jobName, sql, values);
        for (TaskExecutorListener l : this.listeners) {
            try {
                l.targetDeleted(evt, count);
            }
            catch (Exception ex2) {

            }
        }
    }

    private void raiseExecuteFailure(String db, String jobName, String sql, Map<String, Object> values, SQLException ex) {
        TaskExecutorEvent evt = new TaskExecutorEvent(db, jobName, sql, values);
        for (TaskExecutorListener l : this.listeners) {
            try {
                l.executeFailure(evt, ex);
            }
            catch (Exception ex2) {

            }
        }
    }
}
