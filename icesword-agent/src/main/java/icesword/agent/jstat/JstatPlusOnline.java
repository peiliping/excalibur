package icesword.agent.jstat;

import icesword.agent.data.process.Config;
import icesword.agent.data.process.JvmItem;
import icesword.agent.service.ConfigService;
import icesword.agent.service.JpsMonitorService;

import java.util.List;

import org.apache.commons.cli.CommandLine;

public class JstatPlusOnline extends JstatPlus {

    private ConfigService configService;

    public JstatPlusOnline(CommandLine commandLine, int intervel) {
        super(commandLine, intervel);
        String address = commandLine.getOptionValue("r");
        this.configService = ConfigService.builder().address(address).build();
    }

    @Override
    public void fly() {
        configService.updateConfigAndSendEvents();
        Config config = configService.getConfig();
        if (config != null) {
            if (config.getStatus() == 1) {
                coordinateIntervel.set(config.getPeriod());
                List<JvmItem> jvmList = JpsMonitorService.findWorkerJVM(jstatPool, null);
                jstatPool.addJVMs(jvmList, monitorIntervel);
            } else if (config.getStatus() == 0) {
                jstatPool.killAllAttach();
            } else if (config.getStatus() == -1) {
                jstatPool.killAllAttach();
                running.set(false);
                jstatPool.close();
            }
        }
        configService.sendData();
    }

}
