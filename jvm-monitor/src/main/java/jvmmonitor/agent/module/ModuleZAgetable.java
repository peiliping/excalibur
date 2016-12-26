package jvmmonitor.agent.module;

import jvmmonitor.agent.Util;
import jvmmonitor.agent.monitor.MonitorItem;

/**
 * Created by peiliping on 16-12-21.
 */
public class ModuleZAgetable extends AbstractModule {

    private static String[] AGE_CONS =
            {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28",
                    "29", "30", "31"};

    private long ageTableSize = 0;

    public ModuleZAgetable(String moduleName, MonitorItem item) {
        super(moduleName, item);
        super.noChangeMetricNames = new String[] {"minorgc", "majorgc"};
        METRICNAME.put("minorgc", "sun.gc.collector.0.invocations");
        METRICNAME.put("majorgc", "sun.gc.collector.1.invocations");
        ageTableSize = Util.getLongValueFromMonitoredVm(item.getMonitoredVm(), "sun.gc.generation.0.agetable.size", 0) - 1;
        for (int i = 0; i < ageTableSize; i++) {
            METRICNAME.put("agetable-" + AGE_CONS[i], "sun.gc.generation.0.agetable.bytes." + AGE_CONS[i]);
        }
    }

    public void output(long timestamp) {
        long total = 0;
        long count = 0;
        for (int i = 0; i < ageTableSize; i++) {
            total = total + (i + 1) * getOriginVal(AGE_CONS[i]);
            count = count + getOriginVal(AGE_CONS[i]);
            super._output(AGE_CONS[i], timestamp, getOriginVal(AGE_CONS[i]));
        }
        super._output("avg", timestamp, (total * 1000) / (count == 0 ? 1 : count));
        super.output(timestamp);
    }
}
