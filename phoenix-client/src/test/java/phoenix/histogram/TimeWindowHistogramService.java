package phoenix.histogram;

import org.HdrHistogram.Histogram;
import org.apache.commons.lang3.tuple.Pair;

import java.util.concurrent.ConcurrentNavigableMap;

/**
 * Created by peiliping on 16-7-14.
 */
public class TimeWindowHistogramService extends BaseHistogramService {

    protected long interval;

    protected int windowSize;

    public TimeWindowHistogramService(long lowest, long highest, int precisions, long interval, int windowSize) {
        super(lowest, highest, precisions);
        this.interval = interval;
        this.windowSize = windowSize;
    }

    public TimeWindowHistogramService(long interval, int windowSize) {
        super();
        this.interval = interval;
        this.windowSize = windowSize;
    }

    protected long buildWindowTime(long timestamp) {
        return (timestamp / interval) * interval;
    }

    @Override protected String buildKey(String nameSpace, String metric, long timestamp) {
        return WindowMeta.builder().nameSpace(nameSpace).metric(metric).windowTime(buildWindowTime(timestamp)).build().buildKey();
    }

    @Override protected Meta buildMeta(String nameSpace, String metric, long timestamp) {
        return WindowMeta.builder().nameSpace(nameSpace).metric(metric).windowTime(buildWindowTime(timestamp)).build();
    }

    @Override protected HistogramResult buildResult(Meta meta, Histogram hsm) {
        return HistogramResult.builder().nameSpace(meta.getNameSpace()).metric(meta.getMetric()).timeWindow(((WindowMeta) meta).getWindowTime()).startTime(hsm.getStartTimeStamp())
                .endTime(hsm.getEndTimeStamp()).totalCount(hsm.getTotalCount()).mean(hsm.getMean() / UNIT).min(hsm.getMinValue() / UNIT).max(hsm.getMaxValue() / UNIT)
                .p1(hsm.getValueAtPercentile(1) / UNIT).p5(hsm.getValueAtPercentile(5) / UNIT).p25(hsm.getValueAtPercentile(25) / UNIT).p50(hsm.getValueAtPercentile(50) / UNIT)
                .p75(hsm.getValueAtPercentile(75) / UNIT).p95(hsm.getValueAtPercentile(95) / UNIT).p99(hsm.getValueAtPercentile(99) / UNIT).histogram(compressData(hsm)).build();
    }
}
