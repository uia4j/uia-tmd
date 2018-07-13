package uia.tmd;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import uia.tmd.JobListener.ExecutorEvent;
import uia.tmd.TaskListener.TaskEvent;
import uia.tmd.model.xml.DatabaseType;
import uia.tmd.model.xml.ItemType;
import uia.tmd.model.xml.JobType;
import uia.tmd.model.xml.TaskType;

/**
 * Task executor.
 *
 * @author Kyle K. Lin
 *
 */
public final class JobRunner {

    final DataAccess sourceAccess;

    final DataAccess targetAccess;

    final TxPool txPool;

    final TaskFactory factory;

    private final Date txTime;

    private final JobType jobType;

    private final DatabaseType sourceType;

    private final DatabaseType targetType;

    private final ArrayList<JobListener> executorListeners;

    private final ArrayList<TaskListener> taskListeners;

    public JobRunner(TaskFactory factory, JobType jobType) throws Exception {
        this.factory = factory;
        this.txTime = new Date();
        this.jobType = jobType;
        this.sourceType = this.factory.getDatabase(jobType.getSource());
        this.targetType = this.factory.getDatabase(jobType.getTarget());

        this.sourceAccess = (DataAccess) Class.forName(this.sourceType.getDriverClass()).newInstance();
        this.targetAccess = (DataAccess) Class.forName(this.targetType.getDriverClass()).newInstance();
        this.sourceAccess.initial(this.sourceType, factory.getTables());
        this.targetAccess.initial(this.targetType, factory.getTables());

        this.executorListeners = new ArrayList<JobListener>();
        this.taskListeners = new ArrayList<TaskListener>();
        this.txPool = new TxPool();
    }

    public Date getTxTime() {
        return this.txTime;
    }

    public String getExecutorName() {
        return this.jobType.getName();
    }

    public String getDatabaseSource() {
        return this.sourceType.getDbName();
    }

    public String getDatabaseTarget() {
        return this.targetType.getDbName();
    }

    public boolean isSourceDelete() {
        return this.jobType.isSourceDelete();
    }

    public void addExecutorListener(JobListener listener) {
        if (listener == null) {
            return;
        }
        if (!this.executorListeners.contains(listener)) {
            this.executorListeners.add(listener);
        }
    }

    public void addTaskListener(TaskListener listener) {
        if (listener == null) {
            return;
        }
        if (!this.taskListeners.contains(listener)) {
            this.taskListeners.add(listener);
        }
    }

    public boolean run(String where) throws Exception {
        try {
            this.sourceAccess.connect();
            if (this.targetType != null) {  // TODO: good?
                this.targetAccess.connect();
            }

            boolean success = true;
            for (ItemType itemType : this.jobType.getItem()) {
                TaskType taskType = this.factory.getTask(itemType.getTaskName());
                String tableName = taskType.getSourceSelect().getTable();
                TaskRunner taskRunner = new TaskRunner(this);
                if (itemType.getDriverName() != null) {
                    ItemRunner item = (ItemRunner) Class.forName(itemType.getDriverName()).newInstance();
                    String criteria = item.prepare(this, itemType, taskType);

                    raiseItemBegin(new ExecutorEvent(itemType.getTaskName(), tableName, criteria));
                    item.run(this, itemType, taskType, taskRunner);
                    raiseItemEnd(new ExecutorEvent(itemType.getTaskName(), tableName, criteria));
                }
                else {
                    raiseItemBegin(new ExecutorEvent(itemType.getTaskName(), tableName, where));
                    taskRunner.run(taskType, "/", where, null);
                    raiseItemEnd(new ExecutorEvent(itemType.getTaskName(), tableName, where));
                }
            }

            if (success) {
                this.txPool.commitInsert(this.targetAccess);
                this.txPool.commitDelete(this.sourceAccess);
                raiseDone();
            }

            return success;
        }
        finally {
            try {
                this.sourceAccess.disconnect();
                this.targetAccess.disconnect();
            }
            catch (Exception ex) {

            }
        }
    }

    public boolean run(String where, List<Object> paramValues) throws Exception {
        try {
            this.sourceAccess.connect();
            if (this.targetType != null) {  // TODO: good?
                this.targetAccess.connect();
            }

            boolean success = true;
            for (ItemType itemType : this.jobType.getItem()) {
                TaskType taskType = this.factory.getTask(itemType.getTaskName());
                TaskRunner task = new TaskRunner(this);
                if (itemType.getDriverName() != null) {
                    ItemRunner item = (ItemRunner) Class.forName(itemType.getDriverName()).newInstance();
                    item.prepare(this, itemType, taskType);
                    item.run(this, itemType, taskType, task);
                }
                else {
                    task.run(taskType, "/", where, paramValues, null);
                }
            }

            if (success) {
                this.txPool.commitInsert(this.targetAccess);
                this.txPool.commitDelete(this.sourceAccess);
                raiseDone();
            }

            return success;
        }
        finally {
            try {
                this.sourceAccess.disconnect();
                this.targetAccess.disconnect();
            }
            catch (Exception ex) {

            }
        }
    }

    void raiseItemBegin(ExecutorEvent event) {
        for (JobListener l : this.executorListeners) {
            try {
                l.itemBegin(this, event);
            }
            catch (Exception ex2) {

            }
        }
    }

    void raiseItemEnd(ExecutorEvent event) {
        for (JobListener l : this.executorListeners) {
            try {
                l.itemEnd(this, event);
            }
            catch (Exception ex2) {

            }
        }
    }

    void raiseSourceSelected(TaskEvent evt) {
        for (TaskListener l : this.taskListeners) {
            try {
                l.sourceSelected(this, evt);
            }
            catch (Exception ex2) {

            }
        }
    }

    void raiseSourceDeleted(TaskEvent evt) {
        for (TaskListener l : this.taskListeners) {
            try {
                l.sourceDeleted(this, evt);
            }
            catch (Exception ex2) {

            }
        }
    }

    void raiseTargetInserted(TaskEvent evt) {
        for (TaskListener l : this.taskListeners) {
            try {
                l.targetInserted(this, evt);
            }
            catch (Exception ex2) {

            }
        }
    }

    void raiseTargetDeleted(TaskEvent evt) {
        for (TaskListener l : this.taskListeners) {
            try {
                l.targetDeleted(this, evt);
            }
            catch (Exception ex2) {

            }
        }
    }

    void raiseExecuteFailure(TaskEvent evt, SQLException ex) {
        for (TaskListener l : this.taskListeners) {
            try {
                l.executeFailure(this, evt, ex);
            }
            catch (Exception ex2) {

            }
        }
    }

    void raiseDone() {
        for (TaskListener l : this.taskListeners) {
            try {
                l.done(this);
            }
            catch (Exception ex2) {

            }
        }
    }
}
