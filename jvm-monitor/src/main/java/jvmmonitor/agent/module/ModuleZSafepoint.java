package jvmmonitor.agent.module;

import jvmmonitor.agent.monitor.MonitorItem;

/**
 * Created by peiliping on 16-12-21.
 */
public class ModuleZSafepoint extends AbstractModule {

    public ModuleZSafepoint(MonitorItem item) {
        super(item);
        METRICNAME.put("/safepoint/count", "sun.rt.safepoints");
        METRICNAME.put("/safepoint/time", "sun.rt.safepointTime");
        METRICNAME.put("/safepoint/synctime", "sun.rt.safepointSyncTime");
    }

    public void output() {
        System.out.println("/safepoint/count" + ":" + (getDeltaVal("/safepoint/count")));
        System.out.println("/safepoint/time" + ":" + handleTimePrecision(getDeltaVal("/safepoint/time")));
        System.out.println("/safepoint/synctime" + ":" + handleTimePrecision(getDeltaVal("/safepoint/synctime")));
    }
}
