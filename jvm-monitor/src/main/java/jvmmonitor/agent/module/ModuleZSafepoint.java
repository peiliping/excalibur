package jvmmonitor.agent.module;

import jvmmonitor.agent.monitor.MonitorItem;

/**
 * Created by peiliping on 16-12-21.
 */
public class ModuleZSafepoint extends AbstractModule {

    public ModuleZSafepoint(String moduleName, MonitorItem item) {
        super(moduleName, item);
        super.noChangeMetricNames = new String[] {"count"};
        METRICNAME.put("count", "sun.rt.safepoints");
        METRICNAME.put("time", "sun.rt.safepointTime");
        METRICNAME.put("synctime", "sun.rt.safepointSyncTime");
    }

    public void output() {
        super._output("count", getDeltaVal("count"));
        super._output("time", handleTimePrecision(getDeltaVal("time")));
        super._output("synctime", handleTimePrecision(getDeltaVal("synctime")));
    }
}
