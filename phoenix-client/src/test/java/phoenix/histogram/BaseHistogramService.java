package phoenix.histogram;

import com.alibaba.fastjson.JSON;
import org.HdrHistogram.ConcurrentHistogram;
import org.HdrHistogram.Histogram;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.DatatypeConverter;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.zip.Deflater;

import static java.nio.ByteOrder.BIG_ENDIAN;

/**
 * Created by peiliping on 16-7-14.
 */
public class BaseHistogramService {

    protected static Logger LOG = LoggerFactory.getLogger(BaseHistogramService.class);

    protected final ConcurrentHashMap<String, Pair<Meta, Histogram>> writerMap = new ConcurrentHashMap<>();

    protected final ConcurrentSkipListMap<String, Pair<Meta, Histogram>> readerMap = new ConcurrentSkipListMap<>();

    protected final long lowest;

    protected final long highest;

    protected final int precisions;

    protected final static long UNIT = 1000L * 1000L;

    public BaseHistogramService(long lowest, long highest, int precisions) {
        this.lowest = lowest * UNIT;
        this.highest = highest * UNIT;
        this.precisions = precisions;
    }

    public BaseHistogramService() {
        this(1, 3600 * 1000, 3);
    }

    public void addRecord(String nameSpace, String metric, long timestamp, long value, long count) {
        String key = buildKey(nameSpace, metric, timestamp);
        Pair<Meta, Histogram> pair = writerMap.get(key);
        if (pair == null) {
            for (; ; ) {
                pair = Pair.of(buildMeta(nameSpace, metric, timestamp), buildHistogram(timestamp));
                Pair<Meta, Histogram> old = writerMap.putIfAbsent(key, pair);
                if (old == null) {
                    readerMap.put(key, pair);
                    break;
                }
            }
        }
        pair.getRight().recordValueWithCount(value, count);
        while (true) {
            long old = pair.getLeft().getLastModifyTime().get();
            if (timestamp < old || pair.getLeft().getLastModifyTime().compareAndSet(old, timestamp))
                break;
        }
    }

    public void addRecord(String nameSpace, String metric, long timestamp, long value) {
        addRecord(nameSpace, metric, timestamp, value, 1);
    }

    protected String buildKey(String nameSpace, String metric, long timestamp) {
        return Meta.builder().nameSpace(nameSpace).metric(metric).build().buildKey();
    }

    protected Meta buildMeta(String nameSpace, String metric, long timestamp) {
        return Meta.builder().nameSpace(nameSpace).metric(metric).build();
    }

    protected Histogram buildHistogram(long startTime) {
        Histogram hsm = new ConcurrentHistogram(lowest, highest, precisions);
        hsm.setAutoResize(true);
        hsm.setStartTimeStamp(startTime);
        return hsm;
    }

    protected ConcurrentNavigableMap<String, Pair<Meta, Histogram>> acquireAllData() {
        return readerMap.clone();
    }

    protected void shuffle(ConcurrentNavigableMap<String, Pair<Meta, Histogram>> part) {
        for (Map.Entry<String, Pair<Meta, Histogram>> val : part.entrySet()) {
            Meta meta = val.getValue().getLeft();
            Histogram hsm = val.getValue().getRight();
            hsm.setEndTimeStamp(meta.getLastModifyTime().get());
            HistogramResult result = buildResult(meta, hsm);
            System.out.println(JSON.toJSONString(result));
            readerMap.remove(val.getKey());
            writerMap.remove(val.getKey());
        }
    }

    protected HistogramResult buildResult(Meta meta, Histogram hsm) {
        return HistogramResult.builder().nameSpace(meta.getNameSpace()).metric(meta.getMetric()).timeWindow(null).startTime(hsm.getStartTimeStamp()).endTime(hsm.getEndTimeStamp())
                .totalCount(hsm.getTotalCount()).mean(hsm.getMean() / UNIT).min(hsm.getMinValue() / UNIT).max(hsm.getMaxValue() / UNIT).p1(hsm.getValueAtPercentile(1) / UNIT)
                .p5(hsm.getValueAtPercentile(5) / UNIT).p25(hsm.getValueAtPercentile(25) / UNIT).p50(hsm.getValueAtPercentile(50) / UNIT).p75(hsm.getValueAtPercentile(75) / UNIT)
                .p95(hsm.getValueAtPercentile(95) / UNIT).p99(hsm.getValueAtPercentile(99) / UNIT).histogram(compressData(hsm)).build();
    }


    protected String compressData(Histogram hsm) {
        ByteBuffer targetBuffer = ByteBuffer.allocate(hsm.getNeededByteBufferCapacity()).order(BIG_ENDIAN);
        targetBuffer.clear();
        int compressedLength = hsm.encodeIntoCompressedByteBuffer(targetBuffer, Deflater.BEST_COMPRESSION);
        byte[] compressedArray = Arrays.copyOf(targetBuffer.array(), compressedLength);
        return DatatypeConverter.printBase64Binary(compressedArray);
    }

}
