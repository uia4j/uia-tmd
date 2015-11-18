package uia.tmd;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import uia.tmd.TaskExecutorListener.TaskExecutorEvent;
import uia.tmd.model.xml.ColumnType;
import uia.tmd.model.xml.CriteriaType;
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

    private final TaskFactory factory;

    private final TaskType task;

    private final DbServerType source;

    private final DbServerType target;

    private final DataAccessor sourceAccessor;

    private final DataAccessor targetAccessor;

    /**
     * Constructor.
     *
     * @param task Task definition.
     * @param factory Task factory.
     * @throws Exception
     */
    TaskExecutor(TaskType task, TaskFactory factory) throws Exception {
        this.task = task;
        this.factory = factory;
        this.source = this.factory.dbServers.get(task.getSource());
        this.target = this.factory.dbServers.get(task.getTarget());
        this.sourceAccessor = createMSSQL(this.source, factory);
        this.targetAccessor = createMSSQL(this.target, factory);
        this.listeners = new ArrayList<TaskExecutorListener>();
    }

    /**
     *
     * @param listener
     */
    public void addListener(TaskExecutorListener listener) {
        this.listeners.add(listener);
    }

    /**
     * Run this task.
     * @param whereValues Criteria used to select data from database.
     * @return Result.
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

            // source: select
            List<ColumnType> sourceColumns = this.sourceAccessor.prepareColumns(sourceSelect.getTable());
            statement = DataAccessor.sqlSelect(sourceSelect.getTable(), sourceColumns, sourceSelect.getWhere().getColumn());
            statementParams = prepare(whereValues, sourceSelect.getWhere().getColumn());
            List<Map<String, Object>> sourceResult = this.sourceAccessor.select(statement, statementParams);
            raiseSourceSelected(this.source.getId(), this.task.getName(), statement, statementParams, sourceResult.size());

            // target: delete & insert
            for (Map<String, Object> row : sourceResult) {
                String tableName = targetUpdate.getTable() == null ? sourceSelect.getTable() : targetUpdate.getTable();
                List<ColumnType> targetColumns = null;
                if (targetUpdate.getColumns() == null || targetUpdate.getColumns().getColumn().size() == 0) {
                    targetColumns = sourceColumns;
                }
                else {
                    targetColumns = this.targetAccessor.prepareColumns(targetUpdate.getTable());
                }
                List<ColumnType> pk = findPK(targetColumns);

                statement = DataAccessor.sqlDelete(tableName, pk);
                statementParams = prepare(row, pk);
                int count = this.targetAccessor.execueUpdate(statement, statementParams);
                raiseTargetDeleted(this.target.getId(), this.task.getName(), statement, statementParams, count);

                statement = DataAccessor.sqlInsert(tableName, targetColumns);
                statementParams = prepareValues(row, targetColumns);
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

            if (plan.getRule() != null && plan.getRule().getCriteria() != null && plan.getRule().getCriteria().size() > 0) {
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

            // source: select
            List<ColumnType> sourceColumns = this.sourceAccessor.prepareColumns(sourceSelect.getTable());
            statement = DataAccessor.sqlSelect(sourceSelect.getTable(), sourceColumns, sourceSelect.getWhere().getColumn());
            statementParams = prepare(master, sourceSelect.getWhere().getColumn());
            List<Map<String, Object>> sourceTable = this.sourceAccessor.select(statement, statementParams);
            raiseSourceSelected(this.source.getId(), plan.getName(), statement, statementParams, sourceTable.size());

            // target: delete & insert
            for (Map<String, Object> row : sourceTable) {
                String tableName = targetUpdate.getTable() == null ? sourceSelect.getTable() : targetUpdate.getTable();
                List<ColumnType> targetColumns = null;
                if (targetUpdate.getColumns() == null || targetUpdate.getColumns().getColumn().size() == 0) {
                    targetColumns = sourceColumns;
                }
                else {
                    targetColumns = this.targetAccessor.prepareColumns(targetUpdate.getTable());
                }
                List<ColumnType> pk = findPK(targetColumns);

                statement = DataAccessor.sqlDelete(tableName, pk);
                statementParams = prepare(row, pk);
                int count = this.targetAccessor.execueUpdate(statement, statementParams);
                raiseTargetDeleted(this.target.getId(), plan.getName(), statement, statementParams, count);

                statement = DataAccessor.sqlInsert(tableName, targetColumns);
                statementParams = prepareValues(row, targetColumns);
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

    private DataAccessor createMSSQL(DbServerType dbServer, TaskFactory factory) throws Exception {
        if ("MSSQL".equalsIgnoreCase(dbServer.getDbType())) {
            return new MSSQLAccessor(factory.tables, dbServer.getHost(), dbServer.getPort(), dbServer.getDbName());
        }
        else if ("PGSQL".equalsIgnoreCase(dbServer.getDbType())) {
            return new PGSQLAccessor(factory.tables, dbServer.getHost(), dbServer.getPort(), dbServer.getDbName());
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

    private List<ColumnType> findPK(List<ColumnType> columns) {
        ArrayList<ColumnType> pk = new ArrayList<ColumnType>();
        for (ColumnType column : columns) {
            if (column.isPk()) {
                pk.add(column);
            }
        }
        return pk;
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
