package jvmmonitor.agent.monitor;

import java.net.URISyntaxException;
import java.util.Map;
import java.util.Set;

import jvmmonitor.agent.Config;
import jvmmonitor.agent.DataContainer;
import jvmmonitor.agent.Util;
import sun.jvmstat.monitor.MonitorException;
import sun.jvmstat.monitor.MonitoredHost;
import sun.jvmstat.monitor.MonitoredVm;
import sun.jvmstat.monitor.MonitoredVmUtil;
import sun.jvmstat.monitor.VmIdentifier;
import sun.jvmstat.monitor.event.HostEvent;
import sun.jvmstat.monitor.event.HostListener;
import sun.jvmstat.monitor.event.VmStatusChangeEvent;
import sun.tools.jps.Arguments;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * Created by peiliping on 16-12-19.
 */
public class MonitorManager {

    private static Arguments JPS_ARGUMENTS = new Arguments(new String[] {"-l"});

    private final Map<Integer, MonitorItem> CONTAINER = Maps.newHashMap();

    private final DataContainer DATACONTAINER = new DataContainer();

    private Config config;

    private MonitoredHost monitoredHost;

    public MonitorManager(Config config) throws MonitorException {
        this.config = config;
        this.monitoredHost = MonitoredHost.getMonitoredHost(JPS_ARGUMENTS.hostId());
        this.monitoredHost.addHostListener(new HostListener() {
            public void vmStatusChanged(VmStatusChangeEvent event) {
                if (event.getStarted().size() > 0) {
                    findActiveJVM(false, event.getStarted());
                } else if (event.getTerminated().size() > 0) {
                    close(event.getTerminated());
                }
            }

            public void disconnected(HostEvent event) {
                close(null);
            }
        });
        this.DATACONTAINER.getMeta().put("ip", Util.getLocalIP());
    }

    private static VmIdentifier buildVmIdentifier(Integer id) throws URISyntaxException {
        String vmidString = "//" + id + "?mode=r";
        return new VmIdentifier(vmidString);
    }

    public synchronized void findActiveJVM(boolean restartAgent, Set<Integer> jvmIds) {
        try {
            if (jvmIds == null || jvmIds.size() == 0) {
                jvmIds = this.monitoredHost.activeVms();
            }
            for (Integer id : jvmIds) {
                if (!this.CONTAINER.containsKey(id) && this.config.filterPid(id)) {
                    try {
                        MonitoredVm vm = this.monitoredHost.getMonitoredVm(buildVmIdentifier(id), 1000);
                        String mainClass = MonitoredVmUtil.mainClass(vm, true);
                        if (!this.config.filterKeyWords(mainClass)) {
                            MonitorItem item = MonitorItem.builder().pid(id).mainClass(mainClass).monitoredVm(vm).build();
                            item.initBaseInfo();
                            if (!restartAgent)
                                item.initJVMFlags();
                            if (this.config.isDebug())
                                System.out.println(JSON.toJSONString(item.getFlags()));
                            item.initModules(this.config);
                            this.CONTAINER.put(id, item);
                        } else {
                            this.monitoredHost.detach(vm);
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
            vmIds.addAll(this.CONTAINER.keySet());
        }
        for (Integer id : vmIds) {
            MonitorItem item = this.CONTAINER.remove(id);
            try {
                this.monitoredHost.detach(item.getMonitoredVm());
            } catch (MonitorException e) {
                e.printStackTrace();
            }
        }
    }

    private int counter = 1;

    private boolean match4SendData() {
        return this.counter % this.config.getMultiple4SendData() == 0;
    }

    public synchronized void run() {
        boolean match = match4SendData();
        for (Map.Entry<Integer, MonitorItem> item : this.CONTAINER.entrySet()) {
            try {
                item.getValue().run();
                if (match) {
                    Map<String, Map<String, long[][]>> v = item.getValue().getMetrics();
                    if (v.size() > 0) {
                        this.DATACONTAINER.getData().put(item.getValue().getMainClass(), v);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (match) {
            try {
                if (this.config.isDebug()) {
                    System.out.println(JSON.toJSONString(this.DATACONTAINER));
                }
                Util.httpPost(this.config.getMetricUrl(), Util.compress(this.DATACONTAINER));
            } catch (Exception e) {
            }
        }
        this.counter++;
    }
}
