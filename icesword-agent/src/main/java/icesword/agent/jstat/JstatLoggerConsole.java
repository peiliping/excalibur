package icesword.agent.jstat;

import icesword.agent.data.process.JvmItem;

import java.util.concurrent.atomic.AtomicBoolean;

import sun.jvmstat.monitor.MonitorException;
import sun.tools.jstat.OutputFormatter;

public class JstatLoggerConsole extends JstatLogger {

    public JstatLoggerConsole(JvmItem item, long sampleInterval) {
        super(item, sampleInterval);
    }

    private static AtomicBoolean printedHeader = new AtomicBoolean(false);

    private static final String  DELIMITER     = "\t";

    @Override
    public void logSamples(OutputFormatter formatter) throws MonitorException {

        if (!printedHeader.get() && printedHeader.compareAndSet(false, true)) {
            System.out.println("PID" + DELIMITER + formatter.getHeader());
        }

        while (active) {
            String row = formatter.getRow();
            System.out.println(item.pid + DELIMITER + row);
            try {
                Thread.sleep(sampleInterval);
            } catch (Exception e) {
            };
        }
    }
}
