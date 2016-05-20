package icesword.agent.jstat;

import icesword.agent.Startup;
import icesword.agent.data.process.JvmItem;
import icesword.agent.service.ConfigService;
import icesword.agent.service.JpsMonitorService;

import java.util.List;

import org.apache.commons.cli.CommandLine;

public class JstatPlusOnline extends JstatPlus {

    private ConfigService cs;

    public JstatPlusOnline(CommandLine commandLine) {
        super(commandLine);
        cs = ConfigService.builder().configServerAddress(commandLine.getOptionValue("r")).build();
    }

    @Override
    public void run() {
        cs.updateConfigAndSendEvent(Startup.AGENT_VERSION);
        if (cs.getConfig() != null) {
            if (cs.getConfig().getStatus() == 1) {
                super.coordinateIntervel.set(cs.getConfig().getPeriod());
                List<JvmItem> jvmList = JpsMonitorService.findWorkerJVM(null, jstatPool);
                jstatPool.addJVMs(jvmList, super.monitorIntervel * 2);
                jstatPool.cleanDoneFuture();
            } else if (cs.getConfig().getStatus() == 0) {
                jstatPool.killAllAttach();
                jstatPool.cleanDoneFuture();
            } else if (cs.getConfig().getStatus() == -1) {
                jstatPool.killAllAttach();
                jstatPool.cleanDoneFuture();
                super.running.set(false);
            }
        }
        cs.sendData();
    }

}
