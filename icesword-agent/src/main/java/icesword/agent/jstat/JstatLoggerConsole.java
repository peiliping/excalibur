package icesword.agent.jstat;

import icesword.agent.data.process.JvmItem;

import java.util.concurrent.atomic.AtomicBoolean;

import sun.jvmstat.monitor.MonitorException;
import sun.jvmstat.monitor.MonitoredVm;
import sun.tools.jstat.OptionFormat;
import sun.tools.jstat.OptionOutputFormatterEx;

public class JstatLoggerConsole extends JstatLogger {

    public JstatLoggerConsole(JvmItem item, OptionFormat format, MonitoredVm monitoredVm) {
        super(item, format, monitoredVm);
        try {
            this.formatter = new OptionOutputFormatterEx(monitoredVm, format, false);
        } catch (MonitorException e) {
            e.printStackTrace();
        }
    }

    private static AtomicBoolean printedHeader = new AtomicBoolean(false);

    private static final String  DELIMITER     = "\t";

    @Override
    public void logSamples() {
        try {
            if (!printedHeader.get() && printedHeader.compareAndSet(false, true)) {
                System.out.println("PID" + DELIMITER + formatter.getHeader());
            }

            while (active) {
                String row;
                row = formatter.getRow();
                System.out.println(item.pid + DELIMITER + row);
                sleep();
            }
        } catch (MonitorException e) {
            e.printStackTrace();
        }
    }
}
