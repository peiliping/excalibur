package jvmmonitor.agent.module;

import jvmmonitor.agent.monitor.MonitorItem;

/**
 * Created by peiliping on 16-12-21.
 */
public class ModuleZTlab extends AbstractModule {

    public ModuleZTlab(String moduleName, MonitorItem item) {
        super(moduleName, item);
        super.noChangeMetricNames = new String[] {"alloc"};
        METRICNAME.put("alloc", "sun.gc.tlab.alloc");
        METRICNAME.put("allocthreads", "sun.gc.tlab.allocThreads");
        METRICNAME.put("fills", "sun.gc.tlab.fills");
        METRICNAME.put("gcwaste", "sun.gc.tlab.gcWaste");
    }

    public void output() {
        super._output("alloc", getOriginVal("alloc"));
        super._output("allocthreads", getOriginVal("allocthreads"));
        super._output("fills", getOriginVal("fills"));
        super._output("gcwaste", getOriginVal("gcwaste"));
    }
}
