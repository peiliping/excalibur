package jvmmonitor.agent.module;

import jvmmonitor.agent.Util;
import jvmmonitor.agent.monitor.MonitorItem;

/**
 * Created by peiliping on 16-12-21.
 */
public class ModuleZGC extends AbstractModule {

    private String garbageCollector0Name;
    private String garbageCollector1Name;

    public ModuleZGC(String moduleName, MonitorItem item) {
        super(moduleName, item);
        super.metricValuesNum = 4;
        this.garbageCollector0Name = Util.getValueFromMonitoredVm(item.getMonitoredVm(), "sun.gc.collector.0.name").toLowerCase();
        this.garbageCollector1Name = Util.getValueFromMonitoredVm(item.getMonitoredVm(), "sun.gc.collector.1.name").toLowerCase();
        super.noChangeMetricNames = new String[] {garbageCollector0Name + "/count", garbageCollector1Name + "/count"};
        super.addMetric(garbageCollector0Name + "/count", "sun.gc.collector.0.invocations");
        super.addMetric(garbageCollector0Name + "/time", "sun.gc.collector.0.time");
        super.addMetric(garbageCollector0Name + "/pausetime", "sun.gc.policy.avgMinorPauseTime");

        super.addMetric(garbageCollector1Name + "/count", "sun.gc.collector.1.invocations");
        super.addMetric(garbageCollector1Name + "/time", "sun.gc.collector.1.time");
        super.addMetric(garbageCollector1Name + "/pausetime", "sun.gc.policy.avgMajorPauseTime");
    }

    public void transform(long timestamp) {
        super.store(garbageCollector0Name, timestamp, getDeltaVal(garbageCollector0Name + "/count"), handleTimePrecision(getDeltaVal(garbageCollector0Name + "/time")),
                handleTimePrecision(getOriginVal(garbageCollector0Name + "/pausetime")));
        super.store(garbageCollector1Name, timestamp, getDeltaVal(garbageCollector1Name + "/count"), handleTimePrecision(getDeltaVal(garbageCollector1Name + "/time")),
                handleTimePrecision(getOriginVal(garbageCollector1Name + "/pausetime")));
        super.commit();
    }
}
