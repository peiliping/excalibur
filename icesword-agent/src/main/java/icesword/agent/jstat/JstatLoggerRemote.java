package icesword.agent.jstat;

import icesword.agent.Startup;
import icesword.agent.data.process.JstatItem;
import icesword.agent.data.process.JvmItem;
import icesword.agent.service.DataService;
import icesword.agent.util.Mode;

import java.util.List;

import sun.jvmstat.monitor.Monitor;
import sun.jvmstat.monitor.MonitorException;
import sun.tools.jstat.OutputFormatter;

public class JstatLoggerRemote extends JstatLogger {

    private List<Monitor> ageTable;
    private Monitor       desiredSurvivorSize;

    public JstatLoggerRemote(JvmItem item, long sampleInterval, List<Monitor> ageTable, Monitor desiredSurvivorSize) {
        super(item , sampleInterval);
        this.ageTable = ageTable;
        this.desiredSurvivorSize = desiredSurvivorSize;
    }

    @Override
    public void logSamples(OutputFormatter formatter) throws MonitorException {
        if (Startup.MODE == Mode.ON_LINE) {
            while (active) {
                try {
                    String row = formatter.getRow();
                    DataService.addData(item, new JstatItem(row, item, ageTable, desiredSurvivorSize));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    Thread.sleep(sampleInterval);
                } catch (Exception e) {
                };
            }
        }
    }
}
