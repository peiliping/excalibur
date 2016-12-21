package jvmmonitor.agent;

import jvmmonitor.agent.monitor.MonitorManager;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;

import java.util.Set;

/**
 * Created by peiliping on 16-12-19.
 */
public class Startup {

    public static Options OPTIONS = new Options();

    static {
        OPTIONS.addOption("i", "interval", true, "Metric MonitorManager Interval");

        OPTIONS.addOption("p", "pids", true, "filter pids");
        OPTIONS.addOption("x", "excludes", true, "excludes keyswords");

        OPTIONS.addOption("m", "modules", true, "modules name");
    }

    public static void main(String[] args) throws Exception {

        CommandLine commandLine = (new DefaultParser()).parse(OPTIONS, args);

        int interval = Integer.valueOf(commandLine.getOptionValue("i", "2000"));
        Set<Integer> targetPids = Util.parse2IntSet(commandLine.getOptionValue("p", ""));
        Set<String> excludeKeyWords = Util.parse2StringSet(commandLine.getOptionValue("x", ""));
        Set<String> modules = Util.parse2StringSet(commandLine.getOptionValue("m", ""));

        Config cfg = new Config(interval, targetPids, excludeKeyWords, modules);
        final MonitorManager monitorManager = new MonitorManager(cfg);

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                monitorManager.close(null);
            }
        });
        monitorManager.findActiveJVM(null);
        while (true) {
            monitorManager.run();
            Thread.sleep(interval);
        }
    }

}
