package uia.tmd.zztop;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import uia.tmd.zztop.cmd.SyncCmd;

public final class ZztopCLIOld {

    private static final Logger LOGGER = LogManager.getLogger(ZztopCLIOld.class);

    public static void main(String[] args) {
        try {
            LOGGER.info("=============== ZZTOP CLI ===============");
            ZztopEnv.initial();
            
            if(args == null || args.length == 0 || args.length > 2) {
            	System.out.println("1. add {JOB_NAME} argument which defined in conf/tmd_plans.xml.");
            	System.out.println("   example: java -jar tmd-zztop.jar SO_DAILY_SYNC");
            	System.out.println("2. add {FILENAME} {JOB_NAME} arguments which defined in {FILENAME} XML file.");
            	System.out.println("   example: java -jar tmd-zztop.jar conf/HTKS.xml SO_DAILY_SYNC");
            	return;
            }
            
            String filePath = "conf/tmd_plans.xml";
            String jobName = null;
            
            if(args.length == 1) {
            	jobName = args[0];
            }
            else if(args.length == 2) {
            	filePath = args[0];
            	jobName = args[1];
            }
            else {
	            System.out.println("arguments not match");
            	System.exit(0);
            }
            
            LOGGER.info("file: " + filePath);
            LOGGER.info("job : " + jobName);

            SyncCmd cmd = new SyncCmd();
            long t1 = System.currentTimeMillis();
            cmd.run(filePath, jobName, null);
            long t2 = System.currentTimeMillis();
            System.out.println(t2 - t1 + "ms");
        }
        catch (Exception ex) {
            LOGGER.error("broken", ex);
        }
        System.exit(0);
    }
}
