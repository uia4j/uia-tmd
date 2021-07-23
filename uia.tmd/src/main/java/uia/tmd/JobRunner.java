package uia.tmd;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private static final Logger LOGGER = LoggerFactory.getLogger(JobRunner.class);
    
    final DataAccess sourceAccess;

    final DataAccess targetAccess;

    final TxPool txPool;

    final TaskFactory factory;
    
    private String txId;

    private final Date txTime;

    private final JobType jobType;

    private final DatabaseType sourceType;

    private final DatabaseType targetType;

    private final ArrayList<JobListener> jobListeners;

    private final ArrayList<TaskListener> taskListeners;
    
    public JobRunner(TaskFactory factory, JobType jobType) throws Exception {
    	this.txId = "" + System.currentTimeMillis();
        this.txTime = new Date();
        this.txPool = new TxPool(jobType.getName());
        this.jobListeners = new ArrayList<JobListener>();
        this.taskListeners = new ArrayList<TaskListener>();

        this.factory = factory;
        this.jobType = jobType;
        this.sourceType = this.factory.getDatabase(jobType.getSource());
        this.targetType = this.factory.getDatabase(jobType.getTarget());
        this.sourceAccess = (DataAccess) Class.forName(this.sourceType.getDriverClass()).newInstance();
        this.targetAccess = (DataAccess) Class.forName(this.targetType.getDriverClass()).newInstance();

        this.sourceAccess.initial(this.sourceType, factory.getTables());
        this.targetAccess.initial(this.targetType, factory.getTables());
    }

    private JobRunner(String txId, TaskFactory factory, JobType jobType, DatabaseType sourceType, DatabaseType targetType, DataAccess sourceAccess, DataAccess targetAccess) {
    	this.txId = txId;
        this.txTime = new Date();
        this.txPool = new TxPool(jobType.getName());
        this.jobListeners = new ArrayList<JobListener>();
        this.taskListeners = new ArrayList<TaskListener>();

        this.factory = factory;
        this.jobType = jobType;
        this.sourceType = sourceType;
        this.targetType = targetType;
        this.sourceAccess = sourceAccess;
        this.targetAccess = targetAccess;
    }
    
    public JobRunner copy(String txId) throws Exception {
    	return new JobRunner(
    			txId,
    			this.factory, 
    			this.jobType,
    			this.sourceType,
    			this.targetType,
    			this.sourceAccess,
    			this.targetAccess);
    }
    
    public String getTxId() {
		return this.txId;
	}
    
    public void setTxId(String txId) {
    	this.txId = txId;
    }

	public void commit() throws SQLException {
    	this.txPool.commitInsert(this.targetAccess);
    	this.txPool.commitDelete(this.sourceAccess);
    	this.txPool.done(this.txId);
    	this.txId = "" + System.currentTimeMillis();
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

                final JobEvent event = new JobEvent(itemType.getTaskName(), tableName, whereType.toString());

                TaskRunner taskRunner = new TaskRunner(this);
                raiseItemBegin(event);
                itemRunner.run(this, itemType, taskType, taskRunner, whereType);
                raiseItemEnd(event);
            }

            this.txPool.commitInsert(this.targetAccess);
            this.txPool.commitDelete(this.sourceAccess);
            raiseJobDone();

            return true;
        }
        catch(Exception ex) {
        	LOGGER.error("jobRunner> ", ex);
        	raiseJobFailed(ex);
        	return false;
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
                l.jobItemBegin(this, event);
            }
            catch (Exception ex2) {

            }
        }
    }

    void raiseItemEnd(JobEvent event) {
        for (JobListener l : this.jobListeners) {
            try {
                l.jobItemEnd(this, event);
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

    void raiseJobFailed(Exception ex) {
        for (JobListener l : this.jobListeners) {
            try {
                l.jobFailed(this, ex);
            }
            catch (Exception ex2) {

            }
        }
    }

    void raiseJobDone() {
        for (JobListener l : this.jobListeners) {
            try {
                l.jobDone(this);
            }
            catch (Exception ex2) {

            }
        }
    }
}
