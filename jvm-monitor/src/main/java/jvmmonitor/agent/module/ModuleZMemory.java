package jvmmonitor.agent.module;

import jvmmonitor.agent.monitor.MonitorItem;

/**
 * Created by peiliping on 16-12-21.
 */
public class ModuleZMemory extends AbstractModule {

    private String memoryGeneration0Space0Name = "/mem/eden";
    private String memoryGeneration0Space1Name = "/mem/s0";
    private String memoryGeneration0Space2Name = "/mem/s1";
    private String memoryGeneration1Space0Name = "/mem/old";

    private String memoryperm = "/mem/perm";

    private String memorycompressedclassspaceName = "/mem/ccs";
    private String memorymetaspaceName            = "/mem/meta";

    public ModuleZMemory(MonitorItem item) {
        super(item);
        METRICNAME.put(memoryGeneration0Space0Name + "/used", "sun.gc.generation.0.space.0.used");
        METRICNAME.put(memoryGeneration0Space0Name + "/capacity", "sun.gc.generation.0.space.0.capacity");
        METRICNAME.put(memoryGeneration0Space1Name + "/used", "sun.gc.generation.0.space.1.used");
        METRICNAME.put(memoryGeneration0Space1Name + "/capacity", "sun.gc.generation.0.space.1.capacity");
        METRICNAME.put(memoryGeneration0Space2Name + "/used", "sun.gc.generation.0.space.2.used");
        METRICNAME.put(memoryGeneration0Space2Name + "/capacity", "sun.gc.generation.0.space.2.capacity");
        METRICNAME.put(memoryGeneration1Space0Name + "/used", "sun.gc.generation.1.space.0.used");
        METRICNAME.put(memoryGeneration1Space0Name + "/capacity", "sun.gc.generation.1.space.0.capacity");

        if ("1.8".compareTo(super.item.getVmVersion()) >= 0) {
            METRICNAME.put(memorycompressedclassspaceName + "/used", "sun.gc.compressedclassspace.used");
            METRICNAME.put(memorycompressedclassspaceName + "/capacity", "sun.gc.compressedclassspace.capacity");
            METRICNAME.put(memorymetaspaceName + "/used", "sun.gc.metaspace.used");
            METRICNAME.put(memorymetaspaceName + "/capacity", "sun.gc.metaspace.capacity");
        } else {
            METRICNAME.put(memoryperm + "/used", "sun.gc.generation.2.space.0.used");
            METRICNAME.put(memoryperm + "/capacity", "sun.gc.generation.2.space.0.capacity");
        }

    }

    public void output() {
        System.out.println(memoryGeneration0Space0Name + "/used" + ":" + getOriginVal(memoryGeneration0Space0Name + "/used"));
        System.out.println(memoryGeneration0Space0Name + "/capacity" + ":" + getOriginVal(memoryGeneration0Space0Name + "/capacity"));
        System.out.println(memoryGeneration0Space1Name + "/used" + ":" + getOriginVal(memoryGeneration0Space0Name + "/used"));
        System.out.println(memoryGeneration0Space1Name + "/capacity" + ":" + getOriginVal(memoryGeneration0Space0Name + "/capacity"));
        System.out.println(memoryGeneration0Space2Name + "/used" + ":" + getOriginVal(memoryGeneration0Space0Name + "/used"));
        System.out.println(memoryGeneration0Space2Name + "/capacity" + ":" + getOriginVal(memoryGeneration0Space0Name + "/capacity"));
        System.out.println(memoryGeneration1Space0Name + "/used" + ":" + getOriginVal(memoryGeneration0Space0Name + "/used"));
        System.out.println(memoryGeneration1Space0Name + "/capacity" + ":" + getOriginVal(memoryGeneration0Space0Name + "/capacity"));
        if ("1.8".compareTo(super.item.getVmVersion()) >= 0) {
            System.out.println(memorycompressedclassspaceName + "/used" + ":" + getOriginVal(memorycompressedclassspaceName + "/used"));
            System.out.println(memorycompressedclassspaceName + "/capacity" + ":" + getOriginVal(memorycompressedclassspaceName + "/capacity"));
            System.out.println(memorymetaspaceName + "/used" + ":" + getOriginVal(memorymetaspaceName + "/used"));
            System.out.println(memorymetaspaceName + "/capacity" + ":" + getOriginVal(memorymetaspaceName + "/capacity"));
        } else {
            System.out.println(memoryperm + "/used" + ":" + getOriginVal(memoryperm + "/used"));
            System.out.println(memoryperm + "/capacity" + ":" + getOriginVal(memoryperm + "/capacity"));
        }
    }
}
