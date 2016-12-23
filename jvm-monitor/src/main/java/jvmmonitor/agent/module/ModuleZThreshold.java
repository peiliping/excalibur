package jvmmonitor.agent.module;

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
        //TODO sun.gc.generation.0.agetable.bytes.00
    }

    public void output() {
        super._output("current", getOriginVal("current"));
        super._output("max", getOriginVal("max"));
        super._output("incre4gc", getDeltaVal("incre4gc"));
        super._output("decre4gc", getDeltaVal("decre4gc"));
        super._output("decre4survivor", getDeltaVal("decre4survivor"));
    }
}
