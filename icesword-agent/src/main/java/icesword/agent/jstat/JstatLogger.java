package icesword.agent.jstat;

import icesword.agent.data.process.JvmItem;
import sun.jvmstat.monitor.MonitorException;
import sun.tools.jstat.OutputFormatter;

public abstract class JstatLogger {

    protected volatile boolean active = true;

    protected JvmItem          item;

    protected long             sampleInterval;

    public JstatLogger(JvmItem item, long sampleInterval) {
        this.item = item;
        this.sampleInterval = sampleInterval;
    }

    public void stopLogging() {
        this.active = false;
    }

    protected void sleep() {
        try {
            Thread.sleep(sampleInterval);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public abstract void logSamples(OutputFormatter formatter) throws MonitorException;

}
