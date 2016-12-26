package jvmmonitor.agent.module;

import jvmmonitor.agent.Util;
import jvmmonitor.agent.monitor.MonitorItem;

/**
 * Created by peiliping on 16-12-21.
 */
public class ModuleZThreshold extends AbstractModule {

    public ModuleZThreshold(String moduleName, MonitorItem item) {
        super(moduleName, item);
        super.noChangeMetricNames = new String[] {"minorgc", "majorgc"};
        METRICNAME.put("minorgc", "sun.gc.collector.0.invocations");
        METRICNAME.put("majorgc", "sun.gc.collector.1.invocations");

        METRICNAME.put("current", "sun.gc.policy.tenuringThreshold");
        METRICNAME.put("max", "sun.gc.policy.maxTenuringThreshold");
        METRICNAME.put("incre4gc", "sun.gc.policy.incrementTenuringThresholdForGcCost");
        METRICNAME.put("decre4gc", "sun.gc.policy.decrementTenuringThresholdForGcCost");
        METRICNAME.put("decre4survivor", "sun.gc.policy.decrementTenuringThresholdForSurvivorLimit");
    }

    public void output(long timestamp) {
        super._output("current", timestamp, getOriginVal("current"));
        super._output("max", timestamp, getOriginVal("max"));
        super._output("incre4gc", timestamp, getDeltaVal("incre4gc"));
        super._output("decre4gc", timestamp, getDeltaVal("decre4gc"));
        super._output("decre4survivor", timestamp, getDeltaVal("decre4survivor"));
        super.output(timestamp);
    }
}
