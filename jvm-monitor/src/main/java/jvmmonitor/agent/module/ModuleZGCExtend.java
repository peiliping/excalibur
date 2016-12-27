package jvmmonitor.agent.module;

import jvmmonitor.agent.Util;
import jvmmonitor.agent.monitor.MonitorItem;

/**
 * Created by peiliping on 16-12-21.
 */
public class ModuleZGCExtend extends AbstractModule {

    public ModuleZGCExtend(String moduleName, MonitorItem item) {
        super(moduleName, item);
        super.noChangeMetricNames = new String[] {"minorgc", "majorgc"};
        METRICNAME.put("minorgc", "sun.gc.collector.0.invocations");
        METRICNAME.put("majorgc", "sun.gc.collector.1.invocations");

        METRICNAME.put("promoted", "sun.gc.policy.avgPromotedAvg");
        METRICNAME.put("survived", "sun.gc.policy.avgSurvivedAvg");

        METRICNAME.put("timelimitexceeded", "sun.gc.policy.gcTimeLimitExceeded");
        METRICNAME.put("survivoroverflowed", "sun.gc.policy.survivorOverflowed");
    }

    public void output(long timestamp) {
        super._output("promoted", timestamp, getOriginVal("promoted"));
        super._output("survived", timestamp, getOriginVal("survived"));

        super._output("timelimitexceeded", timestamp, getDeltaVal("timelimitexceeded"));
        super._output("survivoroverflowed", timestamp, getDeltaVal("survivoroverflowed"));
        super.output(timestamp);
    }
}
