package jvmmonitor.agent.module;

import jvmmonitor.agent.monitor.MonitorItem;

/**
 * Created by peiliping on 16-12-21.
 */
public class ModuleZThread extends AbstractModule {

    public ModuleZThread(String moduleName, MonitorItem item) {
        super(moduleName, item);
        super.noChangeMetricNames = new String[] {"live", "started", "vmoperationtime"};
        super.atLeastOnce4NoChange = true;
        super.addMetric("live", "java.threads.live");
        super.addMetric("started", "java.threads.started");
        super.addMetric("vmoperationtime", "sun.threads.vmOperationTime");
    }

    public void transform(long timestamp) {
        super.store("live", timestamp, getOriginVal("live"));
        super.store("create", timestamp, getDeltaVal("started"));
        super.store("vmoperationtime", timestamp, handleTimePrecision(getDeltaVal("vmoperationtime")));
        super.commit();
    }
}
