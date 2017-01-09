package jvmmonitor.agent;

import jvmmonitor.agent.monitor.MonitorManager;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.lang3.Validate;

import java.util.Set;

/**
 * Created by peiliping on 16-12-19.
 */
public class Startup {

    public static final Options OPTIONS = new Options();

    private static final long DEFAULT_SLEEP_TIME = 100;

    static {
        OPTIONS.addOption("i", "interval", true, "Metric MonitorManager Interval");
        OPTIONS.addOption("t", "multiple", true, "send data after n interval");

        OPTIONS.addOption("p", "pids", true, "filter pids");
        OPTIONS.addOption("e", "excludes", true, "excludes keyswords");

        OPTIONS.addOption("m", "modules", true, "modules name");

        OPTIONS.addOption("r", "remoteIp", true, "dc ip");

        OPTIONS.addOption("d", "debug", false, "debug");

        OPTIONS.addOption("f", "positiveFlag", false, "restart agent and get flags");

        OPTIONS.addOption("M", "mode", true, "saas or test");

        OPTIONS.addOption("a", "appName", true, "custom appname 4 test mode");
        OPTIONS.addOption("R", "roleName", true, "custom rolename 4 test mode");

        OPTIONS.addOption("o", "output", false, "only print perfdata");
    }

    public static void main(String[] args) throws Exception {

        CommandLine commandLine = (new DefaultParser()).parse(OPTIONS, args);

        int interval = Integer.valueOf(commandLine.getOptionValue("i", "3000"));
        int multiple = Integer.valueOf(commandLine.getOptionValue("t", "10"));

        Set<Integer> targetPids = Util.parse2IntSet(commandLine.getOptionValue("p", ""));
        Set<String> excludeKeyWords = Util.parse2StringSet(commandLine.getOptionValue("x", ""));

        Set<String> modules = Util.parse2StringSet(commandLine.getOptionValue("m", ""));

        String remoteIp = commandLine.getOptionValue("r", "");

        boolean debug = commandLine.hasOption("d");

        boolean getFlagsWhenRestartAgent = commandLine.hasOption("f");

        String mode = commandLine.getOptionValue("M", "saas");

        String appName = commandLine.getOptionValue("a");
        String roleName = commandLine.getOptionValue("R");

        Config cfg = new Config(interval, targetPids, excludeKeyWords, modules, multiple, remoteIp, debug, getFlagsWhenRestartAgent, mode, appName, roleName);
        final MonitorManager monitorManager = new MonitorManager(cfg);

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                monitorManager.close(null);
            }
        });
        monitorManager.findActiveJVM(true, null);

        if (commandLine.hasOption("o")) {
            while (true) {
                monitorManager.printPerfData();
                Thread.sleep(1000);
            }
        }

        Validate.notBlank(remoteIp);

        long lastStart = 0;
        long sleep = 0;

        while (true) {
            lastStart = System.currentTimeMillis();
            monitorManager.run();
            sleep = (interval - System.currentTimeMillis() + lastStart);
            Thread.sleep(sleep > DEFAULT_SLEEP_TIME ? sleep : DEFAULT_SLEEP_TIME);
        }
    }

}
