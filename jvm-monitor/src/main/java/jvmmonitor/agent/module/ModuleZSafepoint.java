package jvmmonitor.agent.module;

import jvmmonitor.agent.monitor.MonitorItem;

/**
 * Created by peiliping on 16-12-21.
 */
public class ModuleZSafepoint extends AbstractModule {

    public ModuleZSafepoint(String moduleName, MonitorItem item) {
        super(moduleName, item);
        super.noChangeMetricNames = new String[] {"count"};
        super.addMetric("count", "sun.rt.safepoints");
        super.addMetric("time", "sun.rt.safepointTime");
        super.addMetric("synctime", "sun.rt.safepointSyncTime");
    }

    public void transform(long timestamp) {
        super.store("count", timestamp, getDeltaVal("count"));
        super.store("time", timestamp, handleTimePrecision(getDeltaVal("time")));
        super.store("synctime", timestamp, handleTimePrecision(getDeltaVal("synctime")));
        super.commit();
    }
}
