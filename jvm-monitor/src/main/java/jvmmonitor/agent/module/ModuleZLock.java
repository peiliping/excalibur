package jvmmonitor.agent.module;

import jvmmonitor.agent.monitor.MonitorItem;

/**
 * Created by peiliping on 16-12-21.
 */
public class ModuleZLock extends AbstractModule {

    public ModuleZLock(MonitorItem item) {
        super(item);
        METRICNAME.put("/lock/contendedlockattempts", "sun.rt._sync_ContendedLockAttempts");
        METRICNAME.put("/lock/parks", "sun.rt._sync_Parks");
        METRICNAME.put("/lock/notifications", "sun.rt._sync_Notifications");
        METRICNAME.put("/lock/futilewakeups", "sun.rt._sync_FutileWakeups");
        METRICNAME.put("/lock/inflations", "sun.rt._sync_Inflations");
        METRICNAME.put("/lock/deflations", "sun.rt._sync_Deflations");
    }

    public void output() {
        System.out.println("/lock/contendedlockattempts" + ":" + getDeltaVal("/lock/contendedlockattempts"));
        System.out.println("/lock/parks" + ":" + getDeltaVal("/lock/parks"));
        System.out.println("/lock/notifications" + ":" + getDeltaVal("/lock/notifications"));
        System.out.println("/lock/futilewakeups" + ":" + getDeltaVal("/lock/futilewakeups"));
        System.out.println("/lock/inflations" + ":" + getDeltaVal("/lock/inflations"));
        System.out.println("/lock/deflations" + ":" + getDeltaVal("/lock/deflations"));
    }
}
