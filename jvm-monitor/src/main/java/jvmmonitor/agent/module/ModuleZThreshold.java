package jvmmonitor.agent.module;

import jvmmonitor.agent.monitor.MonitorItem;

/**
 * Created by peiliping on 16-12-21.
 */
public class ModuleZThreshold extends AbstractModule {

    public ModuleZThreshold(String moduleName, MonitorItem item) {
        super(moduleName, item);
        super.noChangeMetricNames = new String[] {"minorgc", "majorgc"};
        super.filterZeroValue = true;
        super.metricValuesNum = 3;
        super.addMetric("minorgc", "sun.gc.collector.0.invocations");
        super.addMetric("majorgc", "sun.gc.collector.1.invocations");

        super.addMetric("current", "sun.gc.policy.tenuringThreshold");
        super.addMetric("max", "sun.gc.policy.maxTenuringThreshold");
        super.addMetric("incre4gc", "sun.gc.policy.incrementTenuringThresholdForGcCost");
        super.addMetric("decre4gc", "sun.gc.policy.decrementTenuringThresholdForGcCost");
        super.addMetric("decre4survivor", "sun.gc.policy.decrementTenuringThresholdForSurvivorLimit");
    }

    public void transform(long timestamp) {
        super.store("current", timestamp, getOriginVal("current"), 1L);
        super.store("max", timestamp, getOriginVal("max"), 1L);
        super.store("incre4gc", timestamp, getOriginVal("incre4gc"), 0L);
        super.store("decre4gc", timestamp, getOriginVal("decre4gc"), 0L);
        super.store("decre4survivor", timestamp, getOriginVal("decre4survivor"), 0L);
        super.commit();
    }
}
