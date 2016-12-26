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

    public void output(long timestamp) {
        super._output("time", timestamp, handleTimePrecision(getDeltaVal("time")));
        super._output("loading", timestamp, getDeltaVal("loaded"));
        super._output("unloading", timestamp, getDeltaVal("unloaded"));
        super.output(timestamp);
    }
}
