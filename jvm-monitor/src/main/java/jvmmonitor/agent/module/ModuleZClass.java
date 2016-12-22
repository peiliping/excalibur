package jvmmonitor.agent.module;

import jvmmonitor.agent.monitor.MonitorItem;

/**
 * Created by peiliping on 16-12-21.
 */
public class ModuleZClass extends AbstractModule {

    public ModuleZClass(MonitorItem item) {
        super(item);
        METRICNAME.put("/class/time", "sun.cls.time");
        METRICNAME.put("/class/loaded", "java.cls.loadedClasses");
        METRICNAME.put("/class/unloaded", "java.cls.unloadedClasses");
    }

    public void output() {
        super._output("/class/time", handleTimePrecision(getDeltaVal("/class/time")));
        System.out.println("/class/loading" + ":" + getDeltaVal("/class/loaded"));
        System.out.println("/class/loaded" + ":" + getOriginVal("/class/loaded"));
        System.out.println("/class/unloading" + ":" + getDeltaVal("/class/unloaded"));
        System.out.println("/class/unloaded" + ":" + getOriginVal("/class/unloaded"));
    }
}
