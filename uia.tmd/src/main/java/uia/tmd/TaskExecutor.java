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
public class TaskExecutor {

    private final ArrayList<TaskExecutorListener> listeners;

    private final TaskFactory factory;

    private final TaskType task1;

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
    TaskExecutor(TaskFactory factory, ExecutorType executor) throws Exception {
        this.factory = factory;
        this.task1 = this.factory.tasks.get(executor.getTask());
        this.source = this.factory.dbServers.get(executor.getSource());
        this.target = this.factory.dbServers.get(executor.getTarget());
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
    public boolean run(Map<String, Object> whereValues) {

        try {
            this.sourceAccessor.connect(this.source.getUser(), this.source.getPassword());
            this.targetAccessor.connect(this.target.getUser(), this.target.getPassword());
            return runTask(this.task1, whereValues);
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
        finally {
            try {
                this.sourceAccessor.disconnect();
                this.targetAccessor.disconnect();
            }
            catch (Exception ex) {

            }
        }
    }

    /**
     * Run this task.
     * @param whereValues Criteria used to select data from database.
     * @return Result.
     */
    private boolean runTask(TaskType task, Map<String, Object> whereValues) {
        String statement = null;
        Map<String, Object> statementParams = whereValues;
        try {
            SourceSelectType sourceSelect = task.getSourceSelect();
            TargetUpdateType targetUpdate = task.getTargetUpdate();

            // source: select
            List<ColumnType> sourceColumns = this.sourceAccessor.prepareColumns(sourceSelect.getTable());
            statement = DataAccessor.sqlSelect(sourceSelect.getTable(), sourceColumns, whereValues.keySet().toArray(new String[0]));
            List<Map<String, Object>> sourceResult = this.sourceAccessor.select(statement, statementParams);
            raiseSourceSelected(this.source.getId(), task.getName(), statement, statementParams, sourceResult.size());

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
                if (targetColumns.size() == 0) {
                    throw new SQLException(tableName + " without columns.");
                }
                List<ColumnType> pk = findPK(targetColumns);
                if (pk.size() == 0) {
                    throw new SQLException(tableName + " without primary key.");
                }

                statement = DataAccessor.sqlDelete(tableName, pk);
                statementParams = prepare(row, pk);
                int count = this.targetAccessor.execueUpdate(statement, statementParams);
                raiseTargetDeleted(this.target.getId(), task.getName(), statement, statementParams, count);

                statement = DataAccessor.sqlInsert(tableName, targetColumns);
                statementParams = prepare(row, targetColumns);
                this.targetAccessor.execueUpdate(statement, statementParams);
                raiseTargetInserted(this.target.getId(), task.getName(), statement, statementParams);

                // next plans
                if (task.getNexts() != null) {
                    for (PlanType p : task.getNexts().getPlan()) {
                        if (!runPlan(p, row)) {
                            return false;
                        }
                    }
                }
            }
            return true;
        }
        catch (SQLException ex) {
            raiseExecuteFailure(this.source.getId(), task.getName(), statement, statementParams, ex);
            return false;
        }
    }

    private boolean runPlan(PlanType plan, Map<String, Object> master) throws SQLException {
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

        TaskType task = this.factory.tasks.get(plan.getName());
        if (task == null) {
            System.out.println(plan.getName() + " no task");
        }
        Map<String, Object> whereValues = prepare(master, plan.getJoin().getColumn());
        runTask(task, whereValues);

        return true;

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

    private Map<String, Object> prepare(Map<String, Object> row, List<ColumnType> columns) throws SQLException {
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
