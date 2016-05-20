package icesword.agent.jstat;

import icesword.agent.data.process.JvmItem;
import icesword.agent.service.JpsMonitorService;

import java.util.List;

import org.apache.commons.cli.CommandLine;

public class JstatPlusOffline extends JstatPlus {

    public JstatPlusOffline(CommandLine commandLine, int intervel) {
        super(commandLine, intervel);
    }

    @Override
    public void fly() {
        List<JvmItem> jvmList = JpsMonitorService.findWorkerJVM(jstatPool, null);
        jstatPool.addJVMs(jvmList, monitorIntervel);
    }

}
