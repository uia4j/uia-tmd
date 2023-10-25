package uia.tmd.zztop;

import java.util.Arrays;
import java.util.TreeMap;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uia.tmd.zztop.cmd.SimpleSyncCmd;
import uia.tmd.zztop.cmd.TestResultCmd;
import uia.tmd.zztop.cmd.ZrFileDayCmd;
import uia.tmd.zztop.cmd.ConfigLogSyncDayCmd;
import uia.tmd.zztop.cmd.ConfigLogSyncMonthCmd;
import uia.tmd.zztop.cmd.SODeleteDayCmd;
import uia.tmd.zztop.cmd.SODeleteMonthCmd;
import uia.tmd.zztop.cmd.SODeleteOneCmd;
import uia.tmd.zztop.cmd.SOSyncMonthCmd;
import uia.tmd.zztop.cmd.SOSyncDayCmd;
import uia.tmd.zztop.cmd.SOSyncOneCmd;

public class ZztopCLI {
	
	private static final String VER = "0.21.0817A";

    private static final Logger LOGGER = LoggerFactory.getLogger(ZztopCLI.class);

    private static TreeMap<String, String> cmds;

    private static TreeMap<String, Options> cmdArgs;

    static {
        cmds = new TreeMap<>();
        cmds.put("sync", SimpleSyncCmd.class.getName());
        
        cmds.put("so_sync_one", SOSyncOneCmd.class.getName());
        cmds.put("so_sync_day", SOSyncDayCmd.class.getName());
        cmds.put("so_sync_month", SOSyncMonthCmd.class.getName());
        cmds.put("so_delete_one", SODeleteOneCmd.class.getName());
        cmds.put("so_delete_day", SODeleteDayCmd.class.getName());
        cmds.put("so_delete_month", SODeleteMonthCmd.class.getName());

        cmds.put("cfglog_sync_day", ConfigLogSyncDayCmd.class.getName());
        cmds.put("cfglog_sync_month", ConfigLogSyncMonthCmd.class.getName());
        
        cmds.put("zr_file_day", ZrFileDayCmd.class.getName()); 

        cmds.put("test_result", TestResultCmd.class.getName());

        cmdArgs = new TreeMap<>();
        cmdArgs.put("sync", SimpleSyncCmd.arguments());
        
        cmdArgs.put("so_sync_one", SOSyncOneCmd.arguments());
        cmdArgs.put("so_sync_day", SOSyncDayCmd.arguments());
        cmdArgs.put("so_sync_month", SOSyncMonthCmd.arguments());
        cmdArgs.put("so_delete_one", SODeleteOneCmd.arguments());
        cmdArgs.put("so_delete_day", SODeleteDayCmd.arguments());
        cmdArgs.put("so_delete_month", SODeleteMonthCmd.arguments());

        cmdArgs.put("cfglog_sync_day", ConfigLogSyncDayCmd.arguments());
        cmdArgs.put("cfglog_sync_month", ConfigLogSyncMonthCmd.arguments());

        cmdArgs.put("zr_file_day", ZrFileDayCmd.arguments());
        
        cmdArgs.put("test_result", TestResultCmd.arguments());
        
    }

    public static void main(String[] args) throws Exception {
    	// 
    	// nohup java -XX:+UseG1GC -XX:MaxGCPauseMillis=600 -Xmx4096m -jar tmd-zztop.jar so_sync_day -d 2018/04/01 &
    	// nohup java -XX:+UseG1GC -XX:MaxGCPauseMillis=600 -Xmx4096m -jar tmd-zztop.jar so_sync_one -o ShopOrderBO:1020,AAAAAA &
    	// nohup java -XX:+UseG1GC -XX:MaxGCPauseMillis=600 -Xmx4096m -jar tmd-zztop.jar so_delete_day -d 2018/04/01 &
    	ZztopEnv.initial();

    	if(args.length == 0) {
    		System.out.println("Version: " + VER);
    		return;
    	}

    	LOGGER.info("exec: " + String.join(" ", args));
    	String cmdName = args[0];
    	
    	// 1. command
        String cls = cmds.get(cmdName);
        if (cls == null) {
            System.out.println(cmdName + " not found");
            return;
        }
        ZztopCmd instance = (ZztopCmd) Class.forName(cls).newInstance();
    	
    	// 2. arguments
    	Options options = cmdArgs.get(cmdName);
        CommandLine cl = null;
        if(options != null) {
	        try {
	            cl = new DefaultParser().parse(
	            		options, 
	            		Arrays.copyOfRange(args, 1, args.length));
	        }
	        catch (Exception ex) {
	            help(cmdName, options);
	            return;
	        }
        }
    	
        // 3. execute
        long t1 = System.currentTimeMillis();
        boolean result = instance.run(cl);
        long t2 = System.currentTimeMillis();
        System.out.println(String.format("%s: %s, spend: %sms", cmdName, result, (t2 - t1)));
    }

    public static void help(String cmdName, Options options) {
        System.out.println();
        HelpFormatter formatter = new HelpFormatter();
        formatter.setWidth(200);
        formatter.printHelp(
        		String.format("java -jar tmd-zztop.jar %s", cmdName), 
        		options, 
        		true);
    }
}
