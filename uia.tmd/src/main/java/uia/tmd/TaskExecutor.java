package uia.tmd;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import uia.tmd.TaskExecutorListener.TaskExecutorEvent;
import uia.tmd.model.xml.DbServerType;
import uia.tmd.model.xml.ExecutorType;
import uia.tmd.model.xml.MColumnType;
import uia.tmd.model.xml.MCriteriaType;
import uia.tmd.model.xml.MTableType;
import uia.tmd.model.xml.PlanType;
import uia.tmd.model.xml.TaskType;
import uia.utils.dao.ColumnType;
import uia.utils.dao.where.SimpleWhere;
import uia.utils.dao.where.Statement;
import uia.utils.dao.where.Where;

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

        this.sourceAccessor = (DataAccessor) Class.forName(this.source.getDbType()).newInstance();
        this.targetAccessor = (DataAccessor) Class.forName(this.target.getDbType()).newInstance();
        this.sourceAccessor.initial(this.source, factory.getTables());
        this.targetAccessor.initial(this.target, factory.getTables());

        this.listeners = new ArrayList<TaskExecutorListener>();

        this.deleteTarget = true;
        this.tableRows = new TreeMap<String, List<String>>();
        this.name = executor.getName();
    }

    /**
     * Constructor.
     *
     * @param task Task definition.
     * @param factory Task factory.
     * @throws Exception
     */
    TaskExecutor(TaskFactory factory, DataAccessor source, DataAccessor target, ExecutorType executor) throws Exception {
        this.factory = factory;
        this.task1 = this.factory.tasks.get(executor.getTask());
        this.source = this.factory.dbServers.get(executor.getSource());
        this.target = this.factory.dbServers.get(executor.getTarget());
        this.sourceAccessor = source;
        this.targetAccessor = target;
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

    public boolean run(String where) throws Exception {
        this.tableRows.clear();
        try {
            this.sourceAccessor.connect();
            if (this.target != null) {  // TODO: good?
                this.targetAccessor.connect();
            }
            if (runTask(this.task1, where, "/")) {
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

    public boolean run(Where where) throws Exception {
        this.tableRows.clear();
        try {
            this.sourceAccessor.connect();
            if (this.target != null) {  // TODO: good?
                this.targetAccessor.connect();
            }
            if (runTask(this.task1, where, "/")) {
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

    protected abstract boolean runTask(TaskType task, String where, String parentPath) throws SQLException;

    protected abstract boolean runTask(TaskType task, Where where, String parentPath) throws SQLException;

    /**
     * Handle <next>...</next>
     *
     * @param task
     * @param row
     * @param parentPath
     * @return
     * @throws SQLException
     */
    protected boolean runNext(TaskType task, Map<String, Object> row, String parentPath) throws SQLException {
        // <next />
        if (task.getNext() == null) {
            return true;
        }

        // <next><plan /><plan />...</next>
        for (PlanType p : task.getNext().getPlan()) {
            if (!runPlan(p, row, parentPath + task.getName() + "/")) {
                return false;
            }
        }
        return true;
    }

    /**
     * Handle <task>...</task>
     *
     * @param task
     * @param keyString
     * @param targetTableName
     * @param targetColumns
     * @param targetPK
     * @param parentPath
     * @param row
     * @return
     * @throws SQLException
     */
    protected int handle(TaskType task, String keyString, String targetTableName, List<ColumnType> targetColumns, List<MColumnType> targetPK, String parentPath, Map<String, Object> row) throws SQLException {
        // check if sync already
        List<String> kss = this.tableRows.get(task.getName());
        if (kss.contains(keyString)) {
            return 0;
        }
        kss.add(keyString);

        try {
            if (this.deleteTarget) {
                SimpleWhere where1 = SimpleWhere.createAnd();
                filterPK(row, targetPK);

                new Statement().where(where1).prepare(
                        this.targetAccessor.getDatabase().getConnection(),
                        "delete from ??");
            }

            return 1;
        }
        catch (SQLException ex) {
            throw ex;
        }
    }

    protected Map<String, Object> filterPK(Map<String, Object> data, List<MColumnType> columns) throws SQLException {
        LinkedHashMap<String, Object> result = new LinkedHashMap<String, Object>();
        for (MColumnType column : columns) {
            if (!column.isPk()) {
                continue;
            }
            if (column.getSource() == null) {
                result.put(column.getValue(), data.get(column.getValue()));
            }
            else {
                result.put(column.getValue(), data.get(column.getSource()));
            }
        }
        return result;
    }

    protected List<MColumnType> findPK(String tableName, List<MColumnType> columns) {
        List<String> preDefs = new ArrayList<String>();
        if (this.factory.getTable(tableName) != null) {
            MTableType.Pk pks = this.factory.getTable(tableName).getPk();
            if (pks != null) {
                preDefs = pks.getName();
            }
        }

        ArrayList<MColumnType> pk = new ArrayList<MColumnType>();
        for (MColumnType column : columns) {
            if (column.isPk()) {
                pk.add(column);
            }
            else if (preDefs.contains(column.getSource())) {
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

    /**
     * Handle <plan>...</plan>
     *
     * @param plan
     * @param master
     * @param parentPath
     * @return
     * @throws SQLException
     */
    private boolean runPlan(PlanType plan, Map<String, Object> sourceRow, String parentPath) throws SQLException {
        // <rule>
        if (plan.getRule() != null && plan.getRule().getCriteria().size() > 0) {
            for (MCriteriaType criteria : plan.getRule().getCriteria()) {
                Object v = sourceRow.get(criteria.getColumn());
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

        SimpleWhere where = SimpleWhere.createAnd();

        // <join>
        TaskType task = this.factory.tasks.get(plan.getTaskName());
        for (MColumnType column : plan.getJoin().getColumn()) {
            if (column.getSource() == null) {
                where.eq(column.getValue(), sourceRow.get(column.getValue()));
            }
            else {
                where.eq(column.getValue(), sourceRow.get(column.getSource()));
            }
        }

        // <where> TODO:

        return runTask(task, where, parentPath);
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
