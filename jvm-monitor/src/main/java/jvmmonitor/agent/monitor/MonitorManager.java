package jvmmonitor.agent.monitor;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import jvmmonitor.agent.Config;
import sun.jvmstat.monitor.*;
import sun.jvmstat.monitor.event.HostEvent;
import sun.jvmstat.monitor.event.HostListener;
import sun.jvmstat.monitor.event.VmStatusChangeEvent;
import sun.tools.jps.Arguments;

import java.net.URISyntaxException;
import java.util.Map;
import java.util.Set;

/**
 * Created by peiliping on 16-12-19.
 */
public class MonitorManager {

    private static Arguments JPS_ARGUMENTS = new Arguments(new String[] {"-l"});

    private final Map<Integer, MonitorItem> CONTAINER = Maps.newHashMap();

    private Config config;

    private MonitoredHost monitoredHost;

    public MonitorManager(Config config) throws MonitorException {
        this.config = config;
        this.monitoredHost = MonitoredHost.getMonitoredHost(JPS_ARGUMENTS.hostId());
        this.monitoredHost.addHostListener(new HostListener() {
            public void vmStatusChanged(VmStatusChangeEvent event) {
                if (event.getStarted().size() > 0) {
                    findActiveJVM(event.getStarted());
                } else if (event.getTerminated().size() > 0) {
                    close(event.getTerminated());
                }
            }

            public void disconnected(HostEvent event) {
                close(null);
            }
        });
    }

    private static VmIdentifier buildVmIdentifier(Integer id) throws URISyntaxException {
        String vmidString = "//" + id + "?mode=r";
        return new VmIdentifier(vmidString);
    }

    public synchronized void findActiveJVM(Set<Integer> jvmIds) {
        try {
            if (jvmIds == null || jvmIds.size() == 0) {
                jvmIds = monitoredHost.activeVms();
            }
            for (Integer id : jvmIds) {
                if (!CONTAINER.containsKey(id) && config.filterPid(id)) {
                    try {
                        MonitoredVm vm = monitoredHost.getMonitoredVm(buildVmIdentifier(id), 1000);
                        String mainClass = MonitoredVmUtil.mainClass(vm, true);
                        if (!config.filterKeyWords(mainClass)) {
                            MonitorItem item = MonitorItem.builder().pid(id).mainClass(mainClass).monitoredVm(vm).build();
                            item.initBaseInfo();
                            item.initJVMFlags();
                            item.initModules(config);
                            CONTAINER.put(id, item);
                        } else {
                            monitoredHost.detach(vm);
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void close(Set<Integer> vmIds) {
        if (vmIds == null || vmIds.size() == 0) {
            vmIds = Sets.newHashSet();
            vmIds.addAll(CONTAINER.keySet());
        }
        for (Integer id : vmIds) {
            MonitorItem item = CONTAINER.remove(id);
            try {
                monitoredHost.detach(item.getMonitoredVm());
            } catch (MonitorException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void run() {
        for (Map.Entry<Integer, MonitorItem> item : CONTAINER.entrySet()) {
            try {
                item.getValue().run();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
