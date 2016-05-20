package icesword.agent.jstat;

import icesword.agent.data.process.JvmItem;
import icesword.agent.service.JpsMonitorService;

import java.util.List;

import org.apache.commons.cli.CommandLine;

public class JstatPlusOffline extends JstatPlus {

    public JstatPlusOffline(CommandLine commandLine) {
        super(commandLine);
    }

    @Override
    public void run() {
        List<JvmItem> jvmList = JpsMonitorService.findWorkerJVM(null, jstatPool);
        jstatPool.addJVMs(jvmList, super.monitorIntervel);
        jstatPool.cleanDoneFuture();
    }

}
