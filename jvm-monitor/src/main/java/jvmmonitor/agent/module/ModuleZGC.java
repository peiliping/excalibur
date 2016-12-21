package jvmmonitor.agent.module;

import jvmmonitor.agent.Util;
import jvmmonitor.agent.monitor.MonitorItem;

/**
 * Created by peiliping on 16-12-21.
 */
public class ModuleZGC extends AbstractModule {

    private String garbageCollector0Name;
    private String garbageCollector1Name;

    public ModuleZGC(MonitorItem item) {
        super(item);
        this.garbageCollector0Name = "/gc/" + Util.getValueFromMonitoredVm(item.getMonitoredVm(), "sun.gc.collector.0.name");
        this.garbageCollector1Name = "/gc/" + Util.getValueFromMonitoredVm(item.getMonitoredVm(), "sun.gc.collector.1.name");
        METRICNAME.put(garbageCollector0Name + "/count", "sun.gc.collector.0.invocations");
        METRICNAME.put(garbageCollector0Name + "/time", "sun.gc.collector.0.time");
        METRICNAME.put(garbageCollector1Name + "/count", "sun.gc.collector.1.invocations");
        METRICNAME.put(garbageCollector1Name + "/time", "sun.gc.collector.1.time");
    }

    public void output() {
        System.out.println(garbageCollector0Name + "/count" + ":" + getDeltaVal(garbageCollector0Name + "/count"));
        System.out.println(garbageCollector0Name + "/time" + ":" + getDeltaVal(garbageCollector0Name + "/time"));
        System.out.println(garbageCollector1Name + "/count" + ":" + getDeltaVal(garbageCollector1Name + "/count"));
        System.out.println(garbageCollector1Name + "/time" + ":" + getDeltaVal(garbageCollector1Name + "/time"));
    }
}
