package icesword.agent;

import icesword.agent.data.process.JvmItem;
import icesword.agent.service.JpsMonitorService;
import icesword.agent.service.JstatMonitorService;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class JstatPlus {

    public static final String         AGENT_VERSION    = "1.0";

    public static AtomicLong           POLL_INTERVEL    = new AtomicLong(60 * 1000);
    public static AtomicBoolean       RUNNING          = new AtomicBoolean(true);

    private static long                MONITOR_INTERVAL = 1000;

    private static JstatMonitorService jstatPool        = new JstatMonitorService();

    public static void main(String[] args) throws InterruptedException {
        if (args == null || (args.length == 2 && args[1].trim().equals("-k"))) { // 单机模式
            while (RUNNING.get()) {
                List<JvmItem> jvmList = JpsMonitorService.findWorkerJVM((args[1].trim()));
                jstatPool.addJVMs(jvmList, MONITOR_INTERVAL);
                jstatPool.cleanDoneFuture();
                Thread.sleep(POLL_INTERVEL.get());
            }
        } else if (args.length == 2 && args[1].trim().startsWith("-r")) { // Diamond模式
            while (RUNNING.get()) {
                // Update Config
                List<JvmItem> jvmList = JpsMonitorService.findWorkerJVM(null);
                jstatPool.addJVMs(jvmList, MONITOR_INTERVAL * 2);
                jstatPool.cleanDoneFuture();
                Thread.sleep(POLL_INTERVEL.get());
                // Send Data
            }
        }
        System.exit(0);
    }
}
