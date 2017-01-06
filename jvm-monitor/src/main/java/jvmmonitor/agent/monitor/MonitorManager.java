package jvmmonitor.agent.monitor;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import jvmmonitor.agent.Config;
import jvmmonitor.agent.Util;
import jvmmonitor.agent.flag.FlagsContainer;
import sun.jvmstat.monitor.*;
import sun.jvmstat.monitor.event.HostEvent;
import sun.jvmstat.monitor.event.HostListener;
import sun.jvmstat.monitor.event.VmStatusChangeEvent;
import sun.tools.jps.Arguments;

import java.net.URISyntaxException;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by peiliping on 16-12-19.
 */
public class MonitorManager {

    private static Arguments JPS_ARGUMENTS = new Arguments(new String[] {"-l"});

    private final Map<Integer, MonitorItem> CONTAINER = Maps.newHashMap();

    private final DataContainer DATACONTAINER = new DataContainer();

    private Config config;

    private MonitoredHost monitoredHost;

    private Timer timer = new Timer("Jps");

    public MonitorManager(Config config) throws MonitorException {
        this.config = config;
        this.monitoredHost = MonitoredHost.getMonitoredHost(JPS_ARGUMENTS.hostId());
        this.timer.schedule(new TimerTask() {
            @Override public void run() {
                try {
                    Set<Integer> last = Sets.newHashSet(CONTAINER.keySet());
                    Set<Integer> current = monitoredHost.activeVms();
                    last.removeAll(current);
                    if (last.size() > 0) {
                        close(last);
                    }
                    findActiveJVM(false, null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 60000, 60000);

        this.monitoredHost.addHostListener(new HostListener() {
            public void vmStatusChanged(VmStatusChangeEvent event) {
            }

            public void disconnected(HostEvent event) {
                close(null);
            }
        });
        this.DATACONTAINER.getMeta().put("ip", Util.getLocalIP());
        this.DATACONTAINER.getMeta().put("type", "metric");
        this.DATACONTAINER.getMeta().put("mode", config.getMode());
        if (config.getMode().equals("test")) {
            this.DATACONTAINER.getMeta().put("app", config.getAppName());
        }
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
                            if (!restartAgent || this.config.isParseFlagsWhenRestartAgent()) {
                                item.initJVMFlags();
                                FlagsContainer fc = new FlagsContainer(this.DATACONTAINER, item);
                                if (this.config.isDebug())
                                    System.out.println(JSON.toJSONString(fc));
                                Util.httpPost(this.config.getUrl(), Util.compress(fc));
                            }
                            item.initModules(this.config);
                            this.CONTAINER.put(id, item);
                        } else {
                            this.monitoredHost.detach(vm);
                        }
                    } catch (Throwable e) {
                        System.out.println("ID : " + id);
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
                if (item != null) {
                    this.DATACONTAINER.getData().remove(item.getMainClass());
                    this.monitoredHost.detach(item.getMonitoredVm());
                }
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
                    System.out.println("Monitor Pids : " + CONTAINER.keySet().toString());
                    System.out.println(JSON.toJSONString(this.DATACONTAINER));
                }
                Util.httpPost(this.config.getUrl(), Util.compress(this.DATACONTAINER));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        this.counter++;
    }

    public synchronized void printPerfData() {
        for (Map.Entry<Integer, MonitorItem> item : CONTAINER.entrySet()) {
            item.getValue().printPerfData();
        }
    }
}
