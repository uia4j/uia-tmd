package uia.tmd.zztop.cmd;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import uia.tmd.zztop.ZztopCmd;

public class SOSyncOneCmd implements ZztopCmd {

    public static Options arguments() {
        Option d = Option.builder("o")
                .longOpt("order")
                .desc("shop order.")
                .required()
                .hasArg()
                .argName("order")
                .build();

        Option job = Option.builder("j")
                .longOpt("job")
                .desc("job name.")
                .hasArg()
                .argName("job")
                .build();

        return new Options()
                .addOption(d)
                .addOption(job);
    }
    
	@Override
	public boolean run(CommandLine cl) {
		String order = cl.getOptionValue("order");
		String job = cl.getOptionValue("job");
		if(job == null || job.trim().isEmpty()) {
			job = "SO_SYNC";
		}
		else {
			job = job.toUpperCase();
		}
        
		try {
			CommandLine syncCL = SyncCmd.parse(
					"htks.xml", 
					job, 
					order, 
					"HANDLE='" + order + "'");
    		
    		new SyncCmd().run(syncCL);
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
		
		return true;
	}
}
