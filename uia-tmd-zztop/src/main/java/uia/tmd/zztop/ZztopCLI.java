package uia.tmd.zztop;

import java.util.TreeMap;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import uia.tmd.zztop.cmd.SyncCmd;

public class ZztopCLI {

    private static TreeMap<String, String> cmds;

    static {
        cmds = new TreeMap<String, String>();
        cmds.put("sync", SyncCmd.class.getName());
    }

    public static void main(String[] args) throws Exception {
        ZztopEnv.initial();

        Options options = ZztopCLI.createOptions();

        String[] _args = new String[args.length + 1];
        _args[0] = "-c";
        for(int i=0; i<args.length; i++) {
        	_args[i + 1] = args[i];
        }
        
        System.out.println();
        CommandLine cl = null;
        try {
            CommandLineParser parser = new DefaultParser();
            cl = parser.parse(options, _args);
        }
        catch (Exception ex) {
            System.out.println(ex.getMessage());
            help(options);
            System.exit(0);
        }

        String cmd = cl.getOptionValue("cmd");
        String cls = cmds.get(cmd);
        if (cls != null) {
            ZztopCmd instance = (ZztopCmd) Class.forName(cls).newInstance();
            instance.run(cl);
        }
        else {
            System.out.println(cmd + " not found");
            help(options);
        }
    }

    public static void help(Options options) {
        System.out.println();
        HelpFormatter formatter = new HelpFormatter();
        formatter.setWidth(200);
        formatter.printHelp("java -jar tmd-zztop.jar\n", options, true);
    }

    static Options createOptions() {
        Option cmd = Option.builder("c")
                .longOpt("cmd")
                .desc("command name to be executed. [sync]")
                .required()
                .hasArg()
                .argName("commnad")
                .build();

        Option file = Option.builder("f")
                .longOpt("file")
                .desc("plan XML file")
                .required()
                .hasArg()
                .argName("file")
                .build();

        Option job = Option.builder("j")
                .longOpt("job")
                .desc("job name")
                .required()
                .hasArg()
                .argName("job")
                .valueSeparator(',')
                .build();

        Options options = new Options()
                .addOption(cmd)
                .addOption(file)
                .addOption(job);

        return options;

    }
}
