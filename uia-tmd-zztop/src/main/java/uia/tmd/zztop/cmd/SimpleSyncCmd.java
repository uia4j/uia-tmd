package uia.tmd.zztop.cmd;

import java.io.File;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uia.tmd.JobRunner;
import uia.tmd.TaskFactory;
import uia.tmd.TaskListener;
import uia.tmd.zztop.ZztopCmd;

public class SimpleSyncCmd implements ZztopCmd, TaskListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleSyncCmd.class);

    private int rc;

    public static CommandLine parse(String file, String jobName, String jobDescription, String where) {
		String[] args = new String[] { 
				"-f", file, 
				"-j", jobName
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
        
        return new Options()
                .addOption(file)
                .addOption(job);
    }
    

    public boolean run(String filePath, String jobName) {
        LOGGER.info("sync> file: " + filePath);

    	if(filePath == null || !new File(filePath).exists()) {
        	LOGGER.error("sync> file: " + filePath + " not found");
        	return false; 
        }
    	
    	LOGGER.info("sync> job:  " + jobName);
        if(jobName == null || jobName.trim().length() == 0) {
        	LOGGER.error("sync> job: " + jobName + " not found");
        	return false; 
        }
    	try {
            this.rc = 0;

            TaskFactory factory = new TaskFactory(new File(filePath));
            JobRunner runner = factory.createRunner(jobName);
            runner.addTaskListener(this);
            LOGGER.info(String.format("sync> execute: %s(%s)", runner.run((String) null), this.rc));
            return true;
    	}
    	catch(Exception ex) {
    		LOGGER.error("failed", ex);
    		return false;
    	}
    	
    }

	@Override
	public boolean run(CommandLine cl) {
		if(cl == null) {
    		LOGGER.error("arguments not found");
			return false;
		}
		
		// filePath
		String filePath = cl.getOptionValue("file");
        if(filePath == null) {
        	filePath = "conf/tmd_plans.xml";
        }
        // job name
        String jobName = cl.getOptionValue("job");
        
        // run
        return run(filePath, jobName);
	}

    @Override
    public void sourceSelected(JobRunner jobRunner, TaskEvent evt) {
        this.rc += evt.count;
        System.out.println(evt);
    }

    @Override
    public void sourceDeleted(JobRunner jobRunner, TaskEvent evt) {
        System.out.println(evt);
    }

    @Override
    public void targetDeleted(JobRunner jobRunner, TaskEvent evt) {
    }

    @Override
    public void targetInserted(JobRunner jobRunner, TaskEvent evt) {
    }

    @Override
    public void taskFailed(JobRunner jobRunner, TaskEvent evt, Exception ex) {
        System.out.println(evt);
        ex.printStackTrace();
    }

    @Override
    public void taskDone(JobRunner jobRunner) {
    }

}
