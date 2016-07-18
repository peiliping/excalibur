package sinper.histogram.service;

import org.HdrHistogram.Histogram;
import org.apache.commons.lang3.tuple.Pair;
import sinper.histogram.dataObject.meta.BaseMeta;
import sinper.histogram.dataObject.meta.Meta;

import java.util.concurrent.ConcurrentNavigableMap;

/**
 * Created by peiliping on 16-7-15.
 */
public class RealTimeWindowHistogramService extends TimeWindowHistogramService {

    protected int windowSize;

    protected long delay;

    public RealTimeWindowHistogramService(long lowest, long highest, int precisions, long interval, int windowSize) {
        super(lowest, highest, precisions, interval);
        this.windowSize = windowSize;
        this.delay = interval * windowSize;
    }

    public RealTimeWindowHistogramService(long interval, int windowSize) {
        super(interval);
        this.windowSize = windowSize;
        this.delay = interval * windowSize;
    }

    @Override public void addRecord(String nameSpace, String metric, long timestamp, long value, long count) {
        if (checkTimeWindow(timestamp))
            return;
        super.addRecord(nameSpace, metric, timestamp, value, count);
    }

    protected boolean checkTimeWindow(long timestamp) {
        return Math.abs(now() - timestamp) > delay;
    }

    protected ConcurrentNavigableMap<String, Pair<Meta, Histogram>> acquireOverDueWindowsData() {
        long dueTime = buildWindowTime((now() - delay));
        return readerMap.headMap(String.valueOf(dueTime));
    }

    protected long now() {
        return System.currentTimeMillis();
    }
}
