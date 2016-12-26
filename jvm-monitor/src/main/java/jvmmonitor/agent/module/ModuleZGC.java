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
        this.garbageCollector0Name = Util.getValueFromMonitoredVm(item.getMonitoredVm(), "sun.gc.collector.0.name").toLowerCase();
        this.garbageCollector1Name = Util.getValueFromMonitoredVm(item.getMonitoredVm(), "sun.gc.collector.1.name").toLowerCase();
        super.noChangeMetricNames = new String[] {garbageCollector0Name + "/count", garbageCollector1Name + "/count"};
        METRICNAME.put(garbageCollector0Name + "/count", "sun.gc.collector.0.invocations");
        METRICNAME.put(garbageCollector0Name + "/time", "sun.gc.collector.0.time");
        METRICNAME.put(garbageCollector0Name + "/pausetime", "sun.gc.policy.avgMinorPauseTime");

        METRICNAME.put(garbageCollector1Name + "/count", "sun.gc.collector.1.invocations");
        METRICNAME.put(garbageCollector1Name + "/time", "sun.gc.collector.1.time");
        METRICNAME.put(garbageCollector1Name + "/pausetime", "sun.gc.policy.avgMajorPauseTime");

        METRICNAME.put("cross/promoted", "sun.gc.policy.avgPromotedAvg");
        METRICNAME.put("cross/survived", "sun.gc.policy.avgSurvivedAvg");

        METRICNAME.put("error/timelimitexceeded", "sun.gc.policy.gcTimeLimitExceeded");
        METRICNAME.put("error/survivoroverflowed", "sun.gc.policy.survivorOverflowed");
    }

    public void output(long timestamp) {
        super._output(garbageCollector0Name + "/count", timestamp, getDeltaVal(garbageCollector0Name + "/count"));
        super._output(garbageCollector0Name + "/time", timestamp, handleTimePrecision(getDeltaVal(garbageCollector0Name + "/time")));
        super._output(garbageCollector0Name + "/pausetime", timestamp, handleTimePrecision(getOriginVal(garbageCollector0Name + "/pausetime")));

        super._output(garbageCollector1Name + "/count", timestamp, getDeltaVal(garbageCollector1Name + "/count"));
        super._output(garbageCollector1Name + "/time", timestamp, handleTimePrecision(getDeltaVal(garbageCollector1Name + "/time")));
        super._output(garbageCollector1Name + "/pausetime", timestamp, handleTimePrecision(getOriginVal(garbageCollector1Name + "/pausetime")));

        super._output("cross/promoted", timestamp, getOriginVal("cross/promoted"));
        super._output("cross/survived", timestamp, getOriginVal("cross/survived"));

        super._output("error/timelimitexceeded", timestamp, handleTimePrecision(getDeltaVal("error/timelimitexceeded")));
        super._output("error/survivoroverflowed", timestamp, handleTimePrecision(getDeltaVal("error/survivoroverflowed")));
        super.output(timestamp);
    }
}
