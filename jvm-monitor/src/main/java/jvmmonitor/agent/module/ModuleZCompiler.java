package jvmmonitor.agent.module;

import jvmmonitor.agent.monitor.MonitorItem;

/**
 * Created by peiliping on 16-12-21.
 */
public class ModuleZCompiler extends AbstractModule {

    public ModuleZCompiler(String moduleName, MonitorItem item) {
        super(moduleName, item);
        super.noChangeMetricNames = new String[] {"time"};
        METRICNAME.put("time", "java.ci.totalTime");
        METRICNAME.put("count", "sun.ci.totalCompiles");
        METRICNAME.put("osrtime", "sun.ci.osrTime");
        METRICNAME.put("osrcount", "sun.ci.osrCompiles");
        METRICNAME.put("bailouts", "sun.ci.totalBailouts");
        METRICNAME.put("invalidates", "sun.ci.totalInvalidates");
    }

    public void output() {
        super._output("time", handleTimePrecision(getDeltaVal("time")));
        super._output("count", getDeltaVal("count"));
        super._output("osrtime", handleTimePrecision(getDeltaVal("osrtime")));
        super._output("osrcount", getDeltaVal("osrcount"));
        super._output("bailouts", getDeltaVal("bailouts"));
        super._output("invalidates", getDeltaVal("invalidates"));
    }
}
