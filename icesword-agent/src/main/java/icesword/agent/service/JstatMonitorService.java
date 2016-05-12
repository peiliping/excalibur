package icesword.agent.service;

import icesword.agent.data.ClientStatus;
import icesword.agent.data.JVMMemoryData;
import icesword.agent.data.JvmItem;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import sun.jvmstat.monitor.Monitor;
import sun.jvmstat.monitor.MonitorException;
import sun.jvmstat.monitor.MonitoredHost;
import sun.jvmstat.monitor.MonitoredVm;
import sun.jvmstat.monitor.StringMonitor;
import sun.jvmstat.monitor.Units;
import sun.jvmstat.monitor.Variability;
import sun.jvmstat.monitor.VmIdentifier;
import sun.jvmstat.monitor.event.HostEvent;
import sun.jvmstat.monitor.event.HostListener;
import sun.jvmstat.monitor.event.VmStatusChangeEvent;
import sun.tools.jstat.Arguments;
import sun.tools.jstat.OptionFormat;
import sun.tools.jstat.OptionOutputFormatter;
import sun.tools.jstat.OutputFormatter;
import sun.tools.jstat.RawOutputFormatter;

import com.alibaba.fastjson.JSON;

public class JstatMonitorService {

    private ExecutorService                   executor   = Executors.newFixedThreadPool(20);
    private ConcurrentMap<Integer, Future<?>> processing = new ConcurrentHashMap<Integer, Future<?>>();
    public List<JVMMemoryData>                data       = new ArrayList<JVMMemoryData>();

    public void addJVMs(List<JvmItem> jvms, List<ClientStatus> status) throws Exception {
        for (JvmItem item : jvms) {
            if (!processing.containsKey(item.pid) || processing.get(item.pid).isDone()) {
                item.vmVersion = VMAttachService.flag(String.valueOf(item.pid));
                System.out.println(JSON.toJSONString(item));
                Future<?> task = executor.submit(new JstatTask(item, status));
                processing.put(item.pid, task);
            }
        }
    }

    private void jstat(JvmItem item) throws MonitorException {
        Arguments arguments = new Arguments(new String[] {"-gc", String.valueOf(item.pid), "3000"});
        final VmIdentifier vmId = arguments.vmId();
        int interval = arguments.sampleInterval();
        final MonitoredHost monitoredHost = MonitoredHost.getMonitoredHost(vmId);
        MonitoredVm monitoredVm = monitoredHost.getMonitoredVm(vmId, interval);
        final JStatLogger logger = new JStatLogger(item);
        OutputFormatter formatter = null;
        if (arguments.isSpecialOption()) {
            OptionFormat format = arguments.optionFormat();
            formatter = new OptionOutputFormatter(monitoredVm, format);
        } else {
            List<Monitor> logged = monitoredVm.findByPattern(arguments.counterNames());
            Collections.sort(logged, arguments.comparator());
            List<Monitor> constants = new ArrayList<Monitor>();
            for (Iterator<Monitor> i = logged.iterator(); i.hasNext(); /* empty */) {
                Monitor m = (Monitor) i.next();
                if (!(m.isSupported() || arguments.showUnsupported())) {
                    i.remove();
                    continue;
                }
                if (m.getVariability() == Variability.CONSTANT) {
                    i.remove();
                    if (arguments.printConstants())
                        constants.add(m);
                } else if ((m.getUnits() == Units.STRING) && !arguments.printStrings()) {
                    i.remove();
                }
            }
            if (!constants.isEmpty()) {
                logger.printList(constants, arguments.isVerbose(), arguments.showUnsupported(), System.out);
                if (!logged.isEmpty()) {
                    System.out.println();
                }
            }
            if (logged.isEmpty()) {
                monitoredHost.detach(monitoredVm);
                return;
            }
            formatter = new RawOutputFormatter(logged, arguments.printStrings());
        }
        // handle user termination requests by stopping sampling loops
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                logger.stopLogging();
            }
        });
        // handle target termination events for targets other than ourself
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
        logger.logSamples(formatter, arguments.headerRate(), arguments.sampleInterval(), arguments.sampleCount());
        // detach from host events and from the monitored target jvm
        if (terminator != null) {
            monitoredHost.removeHostListener(terminator);
        }
        monitoredHost.detach(monitoredVm);
        processing.remove(item.pid);
    }

    public class JStatLogger {

        private volatile boolean active = true;
        private JvmItem          item;

        public JStatLogger(JvmItem item) {
            this.item = item;
        }

        public void stopLogging() {
            active = false;
        }

        public void printList(List<Monitor> list, boolean verbose, boolean showUnsupported, PrintStream out) throws MonitorException {

            // print out the name of each available counter
            for (Monitor m : list) {

                if (!(m.isSupported() || showUnsupported)) {
                    continue;
                }

                StringBuilder buffer = new StringBuilder();
                buffer.append(m.getName()).append("=");

                if (m instanceof StringMonitor) {
                    buffer.append("\"").append(m.getValue()).append("\"");
                } else {
                    buffer.append(m.getValue());
                }

                if (verbose) {
                    buffer.append(" ").append(m.getUnits());
                    buffer.append(" ").append(m.getVariability());
                    buffer.append(" ").append(m.isSupported() ? "Supported" : "Unsupported");
                }
                out.println(buffer);
            }
        }

        public void logSamples(OutputFormatter formatter, int headerRate, int sampleInterval, int sampleCount) throws MonitorException {
            while (active) {
                try {
                    String row = formatter.getRow();
                    row = row.replaceAll("\\s+ ", " ");
                    String[] t = row.split(" ");
                    System.out.println(Arrays.toString(t));
                    data.add(new JVMMemoryData("JVMMemoryMetrics/" + item.vmVersion + "/S0", "JVMMemoryMetrics/" + item.vmVersion, Double.valueOf(t[2]), Double.valueOf(t[0]))
                            .addTag("processSignal", item.mainClass));
                    data.add(new JVMMemoryData("JVMMemoryMetrics/" + item.vmVersion + "/S1", "JVMMemoryMetrics/" + item.vmVersion, Double.valueOf(t[3]), Double.valueOf(t[1]))
                            .addTag("processSignal", item.mainClass));
                    data.add(new JVMMemoryData("JVMMemoryMetrics/" + item.vmVersion + "/Eden", "JVMMemoryMetrics/" + item.vmVersion, Double.valueOf(t[5]), Double.valueOf(t[4]))
                            .addTag("processSignal", item.mainClass));
                    data.add(new JVMMemoryData("JVMMemoryMetrics/" + item.vmVersion + "/Old", "JVMMemoryMetrics/" + item.vmVersion, Double.valueOf(t[7]), Double.valueOf(t[6]))
                            .addTag("processSignal", item.mainClass));
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


    public class JstatTask implements Runnable {

        private JvmItem            item;

        private List<ClientStatus> status;

        public JstatTask(JvmItem item, List<ClientStatus> status) {
            this.item = item;
            this.status = status;
        }

        @Override
        public void run() {
            try {
                jstat(item);
            } catch (MonitorException e) {
                e.printStackTrace();
                status.add(new ClientStatus(1, "Attach " + item.pid + " " + item.mainClass + "Fail."));
                return;
            }
            status.add(new ClientStatus(1, "Attach " + item.pid + " " + item.mainClass + "Success."));
        }

    }


}
