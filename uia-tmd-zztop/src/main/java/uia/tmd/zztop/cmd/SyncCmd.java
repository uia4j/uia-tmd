package uia.tmd.zztop.cmd;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uia.dao.DaoSession;
import uia.tmd.JobListener;
import uia.tmd.JobRunner;
import uia.tmd.TaskFactory;
import uia.tmd.TaskListener;
import uia.tmd.zztop.ZztopCmd;
import uia.tmd.zztop.db.ExecJob;
import uia.tmd.zztop.db.ExecTask;
import uia.tmd.zztop.db.TxKey;
import uia.tmd.zztop.db.TxTable;
import uia.tmd.zztop.db.conf.ZZTOP;
import uia.tmd.zztop.db.dao.ExecJobDao;
import uia.tmd.zztop.db.dao.ExecTaskDao;
import uia.tmd.zztop.db.dao.TxKeyDao;
import uia.tmd.zztop.db.dao.TxTableDao;

public class SyncCmd implements ZztopCmd, JobListener, TaskListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(SyncCmd.class);

    private ExecJob execJob;

    private TreeMap<String, String> pathKeys;

    private ExecJobDao execJobDao;

    private ExecTaskDao taskLogDao; 

    private TxTableDao txTableDao;

    private TxKeyDao txKeyDao;
    
    private boolean saveTxKey;
    
    public static CommandLine parse(String file, String jobName, String jobDescription, String where) {
		String[] args = new String[] { 
				"-f", file, 
				"-j", jobName, 
				"-d", jobDescription,
				"-w", where
		};
		
        try {
			return new DefaultParser().parse(arguments(), args);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
    }
    
    public static Options arguments() {
        Option file = Option.builder("f")
                .longOpt("file")
                .desc("database XML file.")
                .required()
                .hasArg()
                .argName("file")
                .build();

        Option job = Option.builder("j")
				.longOpt("job")
				.desc("job name defined in the XML.")
				.required()
				.hasArg()
				.argName("job")
				.build();
        
        Option desc = Option.builder("d")
				.longOpt("desc")
                .desc("job description.")
				.required()
                .hasArg()
                .argName("desc")
                .build();

        Option where = Option.builder("w")
                .longOpt("where")
                .desc("extra where statement.")
                .hasArg()
                .argName("where")
                .build();

        return new Options()
                .addOption(file)
                .addOption(job)
                .addOption(desc)
                .addOption(where);
    }
    
    public boolean run(InputStream filePath, String jobName, String jobDescription, String sqlWhere) {
        if(jobName == null || jobName.trim().length() == 0) {
        	return false; 
        }

    	LOGGER.info("sync> job:   " + jobName);
    	LOGGER.info("sync> where: " + sqlWhere);
    	
        try (DaoSession session = ZZTOP.env().createSession()) {
	        this.pathKeys = new TreeMap<String, String>();
	
	        this.execJobDao = session.tableDao(ExecJobDao.class);
	        this.taskLogDao = session.tableDao(ExecTaskDao.class);
	        this.txTableDao = session.tableDao(TxTableDao.class);
	        this.txKeyDao = session.tableDao(TxKeyDao.class);
	
	        TaskFactory factory = new TaskFactory(filePath);
	
	        JobRunner runner = factory.createRunner(jobName);
	        if(runner == null) {
		    	LOGGER.error("sync> JobRunner not found");
		    	return false;
	        }

	        // TODO: redesign
	        this.saveTxKey = !runner.isSourceDelete();
	
	        this.execJob = new ExecJob();
	        this.execJob.setTmdJobBo(runner.getJobName());
	        this.execJob.setTmdTaskLogBo(jobDescription);
	        this.execJob.setDatabaseSource(runner.getDatabaseSource());
	        this.execJob.setDatabaseTarget(runner.getDatabaseTarget());
            this.execJobDao.insert(this.execJob);
        	LOGGER.info("sync> EXEC_JOB id: " + this.execJob.getId());
        	
	        runner.addJobListener(this);
	        runner.addTaskListener(this);
	        return runner.run(sqlWhere);
	    }
        catch(Exception ex) {
        	LOGGER.error("sync> failed", ex);
	        return false;
        }
    }    

    public boolean run(String filePath, String jobName, String jobDescription, String sqlWhere) {
        if(filePath == null || !new File(filePath).exists()) {
        	LOGGER.error("sync> file:  " + filePath + " not found");
        	return false; 
        }
    	
        if(jobName == null || jobName.trim().length() == 0) {
        	return false; 
        }

        LOGGER.info("sync> file:  " + filePath);
    	LOGGER.info("sync> job:   " + jobName);
    	LOGGER.info("sync> where: " + sqlWhere);
    	
        this.execJob = new ExecJob();
        try (DaoSession session = ZZTOP.env().createSession()) {
	        this.pathKeys = new TreeMap<String, String>();
	
	        this.execJobDao = session.tableDao(ExecJobDao.class);
	        this.taskLogDao = session.tableDao(ExecTaskDao.class);
	        this.txTableDao = session.tableDao(TxTableDao.class);
	        this.txKeyDao = session.tableDao(TxKeyDao.class);
	
	        TaskFactory factory = new TaskFactory(new File(filePath));
	
	        JobRunner runner = factory.createRunner(jobName);
	        if(runner == null) {
	        	LOGGER.error(String.format("sync> %s> not found", jobName));
		    	return false;
	        }
        	
	        // TODO: redesign
	        this.saveTxKey = !runner.isSourceDelete();
	
	        this.execJob.setTmdJobBo(runner.getJobName());
	        this.execJob.setTmdTaskLogBo(jobDescription);
	        this.execJob.setDatabaseSource(runner.getDatabaseSource());
	        this.execJob.setDatabaseTarget(runner.getDatabaseTarget());
            this.execJobDao.insert(this.execJob);
            /**
        	LOGGER.debug(String.format("sync> %s> %s",
        			jobName,
        			this.execJob.getId()));
        	LOGGER.debug(String.format("sync> %s> %s to %s, deleteSource:%s",
        			jobName,
        			runner.getDatabaseSource(),
        			runner.getDatabaseTarget(),
        			runner.isSourceDelete()));
        	LOGGER.debug(String.format("sync> %s> extra: %s",
        			jobName,
        			sqlWhere));
        	*/

	        runner.addJobListener(this);
	        runner.addTaskListener(this);
	        return runner.run(sqlWhere);
	    }
        catch(Exception ex) {
        	LOGGER.error(String.format("sync> %s> %s failed",
        			jobName,
        			this.execJob.getId()), ex);
	        return false;
        }
    }

	@Override
	public boolean run(CommandLine cl) {
		if(cl == null) {
			return false;
		}
		
		// filePath
		String filePath = cl.getOptionValue("file");
        if(filePath == null) {
        	filePath = "conf/tmd_plans.xml";
        }
        // job name
        String jobName = cl.getOptionValue("job");
        
        // job description
        String jobDesc = cl.hasOption("desc") ? cl.getOptionValue("desc") : null;
        
        // where statement
        String where = cl.hasOption("where") ? cl.getOptionValue("where") : null;
        
        // run
        return run(filePath, jobName, jobDesc, where);
	}

    @Override
    public void sourceSelected(JobRunner jobRunner, TaskEvent evt) {
        Date txTime = new Date();
        LOGGER.debug("sync> " + jobRunner.getJobName() + "> " + evt);

        ExecTask data = new ExecTask();
        data.setTmdTaskBo(evt.taskName);
        data.setExecJobBo(this.execJob.getId());
        data.setTableName(evt.tableName);
        data.setSqlWhere(evt.sql);
        data.setTriggeredBy(this.pathKeys.get(evt.parentPath));
        data.setResultCount(evt.count);
        data.setTaskPath(evt.getPath());
        data.setGroupId(jobRunner.getTxId());
        this.pathKeys.put(evt.getPath(), data.getId());

        try {
            this.taskLogDao.insert(data);
            if(this.saveTxKey) {
            	TreeSet<TxKey> keys = new TreeSet<TxKey>();
	            evt.keys.forEach(k -> {
	                TxKey key = new TxKey();
	                key.setId(k);
	                key.setTxTime(txTime);
	                key.setTableName(evt.tableName);
	                key.setExecJobBo(this.execJob.getId());
	                keys.add(key);
	            });
	            this.txKeyDao.delete(evt.keys);
	            this.txKeyDao.insert(new ArrayList<>(keys));
            }
        }
        catch (Exception ex) {
        	LOGGER.error(String.format("%s> unique constraint violated, keys: %s", evt.tableName, evt.keys), ex);
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
    public void taskFailed(JobRunner jobRunner, TaskEvent evt, Exception ex) {
    }

    @Override
    public void taskDone(JobRunner jobRunner) {
    }

    @Override
    public void jobItemBegin(JobRunner jobRunner, JobEvent event) throws Exception {
        TxTable txTable = new TxTable();
        txTable.setTxTime(jobRunner.getTxTime());
        txTable.setTableName(event.tableName);
        txTable.setExecJobBo(this.execJob.getId());
        this.txTableDao.insert(txTable);
    }

    @Override
    public void jobItemEnd(JobRunner executor, JobEvent event) throws Exception {

    }

    @Override
    public void jobFailed(JobRunner jobRunner, Exception ex) {
        try {
            Calendar cal = Calendar.getInstance();
            cal.setTime(this.execJob.getExecutedDate());
            cal.add(Calendar.MONTH, 3);

            this.execJob.setExecutedTime(new Date());
            this.execJob.setExecutedResult("FAILED");
            this.execJob.setRunState("FAILED");
            this.execJob.setDeleteAfter(cal.getTime());
            this.execJobDao.update(this.execJob);
        }
        catch (Exception e) {
        	LOGGER.error("SyncCmd> ", e);
        }
    }

    @Override
    public void jobDone(JobRunner jobRunner) {
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
        catch (Exception e) {
        	LOGGER.info("");
        	LOGGER.error("SyncCmd> ", e);
        }
    }
}
