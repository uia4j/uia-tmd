package uia.tmd.zztop;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class ZztopCLI {

    private static final Logger LOGGER = LogManager.getLogger(ZztopCLI.class);

    public static void main(String[] args) {
        try {
            LOGGER.info("=============== ZZTOP CLI ===============");
            ZztopEnv.initial();
            
            if(args != null && args.length != 1) {
            	System.out.println("please add {JOB_NAME} argument which defined in conf/tmd_plans.xml.");
            	System.out.println("example: java -jar tmd-zztop.jar SO_DAILY_SYNC");
            	return;
            }

            QtzJobRunner job = new QtzJobRunner();
            long t1 = System.currentTimeMillis();
            job.run(args[0], null);
            long t2 = System.currentTimeMillis();
            System.out.println(t2 - t1 + "ms");
            
        }
        catch (Exception ex) {
            LOGGER.error("broken", ex);
        }
        System.exit(0);
    }
}
