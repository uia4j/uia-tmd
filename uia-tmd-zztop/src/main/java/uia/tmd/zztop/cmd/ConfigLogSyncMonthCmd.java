package uia.tmd.zztop.cmd;

import java.util.Calendar;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import uia.tmd.zztop.ZztopCmd;

public class ConfigLogSyncMonthCmd implements ZztopCmd {

    public static Options arguments() {
        Option y = Option.builder("y")
                .longOpt("year")
                .desc("year.")
                .required()
                .hasArg()
                .argName("year")
                .build();

        Option m = Option.builder("m")
                .longOpt("month")
                .desc("month.")
                .required()
                .hasArg()
                .argName("month")
                .build();

        Option job = Option.builder("j")
                .longOpt("job")
                .desc("job name.")
                .hasArg()
                .argName("job")
                .build();

        return new Options()
                .addOption(y)
                .addOption(m)
                .addOption(job);
    }
    
	@Override
	public boolean run(CommandLine cl) {
		int y = Integer.parseInt(cl.getOptionValue("year"));
		int m = Integer.parseInt(cl.getOptionValue("month"));
		String job = cl.getOptionValue("job");
		if(job == null || job.trim().isEmpty()) {
			job = "CONFIG_LOG_SYNC";
		}
		else {
			job = job.toUpperCase();
		}
        
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, y);
		cal.set(Calendar.MONTH, m - 1);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.add(Calendar.MONTH, 1);
		cal.add(Calendar.DATE, -1);

		int eom = cal.get(Calendar.DAY_OF_MONTH);
		for(int d = 1; d <= eom; d++) {
			try {
				String ymd = String.format("%s/%02d/%02d", y, m, d);
				CommandLine syncCL = SyncCmd.parse(
						"htks.xml", 
						job, 
						ymd, 
						"TO_VARCHAR(LOG_TIME,'YYYY/MM/DD')='" + ymd + "'");
	    		
	    		if(!new SyncCmd().run(syncCL)) {
	    			return false;
	    		}
	    		Thread.sleep(1000);
			}
			catch(Exception ex) {
				ex.printStackTrace();
			}
		}
		
		return true;
	}
}
