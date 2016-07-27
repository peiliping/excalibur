package icesword.agent.jstat;

import icesword.agent.Startup;
import icesword.agent.data.process.Event;
import icesword.agent.data.process.JvmItem;
import icesword.agent.service.EventService;
import icesword.agent.util.Mode;
import lombok.Builder;
import sun.jvmstat.monitor.MonitoredHost;
import sun.jvmstat.monitor.MonitoredVm;
import sun.jvmstat.monitor.VmIdentifier;
import sun.jvmstat.monitor.event.HostEvent;
import sun.jvmstat.monitor.event.HostListener;
import sun.jvmstat.monitor.event.VmStatusChangeEvent;
import sun.tools.jstat.JstatArguments;
import sun.tools.jstat.OptionFormat;

@Builder
public class JstatWorker implements Runnable {

    private int         interval;

    private JvmItem     item;

    private JstatLogger logger;

    @Override
    public void run() {
        try {
            final JstatArguments arguments = new JstatArguments(new String[] {"-gc" + item.vmVersion, String.valueOf(item.pid), String.valueOf(interval)});
            final OptionFormat format = arguments.optionFormat();
            final VmIdentifier vmId = arguments.vmId();
            final MonitoredHost monitoredHost = MonitoredHost.getMonitoredHost(vmId);
            final MonitoredVm monitoredVm = monitoredHost.getMonitoredVm(vmId, interval);

            if (Startup.MODE == Mode.ON_LINE)
                logger = new JstatLoggerRemote(item, format, monitoredVm);
            if (Startup.MODE == Mode.OFF_LINE)
                logger = new JstatLoggerConsole(item, format, monitoredVm);

            Runtime.getRuntime().addShutdownHook(new Thread() {
                public void run() {
                    logger.stopLogging();
                }
            });
            HostListener terminator = new HostListener() {
                public void vmStatusChanged(VmStatusChangeEvent ev) {
                    Integer lvmid = new Integer(vmId.getLocalVmId());
                    if (ev.getTerminated().contains(lvmid)) {
                        logger.stopLogging();
                    } else if (!ev.getActive().contains(lvmid)) {
                        logger.stopLogging();
                    }
                }

                public void disconnected(HostEvent ev) {
                    if (monitoredHost == ev.getMonitoredHost()) {
                        logger.stopLogging();
                    }
                }
            };
            if (vmId.getLocalVmId() != 0) {
                monitoredHost.addHostListener(terminator);
            }

            EventService.addEvent(new Event(1, "Monitor " + item.mainClass));
            logger.logSamples();

            if (terminator != null) {
                monitoredHost.removeHostListener(terminator);
            }
            monitoredHost.detach(monitoredVm);
        } catch (Throwable e) {
            EventService.addEvent(new Event(0, "Monitor " + item.mainClass + " Exception" + e.getMessage()));
        }
    }

    public void stop() {
        logger.stopLogging();
    }
}
