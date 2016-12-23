package jvmmonitor.agent.module;

import jvmmonitor.agent.monitor.MonitorItem;

/**
 * Created by peiliping on 16-12-21.
 */
public class ModuleZTenuringThreshold extends AbstractModule {

    public ModuleZTenuringThreshold(String moduleName, MonitorItem item) {
        super(moduleName, item);
        /**
         * TODO
         sun.gc.policy.maxTenuringThreshold	15
         sun.gc.policy.tenuringThreshold	15
         sun.gc.policy.incrementTenuringThresholdForGcCost  0
         sun.gc.policy.decrementTenuringThresholdForGcCost	0
         sun.gc.policy.decrementTenuringThresholdForSurvivorLimit	0
         sun.gc.policy.survivorOverflowed 0
         sun.gc.generation.0.agetable.bytes.00   0
         */
    }

    public void output() {
    }
}
