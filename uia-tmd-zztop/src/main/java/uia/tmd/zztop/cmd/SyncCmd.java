package uia.tmd.zztop.cmd;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TreeMap;

import org.apache.commons.cli.CommandLine;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import uia.tmd.JobListener;
import uia.tmd.JobRunner;
import uia.tmd.TaskFactory;
import uia.tmd.TaskListener;
import uia.tmd.zztop.ZztopCmd;
import uia.tmd.zztop.DB;
import uia.tmd.zztop.db.ExecJob;
import uia.tmd.zztop.db.ExecTask;
import uia.tmd.zztop.db.TxKey;
import uia.tmd.zztop.db.TxTable;
import uia.tmd.zztop.db.dao.ExecJobDao;
import uia.tmd.zztop.db.dao.ExecTaskDao;
import uia.tmd.zztop.db.dao.TxKeyDao;
import uia.tmd.zztop.db.dao.TxTableDao;

public class SyncCmd implements ZztopCmd, JobListener, TaskListener {

    private static final Logger LOGGER = LogManager.getLogger(SyncCmd.class);

    private ExecJob execJob;

    private TreeMap<String, String> pathKeys;

    private ExecJobDao execJobDao;

    private ExecTaskDao taskLogDao; 

    private TxTableDao txTableDao;

    private TxKeyDao txKeyDao;
    
    private boolean saveTxKey;

    public SyncCmd() {
    }

    public boolean run(String filePath, String jobName, String sqlWhere) {
        if(filePath == null || !new File(filePath).exists()) {
        	LOGGER.error("sync> file:  " + filePath + " not found");
        	return false; 
        }
    	
        if(jobName == null || jobName.trim().length() == 0) {
        	LOGGER.error("sync> job:   missing");
        	return false; 
        }

        LOGGER.info("sync> file:  " + filePath);
    	LOGGER.info("sync> job:   " + jobName);
    	LOGGER.info("sync> where: " + sqlWhere);
    	
        try(Connection conn = DB.create()) {
	        this.pathKeys = new TreeMap<String, String>();
	
	        this.execJobDao = new ExecJobDao(conn);
	        this.taskLogDao = new ExecTaskDao(conn);
	        this.txTableDao = new TxTableDao(conn);
	        this.txKeyDao = new TxKeyDao(conn);
	
	        TaskFactory factory = new TaskFactory(new File(filePath));
	
	        JobRunner runner = factory.createRunner(jobName);
	        if(runner == null) {
		    	LOGGER.error("sync> job runner not found");
		    	return false;
	        }

	        // TODO: redesign
	        this.saveTxKey = !runner.isSourceDelete();
	
	        this.execJob = new ExecJob();
	        this.execJob.setTmdJobBo(runner.getJobName());
	        this.execJob.setDatabaseSource(runner.getDatabaseSource());
	        this.execJob.setDatabaseTarget(runner.getDatabaseTarget());
            this.execJobDao.insert(this.execJob);
        	LOGGER.info("sync> EXEC_JOB id: " + this.execJob.getId());

	        runner.addJobListener(this);
	        runner.addTaskListener(this);
	        runner.run(sqlWhere);
	        return true;
	    }
        catch(Exception ex) {
        	LOGGER.error("sync> failed", ex);
	        return false;
        }
    }

	@Override
	public boolean run(CommandLine cl) throws Exception {
		// filePath
		String filePath = cl.getOptionValue("file");
        if(filePath == null) {
        	filePath = "conf/tmd_plans.xml";
        }
        // job name
        String jobName = cl.getOptionValue("job");
        
        // where statement
        String where = cl.hasOption("where") ? cl.getOptionValue("where") : null;
        
        // run
        return run(filePath, jobName, where);
	}

    @Override
    public void itemBegin(JobRunner jobRunner, JobEvent event) {
        try {
            TxTable txTable = new TxTable();
            txTable.setTxTime(jobRunner.getTxTime());
            txTable.setTableName(event.tableName);
            this.txTableDao.insert(txTable);
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void itemEnd(JobRunner executor, JobEvent event) {

    }

    @Override
    public void sourceSelected(JobRunner jobRunner, TaskEvent evt) {
        Date txTime = new Date();
        LOGGER.info("sync> " + jobRunner.getJobName() + "> " + evt);

        ExecTask data = new ExecTask();
        data.setTmdTaskBo(evt.taskName);
        data.setExecJobBo(this.execJob.getId());
        data.setTableName(evt.tableName);
        data.setSqlWhere(evt.sql);
        data.setTriggeredBy(this.pathKeys.get(evt.parentPath));
        data.setResultCount(evt.count);
        data.setTaskPath(evt.getPath());

        if (this.pathKeys.isEmpty()) {
            this.execJob.setTmdTaskLogBo(data.getId());
        }
        this.pathKeys.put(evt.getPath(), data.getId());

        try {
            this.taskLogDao.insert(data);
            if(this.saveTxKey) {
            	ArrayList<TxKey> keys = new ArrayList<TxKey>();
	            evt.keys.forEach(k -> {
	                TxKey key = new TxKey();
	                key.setId(k);
	                key.setTxTime(txTime);
	                key.setTableName(evt.tableName);
	                keys.add(key);
	            });
	            this.txKeyDao.delete(evt.keys);
	            this.txKeyDao.insert(keys);
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void sourceDeleted(JobRunner jobRunner, TaskEvent evt) {

    }

    @Override
    public void targetInserted(JobRunner jobRunner, TaskEvent evt) {
    }

    @Override
    public void targetDeleted(JobRunner jobRunner, TaskEvent evt) {

    }

    @Override
    public void failed(JobRunner jobRunner, TaskEvent evt, SQLException ex) {
        try {
            this.execJob.setExecutedTime(new Date());
            this.execJob.setExecutedResult("FAILED");
            this.execJob.setRunState("FAILED");
            this.execJobDao.update(this.execJob);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void done(JobRunner jobRunner) {
        try {
            Calendar cal = Calendar.getInstance();
            cal.setTime(this.execJob.getExecutedDate());
            cal.add(Calendar.MONTH, 3);

            this.execJob.setExecutedTime(new Date());
            this.execJob.setExecutedResult("DONE");
            this.execJob.setRunState("WAIT_CONFIRM");
            this.execJob.setDeleteAfter(cal.getTime());
            this.execJobDao.update(this.execJob);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
