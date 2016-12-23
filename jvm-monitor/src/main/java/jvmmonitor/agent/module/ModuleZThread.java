package jvmmonitor.agent.module;

import jvmmonitor.agent.monitor.MonitorItem;

/**
 * Created by peiliping on 16-12-21.
 */
public class ModuleZThread extends AbstractModule {

    public ModuleZThread(String moduleName, MonitorItem item) {
        super(moduleName, item);
        super.noChangeMetricNames = new String[] {"live", "started", "vmoperationtime"};
        METRICNAME.put("live", "java.threads.live");
        METRICNAME.put("started", "java.threads.started");
        METRICNAME.put("vmoperationtime", "sun.threads.vmOperationTime");
    }

    public void output() {
        super._output("live", getOriginVal("live"));
        super._output("create", getDeltaVal("started"));
        super._output("vmoperationtime", handleTimePrecision(getDeltaVal("vmoperationtime")));
    }
}
