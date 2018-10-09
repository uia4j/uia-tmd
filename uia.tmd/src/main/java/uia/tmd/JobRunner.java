package uia.tmd;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import uia.tmd.ItemRunner.WhereType;
import uia.tmd.JobListener.JobEvent;
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

    private final ArrayList<JobListener> jobListeners;

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

        this.jobListeners = new ArrayList<JobListener>();
        this.taskListeners = new ArrayList<TaskListener>();
        this.txPool = new TxPool(jobType.getName());
    }
    
    public void commit() throws SQLException {
    	this.txPool.commitInsert(this.targetAccess);
    	this.txPool.commitDelete(this.sourceAccess);
    	this.txPool.clear();
    }

    public List<String> pkInSource(String tableName) throws SQLException {
        return this.sourceAccess.prepareTable(tableName)
                .selectPk()
                .stream()
                .map(c -> c.getColumnName())
                .collect(Collectors.toList());
    }

    public List<String> pkInTarget(String tableName) throws SQLException {
        return this.targetAccess.prepareTable(tableName)
                .selectPk()
                .stream()
                .map(c -> c.getColumnName())
                .collect(Collectors.toList());
    }

    public Date getTxTime() {
        return this.txTime;
    }

    public String getJobName() {
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

    public void addJobListener(JobListener listener) {
        if (listener == null) {
            return;
        }
        if (!this.jobListeners.contains(listener)) {
            this.jobListeners.add(listener);
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
                if (!itemType.isEnabled()) {
                    continue;
                }

                ItemRunner itemRunner;
                if (itemType.getDriverName() != null) {
                    itemRunner = (ItemRunner) Class.forName(itemType.getDriverName()).newInstance();
                }
                else if (this.jobType.getItemRunner() != null) {
                    itemRunner = (ItemRunner) Class.forName(this.jobType.getItemRunner()).newInstance();
                }
                else {
                    itemRunner = new ItemRunnerImpl();
                }

                TaskType taskType = this.factory.getTask(itemType.getTaskName());
                String tableName = taskType.getSourceSelect().getTable();
                WhereType whereType = itemRunner.prepare(this, itemType, taskType, where);

                TaskRunner taskRunner = new TaskRunner(this);
                raiseItemBegin(new JobEvent(itemType.getTaskName(), tableName, whereType.toString()));
                itemRunner.run(this, itemType, taskType, taskRunner, whereType);
                raiseItemEnd(new JobEvent(itemType.getTaskName(), tableName, whereType.toString()));
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
    
    @Override
    public String toString() {
    	return this.jobType.getName() +", items:" + this.jobType.getItem().size();
    }

    void raiseItemBegin(JobEvent event) {
        for (JobListener l : this.jobListeners) {
            try {
                l.itemBegin(this, event);
            }
            catch (Exception ex2) {

            }
        }
    }

    void raiseItemEnd(JobEvent event) {
        for (JobListener l : this.jobListeners) {
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

    void raiseJobFailure(TaskEvent evt, SQLException ex) {
        for (TaskListener l : this.taskListeners) {
            try {
                l.failed(this, evt, ex);
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
