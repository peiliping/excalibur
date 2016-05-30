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

    public static String       DEBUG_IP      = null;

    public static int          INTERVEL      = 1000;

    public static void main(String[] args) throws Exception {

        Options options =
                (new Options()).addOption("m", "mode", true, "offline or online").addOption("r", "remoteAddress", true, "config Server Ip .")
                        .addOption("d", "debug", false, "debug").addOption("p", "debugip", true, "the ip 4 debug").addOption("i", "intervel", true, "jstat intervel");
        CommandLine commandLine = (new DefaultParser()).parse(options, args);

        DEBUG = commandLine.hasOption('d');
        DEBUG_IP = commandLine.getOptionValue("p", DEBUG_IP);

        JstatPlus jstat = null;
        MODE = Mode.getMode(commandLine.getOptionValue('m'));
        INTERVEL = Integer.valueOf(commandLine.getOptionValue("i", "1000"));

        if (MODE == Mode.OFF_LINE) { // 单机模式
            jstat = new JstatPlusOffline(commandLine, INTERVEL);
        } else if (MODE == Mode.ON_LINE) { // Diamond模式
            jstat = new JstatPlusOnline(commandLine, INTERVEL * 2);
        }

        jstat.run();
        System.exit(0);
    }
}
