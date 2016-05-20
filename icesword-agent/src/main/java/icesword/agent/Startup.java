package icesword.agent;

import icesword.agent.data.process.JvmItem;
import icesword.agent.service.ConfigService;
import icesword.agent.service.JpsMonitorService;
import icesword.agent.service.JstatMonitorService;
import icesword.agent.util.Mode;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;

import com.alibaba.fastjson.JSON;

public class Startup {

    public static final String         AGENT_VERSION    = "1.0";

    public static Mode                 MODE             = null;

    public static AtomicLong           POLL_INTERVEL    = new AtomicLong(60 * 1000);
    public static AtomicBoolean        RUNNING          = new AtomicBoolean(true);

    private static long                MONITOR_INTERVAL = 1000;

    private static JstatMonitorService jstatPool        = new JstatMonitorService();

    public static void main(String[] args) throws Exception {

        CommandLineParser parser = new DefaultParser();
        Options options = (new Options()).addOption("m", "mode", true, "offline or online").addOption("r", "remoteAddress", true, "Config Server Ip .");

        CommandLine commandLine = parser.parse(options, args);
        MODE = Mode.getMode(commandLine.getOptionValue('m'));

        if (MODE == Mode.OFF_LINE) { // 单机模式
            while (RUNNING.get()) {
                List<JvmItem> jvmList = JpsMonitorService.findWorkerJVM((args.length == 0 ? null : args[1].trim()), jstatPool);
                jstatPool.addJVMs(jvmList, MONITOR_INTERVAL);
                jstatPool.cleanDoneFuture();
                Thread.sleep(POLL_INTERVEL.get());
            }
        } else if (MODE == Mode.ON_LINE) { // Diamond模式
            ConfigService cs = ConfigService.builder().configServerAddress(commandLine.getOptionValue("r")).build();
            while (RUNNING.get()) {
                cs.updateConfigAndSendEvent(AGENT_VERSION);
                System.out.println(JSON.toJSONString(cs.getConfig()));
                if (cs.getConfig() != null) {
                    if (cs.getConfig().getStatus() == 1) {
                        POLL_INTERVEL.set(cs.getConfig().getPeriod());
                        List<JvmItem> jvmList = JpsMonitorService.findWorkerJVM(null, jstatPool);
                        jstatPool.addJVMs(jvmList, MONITOR_INTERVAL * 2);
                        jstatPool.cleanDoneFuture();
                    } else if (cs.getConfig().getStatus() == 0) {
                        jstatPool.killAllAttach();
                        jstatPool.cleanDoneFuture();
                    } else if (cs.getConfig().getStatus() == -1) {
                        jstatPool.killAllAttach();
                        jstatPool.cleanDoneFuture();
                        break;
                    }
                }
                Thread.sleep(POLL_INTERVEL.get());
                cs.sendData();
            }
        }
        System.exit(0);
    }
}
