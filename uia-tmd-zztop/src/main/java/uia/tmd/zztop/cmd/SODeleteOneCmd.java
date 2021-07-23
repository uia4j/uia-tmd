package uia.tmd.zztop.cmd;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import uia.tmd.zztop.ZztopCmd;

public class SODeleteOneCmd implements ZztopCmd {

    public static Options arguments() {
        Option order = Option.builder("o")
                .longOpt("order")
                .desc("shop order.")
                .required()
                .hasArg()
                .argName("order")
                .build();

        Option job = Option.builder("j")
                .longOpt("job")
                .desc("job name.")
                .required()
                .hasArg()
                .argName("job")
                .build();

        return new Options()
                .addOption(order)
                .addOption(job);
    }
    
	@Override
	public boolean run(CommandLine cl) {
		String order = cl.getOptionValue("order");
		String job = cl.getOptionValue("job");

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
