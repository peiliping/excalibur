package sinper.histogram.service;

import org.HdrHistogram.Histogram;
import sinper.histogram.dataObject.meta.BaseMeta;
import sinper.histogram.dataObject.meta.Meta;
import sinper.histogram.dataObject.meta.WindowBaseMeta;
import sinper.histogram.dataObject.result.HistogramResult;

/**
 * Created by peiliping on 16-7-15.
 */
public class TimeWindowHistogramService extends BaseHistogramService {

    protected long interval;

    public TimeWindowHistogramService(long lowest, long highest, int precisions, long interval) {
        super(lowest, highest, precisions);
        this.interval = interval;

    }

    public TimeWindowHistogramService(long interval) {
        super();
        this.interval = interval;
    }

    protected long buildWindowTime(long timestamp) {
        return (timestamp / interval) * interval;
    }

    @Override protected String buildKey(String nameSpace, String metric, long timestamp) {
        return WindowBaseMeta.builder().nameSpace(nameSpace).metric(metric).windowTime(buildWindowTime(timestamp)).build().buildKey();
    }

    @Override protected Meta buildMeta(String nameSpace, String metric, long timestamp) {
        return WindowBaseMeta.builder().nameSpace(nameSpace).metric(metric).windowTime(buildWindowTime(timestamp)).build();
    }

    @Override protected HistogramResult buildResult(Meta baseMeta, Histogram hsm) {
        return HistogramResult.builder().nameSpace(baseMeta.getNameSpace()).metric(baseMeta.getMetric()).timeWindow(((WindowBaseMeta) baseMeta).getWindowTime())
                .startTime(hsm.getStartTimeStamp()).endTime(hsm.getEndTimeStamp()).totalCount(hsm.getTotalCount()).mean(hsm.getMean()).min(hsm.getMinValue()).max(hsm.getMaxValue())
                .p1(hsm.getValueAtPercentile(1)).p99(hsm.getValueAtPercentile(99)).footprint(buildFootPrint(hsm)).histogram(compressData(hsm)).build();
    }
}
