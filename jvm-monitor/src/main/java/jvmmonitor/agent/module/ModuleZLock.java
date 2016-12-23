package jvmmonitor.agent.module;

import jvmmonitor.agent.monitor.MonitorItem;

/**
 * Created by peiliping on 16-12-21.
 */
public class ModuleZLock extends AbstractModule {

    public ModuleZLock(String moduleName, MonitorItem item) {
        super(moduleName, item);
        super.noChangeMetricNames = new String[] {"contendedlockattempts", "parks", "notifications", "futilewakeups", "inflations", "deflations"};
        METRICNAME.put("contendedlockattempts", "sun.rt._sync_ContendedLockAttempts");
        METRICNAME.put("parks", "sun.rt._sync_Parks");
        METRICNAME.put("notifications", "sun.rt._sync_Notifications");
        METRICNAME.put("futilewakeups", "sun.rt._sync_FutileWakeups");
        METRICNAME.put("inflations", "sun.rt._sync_Inflations");
        METRICNAME.put("deflations", "sun.rt._sync_Deflations");
    }

    public void output() {
        super._output("contendedlockattempts", getDeltaVal("contendedlockattempts"));
        super._output("parks", getDeltaVal("parks"));
        super._output("notifications", getDeltaVal("notifications"));
        super._output("futilewakeups", getDeltaVal("futilewakeups"));
        super._output("inflations", getDeltaVal("inflations"));
        super._output("deflations", getDeltaVal("deflations"));
    }
}
