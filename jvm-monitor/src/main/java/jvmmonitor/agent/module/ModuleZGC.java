package jvmmonitor.agent.module;

import jvmmonitor.agent.Util;
import jvmmonitor.agent.monitor.MonitorItem;

/**
 * Created by peiliping on 16-12-21.
 */
public class ModuleZGC extends AbstractModule {

    private String garbageCollector0Name;
    private String garbageCollector1Name;

    public ModuleZGC(MonitorItem item) {
        super(item);
        this.garbageCollector0Name = "/gc/" + Util.getValueFromMonitoredVm(item.getMonitoredVm(), "sun.gc.collector.0.name");
        this.garbageCollector1Name = "/gc/" + Util.getValueFromMonitoredVm(item.getMonitoredVm(), "sun.gc.collector.1.name");
        METRICNAME.put(garbageCollector0Name + "/count", "sun.gc.collector.0.invocations");
        METRICNAME.put(garbageCollector0Name + "/time", "sun.gc.collector.0.time");
        METRICNAME.put(garbageCollector1Name + "/count", "sun.gc.collector.1.invocations");
        METRICNAME.put(garbageCollector1Name + "/time", "sun.gc.collector.1.time");
        /**
         * TODO
         *
         agetable
         sun.gc.policy.avgBaseFootprint	0
         sun.gc.policy.avgMajorIntervalTime	0
         sun.gc.policy.avgMajorPauseTime	0
         sun.gc.policy.avgMinorIntervalTime	0
         sun.gc.policy.avgMinorPauseTime	0
         sun.gc.policy.avgOldLive	0
         sun.gc.policy.avgPretenuredPaddedAvg	0
         sun.gc.policy.avgPromotedAvg	32505856
         sun.gc.policy.avgPromotedDev	0
         sun.gc.policy.avgPromotedPaddedAvg	32505856
         sun.gc.policy.avgSurvivedAvg	5242880
         sun.gc.policy.avgSurvivedDev	0
         sun.gc.policy.avgSurvivedPaddedAvg	5242880
         sun.gc.policy.avgYoungLive	0
         sun.gc.policy.boundaryMoved	0
         sun.gc.policy.changeOldGenForMajPauses	0
         sun.gc.policy.changeOldGenForMinPauses	0
         sun.gc.policy.changeYoungGenForMajPauses	0
         sun.gc.policy.changeYoungGenForMinPauses	0
         sun.gc.policy.collectors	2
         sun.gc.policy.decideAtFullGc	0
         sun.gc.policy.decreaseForFootprint	0
         sun.gc.policy.decrementTenuringThresholdForGcCost	0
         sun.gc.policy.decrementTenuringThresholdForSurvivorLimit	0
         sun.gc.policy.desiredSurvivorSize	0
         sun.gc.policy.edenSize	32505856
         sun.gc.policy.freeSpace	65011712
         sun.gc.policy.fullFollowsScavenge	0
         sun.gc.policy.gcTimeLimitExceeded	0
         sun.gc.policy.generations	3
         sun.gc.policy.increaseOldGenForThroughput	0
         sun.gc.policy.increaseYoungGenForThroughput	0
         sun.gc.policy.incrementTenuringThresholdForGcCost	0
         sun.gc.policy.liveAtLastFullGc	32505856
         sun.gc.policy.liveSpace	0
         sun.gc.policy.majorCollectionSlope	0
         sun.gc.policy.majorGcCost	0
         sun.gc.policy.majorPauseOldSlope	0
         sun.gc.policy.majorPauseYoungSlope	0
         sun.gc.policy.maxTenuringThreshold	15
         sun.gc.policy.minorCollectionSlope	0
         sun.gc.policy.minorGcCost	0
         sun.gc.policy.minorPauseOldSlope	0
         sun.gc.policy.minorPauseTime	0
         sun.gc.policy.minorPauseYoungSlope	0
         sun.gc.policy.mutatorCost	1
         sun.gc.policy.name	ParScav:MSC
         sun.gc.policy.oldCapacity	130023424
         sun.gc.policy.oldEdenSize	32505856
         sun.gc.policy.oldPromoSize	32505856
         sun.gc.policy.promoSize	32505856
         sun.gc.policy.promoted	0
         sun.gc.policy.scavengeSkipped	0
         sun.gc.policy.survived	0
         sun.gc.policy.survivorOverflowed	0
         sun.gc.policy.tenuringThreshold	15
         sun.gc.policy.youngCapacity	37748736
         */
    }

    public void output() {
        System.out.println(garbageCollector0Name + "/count" + ":" + getDeltaVal(garbageCollector0Name + "/count"));
        System.out.println(garbageCollector0Name + "/time" + ":" + handleTimePrecision(getDeltaVal(garbageCollector0Name + "/time")));
        System.out.println(garbageCollector1Name + "/count" + ":" + getDeltaVal(garbageCollector1Name + "/count"));
        System.out.println(garbageCollector1Name + "/time" + ":" + handleTimePrecision(getDeltaVal(garbageCollector1Name + "/time")));
    }
}
