package icesword.agent.service;

import icesword.agent.data.process.Event;
import icesword.agent.data.process.JstatArguments;
import icesword.agent.data.process.JvmItem;
import icesword.agent.util.jstatex.OptionOutputFormatterEx;
import lombok.Builder;
import sun.jvmstat.monitor.MonitoredHost;
import sun.jvmstat.monitor.MonitoredVm;
import sun.jvmstat.monitor.VmIdentifier;
import sun.jvmstat.monitor.event.HostEvent;
import sun.jvmstat.monitor.event.HostListener;
import sun.jvmstat.monitor.event.VmStatusChangeEvent;
import sun.tools.jstat.OptionFormat;
import sun.tools.jstat.OutputFormatter;

import com.google.common.base.Preconditions;

@Builder
public class JstatWorker implements Runnable {

    private long        interval;

    private JvmItem     item;

    private JstatLogger logger;

    @Override
    public void run() {
        try {
            JstatArguments arguments = new JstatArguments(new String[] {"-gc" + item.vmVersion, String.valueOf(item.pid), String.valueOf(interval)});
            final VmIdentifier vmId = arguments.vmId();
            int interval = arguments.sampleInterval();
            final MonitoredHost monitoredHost = MonitoredHost.getMonitoredHost(vmId);
            MonitoredVm monitoredVm = monitoredHost.getMonitoredVm(vmId, interval);
            logger = new JstatLogger(item);
            OutputFormatter formatter = null;
            Preconditions.checkArgument(arguments.isSpecialOption());
            OptionFormat format = arguments.optionFormat();
            formatter = new OptionOutputFormatterEx(monitoredVm, format);
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
            logger.logSamples(formatter, arguments.headerRate(), arguments.sampleInterval());
            if (terminator != null) {
                monitoredHost.removeHostListener(terminator);
            }
            monitoredHost.detach(monitoredVm);
        } catch (Exception e) {
            e.printStackTrace();
            EventService.addEvent(new Event(0, "Monitor " + item.mainClass + " Exception" + e.getMessage()));
        }
    }

    public void stop() {
        logger.stopLogging();
    }
}
