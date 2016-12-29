package jvmmonitor.agent.module;

import jvmmonitor.agent.monitor.MonitorItem;

/**
 * Created by peiliping on 16-12-21.
 */
public class ModuleZCompiler extends AbstractModule {

    public ModuleZCompiler(String moduleName, MonitorItem item) {
        super(moduleName, item);
        super.noChangeMetricNames = new String[] {"time"};
        super.filterZeroValue = true;
        super.addMetric("time", "java.ci.totalTime");
        super.addMetric("count", "sun.ci.totalCompiles");
        super.addMetric("osrtime", "sun.ci.osrTime");
        super.addMetric("osrcount", "sun.ci.osrCompiles");
        super.addMetric("bailouts", "sun.ci.totalBailouts");
        super.addMetric("invalidates", "sun.ci.totalInvalidates");
    }

    public void transform(long timestamp) {
        super.store("time", timestamp, handleTimePrecision(getDeltaVal("time")));
        super.store("count", timestamp, getDeltaVal("count"));
        super.store("osrtime", timestamp, handleTimePrecision(getDeltaVal("osrtime")));
        super.store("osrcount", timestamp, getDeltaVal("osrcount"));
        super.store("bailouts", timestamp, getDeltaVal("bailouts"));
        super.store("invalidates", timestamp, getDeltaVal("invalidates"));
        super.commit();
    }
}
