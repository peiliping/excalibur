package icesword.agent;

import icesword.agent.jstat.JstatPlus;
import icesword.agent.jstat.JstatPlusOffline;
import icesword.agent.jstat.JstatPlusOnline;
import icesword.agent.util.Mode;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;

public class Startup {

    public static final String AGENT_VERSION = "1.0";

    public static Mode         MODE          = null;

    public static boolean      DEBUG         = false;

    public static void main(String[] args) throws Exception {

        Options options =
                (new Options()).addOption("m", "mode", true, "offline or online").addOption("r", "remoteAddress", true, "Config Server Ip .")
                        .addOption("d", "debug", false, "Debug");
        CommandLine commandLine = (new DefaultParser()).parse(options, args);

        String debug = commandLine.getOptionValue('d');
        DEBUG = (debug != null && Boolean.valueOf(debug));

        JstatPlus jstat = null;
        MODE = Mode.getMode(commandLine.getOptionValue('m'));

        if (MODE == Mode.OFF_LINE) { // 单机模式
            jstat = new JstatPlusOffline(commandLine, 1000 * 1);
        } else if (MODE == Mode.ON_LINE) { // Diamond模式
            jstat = new JstatPlusOnline(commandLine, 1000 * 2);
        }

        jstat.run();
    }
}
