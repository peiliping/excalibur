package jvmmonitor.agent.module;

import jvmmonitor.agent.monitor.MonitorItem;

/**
 * Created by peiliping on 16-12-21.
 */
public class ModuleZTlab extends AbstractModule {

    public ModuleZTlab(String moduleName, MonitorItem item) {
        super(moduleName, item);
        super.noChangeMetricNames = new String[] {"minorgc", "majorgc"};
        METRICNAME.put("minorgc", "sun.gc.collector.0.invocations");
        METRICNAME.put("majorgc", "sun.gc.collector.1.invocations");

        METRICNAME.put("alloc", "sun.gc.tlab.alloc");
        METRICNAME.put("allocthreads", "sun.gc.tlab.allocThreads");
        METRICNAME.put("fills", "sun.gc.tlab.fills");
        METRICNAME.put("gcwaste", "sun.gc.tlab.gcWaste");
    }

    public void output(long timestamp) {
        super._output("alloc", timestamp, getOriginVal("alloc"));
        super._output("allocthreads", timestamp, getOriginVal("allocthreads"));
        super._output("fills", timestamp, getOriginVal("fills"));
        super._output("gcwaste", timestamp, getOriginVal("gcwaste"));
        super.output(timestamp);
    }
}
