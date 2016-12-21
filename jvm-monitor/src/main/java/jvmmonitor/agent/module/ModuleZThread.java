package jvmmonitor.agent.module;

import jvmmonitor.agent.monitor.MonitorItem;

/**
 * Created by peiliping on 16-12-21.
 */
public class ModuleZThread extends AbstractModule {

    public ModuleZThread(MonitorItem item) {
        super(item);
        METRICNAME.put("/threads/live", "java.threads.live");
        METRICNAME.put("/threads/started", "java.threads.started");
        //TODO sun.threads.vmOperationTime
    }

    public void output() {
        System.out.println("/threads/live" + ":" + getOriginVal("/threads/live"));
        System.out.println("/threads/started" + ":" + getOriginVal("/threads/started"));
        System.out.println("/threads/create" + ":" + getDeltaVal("/threads/started"));
    }
}
