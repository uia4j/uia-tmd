package uia.tmd;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import uia.tmd.TaskExecutorListener.TaskExecutorEvent;
import uia.tmd.TaskExecutorListener.TaskExecutorEvent.Database;
import uia.tmd.model.xml.ColumnType;
import uia.tmd.model.xml.CriteriaType;
import uia.tmd.model.xml.DbServerType;
import uia.tmd.model.xml.ExecutorType;
import uia.tmd.model.xml.PlanType;
import uia.tmd.model.xml.TaskType;

/**
 * Task executor.
 *
 * @author Kyle K. Lin
 *
 */
public abstract class TaskExecutor {

    protected final ArrayList<TaskExecutorListener> listeners;

    protected final TaskFactory factory;

    protected final TaskType task1;

    protected final DbServerType source;

    protected final DbServerType target;

    protected final DataAccessor sourceAccessor;

    protected final DataAccessor targetAccessor;

    protected final TreeMap<String, List<String>> tableRows;

    private final String name;

    protected boolean deleteTarget;

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
        this.sourceAccessor = AbstractDataAccessor.create(this.source, factory.tables);
        this.targetAccessor = AbstractDataAccessor.create(this.target, factory.tables);
        this.listeners = new ArrayList<TaskExecutorListener>();

        this.deleteTarget = true;
        this.tableRows = new TreeMap<String, List<String>>();
        this.name = executor.getName();
    }

    /**
     * Get the name.
     * @return Name.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Add task listener.
     * @param listener The listener.
     */
    public void addListener(TaskExecutorListener listener) {
        if (listener == null) {
            return;
        }
        if (!this.listeners.contains(listener)) {
            this.listeners.add(listener);
        }
    }

    /**
     *
     * @param deleteTarget
     */
    public void setDeleteTarget(boolean deleteTarget) {
        this.deleteTarget = deleteTarget;
    }

    /**
     *
     * @return
     */
    public boolean isDeleteTarget() {
        return this.deleteTarget;
    }

    public boolean run(Where[] wheres) throws SQLException {
        this.tableRows.clear();
        try {
            this.sourceAccessor.connect(this.source.getUser(), this.source.getPassword());
            if (this.target != null) {  // TODO: good?
                this.targetAccessor.connect(this.target.getUser(), this.target.getPassword());
            }
            if (runTask(this.task1, wheres, "/")) {
                raiseDone();
                return true;
            }
            else {
                return false;
            }
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

    public boolean run(Map<String, Object> whereValues) throws SQLException {
        this.tableRows.clear();
        try {
            this.sourceAccessor.connect(this.source.getUser(), this.source.getPassword());
            if (this.target != null) {  // TODO: good?
                this.targetAccessor.connect(this.target.getUser(), this.target.getPassword());
            }
            if (runTask(this.task1, whereValues, "/")) {
                raiseDone();
                return true;
            }
            else {
                return false;
            }
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

    protected abstract boolean runTask(TaskType task, Where[] wheres, String parentPath);

    protected abstract boolean runTask(TaskType task, Map<String, Object> whereValues, String parentPath);

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

    protected int handle(TaskType task, String keyString, String targetTableName, List<ColumnType> targetColumns, List<ColumnType> targetPK, String parentPath, Map<String, Object> row) {

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
                statementParams = prepare(row, targetPK);
                int count = this.targetAccessor.execueUpdate(statement, statementParams);
                raiseTargetDeleted(new TaskExecutorEvent(
                        task,
                        parentPath,
                        statement,
                        statementParams,
                        count,
                        Database.TARGET));
            }

            statement = AbstractDataAccessor.sqlInsert(targetTableName, targetColumns);
            statementParams = prepare(row, targetColumns);
            int uc = this.targetAccessor.execueUpdate(statement, statementParams);
            raiseTargetInserted(new TaskExecutorEvent(
                    task,
                    parentPath,
                    statement,
                    statementParams,
                    uc,
                    Database.TARGET));

            return uc;
        }
        catch (SQLException ex) {
            raiseExecuteFailure(
                    new TaskExecutorEvent(task, parentPath, statement, statementParams, 0, Database.TARGET),
                    ex);
            return -1;
        }
    }

    protected Map<String, Object> prepare(Map<String, Object> row, List<ColumnType> columns) throws SQLException {
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

    protected List<ColumnType> findPK(List<ColumnType> columns) {
        ArrayList<ColumnType> pk = new ArrayList<ColumnType>();
        for (ColumnType column : columns) {
            if (column.isPk()) {
                pk.add(column);
            }
        }
        return pk;
    }

    protected void raiseSourceSelected(TaskExecutorEvent evt) {
        for (TaskExecutorListener l : this.listeners) {
            try {
                l.sourceSelected(this, evt);
            }
            catch (Exception ex2) {

            }
        }
    }

    protected void raiseSourceDeleted(TaskExecutorEvent evt) {
        for (TaskExecutorListener l : this.listeners) {
            try {
                l.sourceDeleted(this, evt);
            }
            catch (Exception ex2) {

            }
        }
    }

    protected void raiseTargetInserted(TaskExecutorEvent evt) {
        for (TaskExecutorListener l : this.listeners) {
            try {
                l.targetInserted(this, evt);
            }
            catch (Exception ex2) {

            }
        }
    }

    protected void raiseTargetDeleted(TaskExecutorEvent evt) {
        for (TaskExecutorListener l : this.listeners) {
            try {
                l.targetDeleted(this, evt);
            }
            catch (Exception ex2) {

            }
        }
    }

    protected void raiseExecuteFailure(TaskExecutorEvent evt, SQLException ex) {
        for (TaskExecutorListener l : this.listeners) {
            try {
                l.executeFailure(this, evt, ex);
            }
            catch (Exception ex2) {

            }
        }
    }

    protected void raiseDone() {
        for (TaskExecutorListener l : this.listeners) {
            try {
                l.done(this);
            }
            catch (Exception ex2) {

            }
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
        Map<String, Object> whereValues = prepare(master, plan.getJoin().getColumn());

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

        return runTask(task, whereValues, parentPath);
    }

    void printRunLog() {
        for (Map.Entry<String, List<String>> e : this.tableRows.entrySet()) {
            System.out.println(e.getKey() + "(" + e.getValue().size() + ")");
            for (String key : e.getValue()) {
                System.out.println("  " + key);
            }
        }
    }

}
