package jvmmonitor.agent.module;

import jvmmonitor.agent.monitor.MonitorItem;

/**
 * Created by peiliping on 16-12-21.
 */
public class ModuleZClass extends AbstractModule {

    public ModuleZClass(String moduleName, MonitorItem item) {
        super(moduleName, item);
        super.noChangeMetricNames = new String[] {"time"};
        METRICNAME.put("time", "sun.cls.time");
        METRICNAME.put("loaded", "java.cls.loadedClasses");
        METRICNAME.put("unloaded", "java.cls.unloadedClasses");
    }

    public void output() {
        super._output("time", handleTimePrecision(getDeltaVal("time")));
        super._output("loading", getDeltaVal("loaded"));
        super._output("unloading", getDeltaVal("unloaded"));
    }
}
