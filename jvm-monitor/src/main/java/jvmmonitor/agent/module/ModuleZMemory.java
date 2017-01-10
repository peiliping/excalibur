package jvmmonitor.agent.module;

import jvmmonitor.agent.monitor.MonitorItem;

/**
 * Created by peiliping on 16-12-21.
 */
public class ModuleZMemory extends AbstractModule {

    private String memoryGeneration0Space0Name = "eden";
    private String memoryGeneration0Space1Name = "s0";
    private String memoryGeneration0Space2Name = "s1";
    private String memoryGeneration1Space0Name = "old";

    private String memorySurvivorName = "survivordesired";

    private String memoryPermName = "perm";

    private String memoryCompressedClassSpaceName = "ccs";
    private String memoryMetaSpaceName            = "meta";

    private boolean greaterThanOrEqualJava1_8 = true;

    private boolean hasDesiredSurvivor = true;

    public ModuleZMemory(String moduleName, MonitorItem item) {
        super(moduleName, item);
        super.metricValuesNum = 3;
        super.noChangeMetricNames = new String[] {"eden/used", "old/used"};
        super.atLeastOnce4NoChange = true;
        this.greaterThanOrEqualJava1_8 = ("1.8".compareTo(item.getVmVersion()) >= 0);

        super.addMetric(item, memoryGeneration0Space0Name + "/used", "sun.gc.generation.0.space.0.used");
        super.addMetric(item, memoryGeneration0Space0Name + "/capacity", "sun.gc.generation.0.space.0.capacity");
        super.addMetric(item, memoryGeneration0Space1Name + "/used", "sun.gc.generation.0.space.1.used");
        super.addMetric(item, memoryGeneration0Space1Name + "/capacity", "sun.gc.generation.0.space.1.capacity");
        super.addMetric(item, memoryGeneration0Space2Name + "/used", "sun.gc.generation.0.space.2.used");
        super.addMetric(item, memoryGeneration0Space2Name + "/capacity", "sun.gc.generation.0.space.2.capacity");
        super.addMetric(item, memoryGeneration1Space0Name + "/used", "sun.gc.generation.1.space.0.used");
        super.addMetric(item, memoryGeneration1Space0Name + "/capacity", "sun.gc.generation.1.space.0.capacity");

        this.hasDesiredSurvivor = super.addMetric(item, memorySurvivorName + "/capacity", "sun.gc.policy.desiredSurvivorSize");

        if (this.greaterThanOrEqualJava1_8) {
            super.addMetric(item, memoryCompressedClassSpaceName + "/used", "sun.gc.compressedclassspace.used");
            super.addMetric(item, memoryCompressedClassSpaceName + "/capacity", "sun.gc.compressedclassspace.capacity");
            super.addMetric(item, memoryMetaSpaceName + "/used", "sun.gc.metaspace.used");
            super.addMetric(item, memoryMetaSpaceName + "/capacity", "sun.gc.metaspace.capacity");
        } else {
            super.addMetric(item, memoryPermName + "/used", "sun.gc.generation.2.space.0.used");
            super.addMetric(item, memoryPermName + "/capacity", "sun.gc.generation.2.space.0.capacity");
        }
    }

    public void transform(long timestamp) {
        super.store(memoryGeneration0Space0Name, timestamp, getOriginVal(memoryGeneration0Space0Name + "/used"), getOriginVal(memoryGeneration0Space0Name + "/capacity"));
        super.store(memoryGeneration0Space1Name, timestamp, getOriginVal(memoryGeneration0Space1Name + "/used"), getOriginVal(memoryGeneration0Space1Name + "/capacity"));
        super.store(memoryGeneration0Space2Name, timestamp, getOriginVal(memoryGeneration0Space2Name + "/used"), getOriginVal(memoryGeneration0Space2Name + "/capacity"));
        super.store(memoryGeneration1Space0Name, timestamp, getOriginVal(memoryGeneration1Space0Name + "/used"), getOriginVal(memoryGeneration1Space0Name + "/capacity"));

        if (hasDesiredSurvivor)
            super.store(memorySurvivorName, timestamp, 0L, getOriginVal(memorySurvivorName + "/capacity"));

        if (this.greaterThanOrEqualJava1_8) {
            super.store(memoryCompressedClassSpaceName, timestamp, getOriginVal(memoryCompressedClassSpaceName + "/used"),
                    getOriginVal(memoryCompressedClassSpaceName + "/capacity"));
            super.store(memoryMetaSpaceName, timestamp, getOriginVal(memoryMetaSpaceName + "/used"), getOriginVal(memoryMetaSpaceName + "/capacity"));
        } else {
            super.store(memoryPermName, timestamp, getOriginVal(memoryPermName + "/used"), getOriginVal(memoryPermName + "/capacity"));
        }
        super.commit();
    }
}
