package uia.tmd.zztop.cmd;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import uia.tmd.zztop.ZztopCmd;

public class ZrFileDayCmd implements ZztopCmd {

    public static Options arguments() {
        Option d = Option.builder("d")
                .longOpt("date")
                .desc("date.")
                .hasArg()
                .argName("date")
                .build();

        Option offset = Option.builder("o")
                .longOpt("offset")
                .desc("offset of date")
                .hasArg()
                .argName("offset")
                .build();

        Option job = Option.builder("j")
                .longOpt("job")
                .desc("job name.")
                .hasArg()
                .argName("job")
                .build();

        return new Options()
                .addOption(d)
                .addOption(offset)
                .addOption(job);
    }
    
	@Override
	public boolean run(CommandLine cl) {
		SimpleDateFormat fmt = new SimpleDateFormat("yyyy/MM/dd");
		
		String ymd = cl.getOptionValue("date");
		String offset = cl.getOptionValue("offset");
		if(ymd == null) {
			if(offset == null) {
				offset = "600";
			}

			Calendar cal = Calendar.getInstance();
			cal.setTime(new Date());
			cal.add(Calendar.DATE, -Integer.parseInt(offset));
			ymd = fmt.format(cal.getTime());
		}
		
		String job = cl.getOptionValue("job");
		if(job == null || job.trim().isEmpty()) {
			job = "ZR_FILE_SYNC";
		}
		else {
			job = job.toUpperCase();
		}
        
		try {
			CommandLine syncCL = SyncCmd.parse(
					"htks.xml", 
					job, 
					ymd, 
					"TO_VARCHAR(UPDATED_TIME,'YYYY/MM/DD')='" + ymd + "'");
    		return new SyncCmd().run(syncCL);
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
		
		return true;
	}
}
