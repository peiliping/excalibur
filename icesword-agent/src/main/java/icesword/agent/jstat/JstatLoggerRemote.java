package icesword.agent.jstat;

import icesword.agent.data.process.JstatItem;
import icesword.agent.data.process.JvmItem;
import icesword.agent.service.DataService;

import java.util.List;

import sun.jvmstat.monitor.Monitor;
import sun.jvmstat.monitor.MonitoredVm;
import sun.tools.jstat.OptionFormat;
import sun.tools.jstat.OptionOutputFormatterEx;

public class JstatLoggerRemote extends JstatLogger {

    private List<Monitor> ageTable;
    private Monitor       desiredSurvivorSize;

    public JstatLoggerRemote(JvmItem item, OptionFormat format, MonitoredVm monitoredVm) {
        super(item, format, monitoredVm);
        try {
            this.formatter = new OptionOutputFormatterEx(monitoredVm, format, true);
            this.ageTable = monitoredVm.findByPattern("sun.gc.generation.0.agetable.bytes");
            this.desiredSurvivorSize = monitoredVm.findByName("sun.gc.policy.desiredSurvivorSize");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void logSamples() {
        while (active) {
            try {
                String row = formatter.getRow();
                DataService.addData(item, new JstatItem(item, row, ageTable, desiredSurvivorSize));
            } catch (Exception e) {
                e.printStackTrace();
            }
            sleep();
        }
    }
}
