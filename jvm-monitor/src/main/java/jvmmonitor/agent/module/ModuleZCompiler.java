package jvmmonitor.agent.module;

import jvmmonitor.agent.monitor.MonitorItem;

/**
 * Created by peiliping on 16-12-21.
 */
public class ModuleZCompiler extends AbstractModule {

    public ModuleZCompiler(MonitorItem item) {
        super(item);
        METRICNAME.put("/compiler/time", "java.ci.totalTime");
        METRICNAME.put("/compiler/count", "sun.ci.totalCompiles");
        METRICNAME.put("/compiler/osrtime", "sun.ci.osrTime");
        METRICNAME.put("/compiler/osrcount", "sun.ci.osrCompiles");
        METRICNAME.put("/compiler/bailouts", "sun.ci.totalBailouts");
        METRICNAME.put("/compiler/invalidates", "sun.ci.totalInvalidates");
    }

    public void output() {
        System.out.println("/compiler/time" + ":" + handleTimePrecision(getDeltaVal("/compiler/time")));
        System.out.println("/compiler/count" + ":" + getDeltaVal("/compiler/count"));
        System.out.println("/compiler/osrtime" + ":" + handleTimePrecision(getDeltaVal("/compiler/osrtime")));
        System.out.println("/compiler/osrcount" + ":" + getDeltaVal("/compiler/osrcount"));
        System.out.println("/compiler/bailouts" + ":" + getDeltaVal("/compiler/bailouts"));
        System.out.println("/compiler/invalidates" + ":" + getDeltaVal("/compiler/invalidates"));
    }
}
