package jvmmonitor.agent.module;

import jvmmonitor.agent.monitor.MonitorItem;

/**
 * Created by peiliping on 16-12-21.
 */
public class ModuleZGCExtend extends AbstractModule {

    public ModuleZGCExtend(String moduleName, MonitorItem item) {
        super(moduleName, item);
        super.noChangeMetricNames = new String[] {"minorgc", "majorgc"};
        super.filterZeroValue = true;
        super.metricValuesNum = 3;
        super.addMetric("minorgc", "sun.gc.collector.0.invocations");
        super.addMetric("majorgc", "sun.gc.collector.1.invocations");

        super.addMetric("promoted", "sun.gc.policy.avgPromotedAvg");
        super.addMetric("survived", "sun.gc.policy.avgSurvivedAvg");

        super.addMetric("timelimitexceeded", "sun.gc.policy.gcTimeLimitExceeded");
        super.addMetric("survivoroverflowed", "sun.gc.policy.survivorOverflowed");
    }

    public void transform(long timestamp) {
        super.store("promoted", timestamp, getOriginVal("promoted"), 1L);
        super.store("survived", timestamp, getOriginVal("survived"), 1L);

        super.store("timelimitexceeded", timestamp, getOriginVal("timelimitexceeded"), 0L);
        super.store("survivoroverflowed", timestamp, getOriginVal("survivoroverflowed"), 0L);
        super.commit();
    }
}
