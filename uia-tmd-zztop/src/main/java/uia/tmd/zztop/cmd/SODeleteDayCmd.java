package uia.tmd.zztop.cmd;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import uia.tmd.zztop.ZztopCmd;

public class SODeleteDayCmd implements ZztopCmd {

    public static Options arguments() {
        Option d = Option.builder("d")
                .longOpt("date")
                .desc("date.")
                .required()
                .hasArg()
                .argName("date")
                .build();

        Option job = Option.builder("j")
                .longOpt("job")
                .desc("job name.")
                .required()
                .hasArg()
                .argName("job")
                .build();

        return new Options()
                .addOption(d)
                .addOption(job);
    }
    
	@Override
	public boolean run(CommandLine cl) {
		String ymd = cl.getOptionValue("date");
		String job = cl.getOptionValue("job");
        
		try {
			CommandLine syncCL = SyncCmd.parse(
					"htks.xml", 
					job, 
					ymd, 
					"TO_VARCHAR(ACTUAL_COMP_DATE,'YYYY/MM/DD')='" + ymd + "'");
    		
    		new SyncCmd().run(syncCL);
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
		
		return true;
	}
}
