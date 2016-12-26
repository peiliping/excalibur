package jvmmonitor.agent.module;

import jvmmonitor.agent.Util;
import jvmmonitor.agent.monitor.MonitorItem;

/**
 * Created by peiliping on 16-12-21.
 */
public class ModuleZThreshold extends AbstractModule {

    private static String[] AGE_CONS =
            {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28",
                    "29", "30", "31"};

    private long ageTableSize = 0;

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

        ageTableSize = Util.getLongValueFromMonitoredVm(item.getMonitoredVm(), "sun.gc.generation.0.agetable.size", 0) - 1;

        for (int i = 0; i < ageTableSize; i++) {
            METRICNAME.put("agetable-" + AGE_CONS[i], "sun.gc.generation.0.agetable.bytes." + AGE_CONS[i]);
        }
    }

    public void output(long timestamp) {
        super._output("current", timestamp, getOriginVal("current"));
        super._output("max", timestamp, getOriginVal("max"));
        super._output("incre4gc", timestamp, getDeltaVal("incre4gc"));
        super._output("decre4gc", timestamp, getDeltaVal("decre4gc"));
        super._output("decre4survivor", timestamp, getDeltaVal("decre4survivor"));

        long r = 0;
        long sum = 0;
        for (int i = 0; i < ageTableSize; i++) {
            r = r + (i + 1) * getOriginVal("agetable-" + AGE_CONS[i]);
            sum = sum + getOriginVal("agetable-" + AGE_CONS[i]);
            super._output("agetable-" + AGE_CONS[i], timestamp, getOriginVal("agetable-" + AGE_CONS[i]));
        }
        super._output("avgage", timestamp, r / (sum == 0 ? 1 : sum));
        super.output(timestamp);
    }
}
