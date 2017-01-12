package jvmmonitor.agent.module;

import jvmmonitor.agent.monitor.MonitorItem;

/**
 * Created by peiliping on 16-12-21.
 */
public class ModuleZThread extends AbstractModule {

    public ModuleZThread(String moduleName, MonitorItem item) {
        super(moduleName, item);
        super.noChangeMetricNames = new String[] {"live", "started"};
        super.atLeastOnce4NoChange = true;
        super.filterZeroValue = true;
        super.metricValuesNum = 3;
        super.addMetric(item, "live", "java.threads.live");
        super.addMetric(item, "started", "java.threads.started");
    }

    public void transform(long timestamp) {
        super.store("live", timestamp, getOriginVal("live"), 1L);
        super.store("starting", timestamp, getDeltaVal("started"), 0L);
        super.commit();
    }
}
