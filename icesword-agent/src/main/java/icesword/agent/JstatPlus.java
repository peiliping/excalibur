package icesword.agent;

import icesword.agent.data.process.JvmItem;
import icesword.agent.service.JpsMonitorService;
import icesword.agent.service.JstatMonitorService;

import java.util.List;

public class JstatPlus {

    private static JstatMonitorService jstatPool = new JstatMonitorService();

    public static void main(String[] args) throws InterruptedException {
        while (true) {
            List<JvmItem> jvmList = JpsMonitorService.findWorkerJVM(getMainClassKeyWord(args));
            jstatPool.addJVMs(jvmList, 1000);
            jstatPool.cleanDoneFuture();
            Thread.sleep(60 * 1000);
        }
    }

    private static String getMainClassKeyWord(String[] args) {
        return ((args != null && args.length == 1) ? args[0].trim() : null);
    }
}
