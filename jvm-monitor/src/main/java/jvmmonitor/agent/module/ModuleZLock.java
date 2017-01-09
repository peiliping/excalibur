package jvmmonitor.agent.module;

import jvmmonitor.agent.monitor.MonitorItem;

/**
 * Created by peiliping on 16-12-21.
 */
public class ModuleZLock extends AbstractModule {

    public ModuleZLock(String moduleName, MonitorItem item) {
        super(moduleName, item);
        super.noChangeMetricNames = new String[] {"contendedlockattempts", "parks", "notifications", "futilewakeups", "inflations", "deflations"};
        super.filterZeroValue = true;
        super.addMetric(item, "contendedlockattempts", "sun.rt._sync_ContendedLockAttempts");
        super.addMetric(item, "parks", "sun.rt._sync_Parks");
        super.addMetric(item, "notifications", "sun.rt._sync_Notifications");
        super.addMetric(item, "futilewakeups", "sun.rt._sync_FutileWakeups");
        super.addMetric(item, "inflations", "sun.rt._sync_Inflations");
        super.addMetric(item, "deflations", "sun.rt._sync_Deflations");
    }

    public void transform(long timestamp) {
        super.store("contendedlockattempts", timestamp, getDeltaVal("contendedlockattempts"));
        super.store("parks", timestamp, getDeltaVal("parks"));
        super.store("notifications", timestamp, getDeltaVal("notifications"));
        super.store("futilewakeups", timestamp, getDeltaVal("futilewakeups"));
        super.store("inflations", timestamp, getDeltaVal("inflations"));
        super.store("deflations", timestamp, getDeltaVal("deflations"));
        super.commit();
    }
}
