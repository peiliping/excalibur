package jvmmonitor.agent.module;

import jvmmonitor.agent.monitor.MonitorItem;

/**
 * Created by peiliping on 16-12-21.
 */
public class ModuleZTlab extends AbstractModule {

    public ModuleZTlab(MonitorItem item) {
        super(item);
        METRICNAME.put("/tlab/alloc", "sun.gc.tlab.alloc");
        METRICNAME.put("/tlab/allocthreads", "sun.gc.tlab.allocThreads");
        METRICNAME.put("/tlab/fills", "sun.gc.tlab.fills");
        METRICNAME.put("/tlab/gcwaste", "sun.gc.tlab.gcWaste");
    }

    public void output() {
        System.out.println("/tlab/alloc" + ":" + getOriginVal("/tlab/alloc"));
        System.out.println("/tlab/allocthreads" + ":" + getOriginVal("/tlab/allocthreads"));
        System.out.println("/tlab/fills" + ":" + getOriginVal("/tlab/fills"));
        System.out.println("/tlab/gcwaste" + ":" + getOriginVal("/tlab/gcwaste"));
    }
}
