package icesword.agent.jstat;

import icesword.agent.data.process.JvmItem;
import sun.jvmstat.monitor.MonitoredVm;
import sun.tools.jstat.OptionFormat;
import sun.tools.jstat.OutputFormatter;

public abstract class JstatLogger {

    protected volatile boolean active = true;

    protected JvmItem          item;

    protected MonitoredVm      monitoredVm;

    protected OptionFormat     format;

    protected OutputFormatter  formatter;

    public JstatLogger(JvmItem item, OptionFormat format, MonitoredVm monitoredVm) {
        this.item = item;
        this.format = format;
        this.monitoredVm = monitoredVm;
    }

    public void stopLogging() {
        this.active = false;
    }

    protected void sleep() {
        try {
            Thread.sleep(monitoredVm.getInterval());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public abstract void logSamples();

}
