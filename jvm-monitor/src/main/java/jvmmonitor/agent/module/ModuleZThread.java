package jvmmonitor.agent.module;

import jvmmonitor.agent.monitor.MonitorItem;

/**
 * Created by peiliping on 16-12-21.
 */
public class ModuleZThread extends AbstractModule {

    public ModuleZThread(String moduleName, MonitorItem item) {
        super(moduleName, item);
        METRICNAME.put("live", "java.threads.live");
        METRICNAME.put("started", "java.threads.started");
        METRICNAME.put("vmoperationtime", "sun.threads.vmOperationTime");
    }

    public void output(long timestamp) {
        super._output("live", timestamp, getOriginVal("live"));
        super._output("create", timestamp, getDeltaVal("started"));
        super._output("vmoperationtime", timestamp, handleTimePrecision(getDeltaVal("vmoperationtime")));
        super.output(timestamp);
    }
}
