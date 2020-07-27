package uia.tmd.zztop;

import java.util.Arrays;
import java.util.TreeMap;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

import uia.tmd.zztop.cmd.SOMSyncCmd;
import uia.tmd.zztop.cmd.SimpleSyncCmd;
import uia.tmd.zztop.cmd.SOMDeleteCmd;

public class ZztopCLI {

    private static TreeMap<String, String> cmds;

    private static TreeMap<String, Options> cmdArgs;

    static {
        cmds = new TreeMap<>();
        cmds.put("sync", SimpleSyncCmd.class.getName());
        cmds.put("so_month_sync", SOMSyncCmd.class.getName());
        cmds.put("so_month_delete", SOMDeleteCmd.class.getName());

        cmdArgs = new TreeMap<>();
        cmdArgs.put("sync", SimpleSyncCmd.arguments());
        cmdArgs.put("so_month_sync", SOMSyncCmd.arguments());
        cmdArgs.put("so_month_delete", SOMDeleteCmd.arguments());
        
    }

    public static void main(String[] args) throws Exception {
    	// nohup java -jar tmd-zztop.jar so_month_delete -y 2019 -m 3 &
    	// nohup java -jar tmd-zztop.jar so_month_sync -y 2019 -m 3 &
    	ZztopEnv.initial();

    	if(args.length == 0) {
    		System.out.println("No command");
    		return;
    	}

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
