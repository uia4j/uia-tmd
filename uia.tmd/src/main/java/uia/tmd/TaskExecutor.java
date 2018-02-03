package uia.tmd;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import uia.tmd.TaskExecutorListener.TaskExecutorEvent;
import uia.tmd.TaskExecutorListener.TaskExecutorEvent.OperationType;
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

    protected final TaskType rootTask;

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
        this.rootTask = this.factory.tasks.get(executor.getTask());
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
        this.rootTask = this.factory.tasks.get(executor.getTask());
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

    public boolean run(Where[] wheres) throws SQLException {
        this.tableRows.clear();
        try {
            this.sourceAccessor.connect();
            if (this.target != null) {  // TODO: good?
                this.targetAccessor.connect();
            }
            if (runTask(this.rootTask, wheres, "/")) {
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

    public boolean run(String where) throws SQLException {
        this.tableRows.clear();
        try {
            this.sourceAccessor.connect();
            if (this.target != null) {  // TODO: good?
                this.targetAccessor.connect();
            }
            if (runTask(this.rootTask, where, "/")) {
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

    protected abstract boolean runTask(TaskType task, Where[] wheres, String parentPath) throws SQLException;

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

    void printRunLog() {
        for (Map.Entry<String, List<String>> e : this.tableRows.entrySet()) {
            System.out.println(e.getKey() + "(" + e.getValue().size() + ")");
            for (String key : e.getValue()) {
                System.out.println("  " + key);
            }
        }
    }

}
