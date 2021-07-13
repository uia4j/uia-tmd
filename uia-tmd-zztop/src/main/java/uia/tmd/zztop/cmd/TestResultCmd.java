package uia.tmd.zztop.cmd;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import uia.dao.Database;
import uia.dao.hana.Hana;
import uia.tmd.zztop.ZztopCmd;

public class TestResultCmd implements ZztopCmd {

    public static Options arguments() {
        Option f = Option.builder("f")
                .longOpt("file")
                .desc("file.")
                .required()
                .hasArg()
                .argName("file")
                .build();

        return new Options()
                .addOption(f);
    }
    
	@Override
	public boolean run(CommandLine cl) {
		try {
			String path = cl.getOptionValue("f");
			List<String> sqls = Files.readAllLines(Paths.get(path));
			
			try(Hana hana = new Hana("10.160.2.23", "31015", "WIP_ARCHIVE", "WIP_ARCHIVE", "Sap12345")) {
			// try(Hana hana = new Hana("10.160.2.23", "31047", "MES", "MES", "Sap12345")) {
				Connection conn = hana.createConnection();
				Statement stat = conn.createStatement();
				for(String sql : sqls) {
					ResultSet rs = stat.executeQuery(sql);
					rs.next();
					System.out.println(String.format("%8d - %s", rs.getInt(1), sql));
				}
			}
			
			return true;
		}
		catch(Exception ex) {
			return false;
		}
	}
}
