package jvmmonitor.agent.module;

import jvmmonitor.agent.monitor.MonitorItem;

/**
 * Created by peiliping on 16-12-21.
 */
public class ModuleZClass extends AbstractModule {

    public ModuleZClass(String moduleName, MonitorItem item) {
        super(moduleName, item);
        super.noChangeMetricNames = new String[] {"time"};
        super.addMetric("time", "sun.cls.time");
        super.addMetric("loaded", "java.cls.loadedClasses");
        super.addMetric("unloaded", "java.cls.unloadedClasses");
    }

    public void transform(long timestamp) {
        super.store("time", timestamp, handleTimePrecision(getDeltaVal("time")));
        super.store("loading", timestamp, getDeltaVal("loaded"));
        super.store("unloading", timestamp, getDeltaVal("unloaded"));
        super.commit();
    }
}
