package jvmmonitor.agent.module;

import jvmmonitor.agent.monitor.MonitorItem;

/**
 * Created by peiliping on 16-12-21.
 */
public class ModuleZTlab extends AbstractModule {

    public ModuleZTlab(String moduleName, MonitorItem item) {
        super(moduleName, item);
        super.noChangeMetricNames = new String[] {"minorgc", "majorgc"};
        super.metricValuesNum = 3;
        super.addMetric(item, "minorgc", "sun.gc.collector.0.invocations");
        super.addMetric(item, "majorgc", "sun.gc.collector.1.invocations");

        super.addMetric(item, "alloc", "sun.gc.tlab.alloc");
        super.addMetric(item, "allocthreads", "sun.gc.tlab.allocThreads");
        super.addMetric(item, "fills", "sun.gc.tlab.fills");
        super.addMetric(item, "gcwaste", "sun.gc.tlab.gcWaste");
    }

    public void transform(long timestamp) {
        super.store("alloc", timestamp, getOriginVal("alloc"), 1L);
        super.store("allocthreads", timestamp, getOriginVal("allocthreads"), 1L);
        super.store("fills", timestamp, getOriginVal("fills"), 1L);
        super.store("gcwaste", timestamp, getOriginVal("gcwaste"), 1L);
        super.commit();
    }
}
