package sniper.histogram.service;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Charsets;
import com.google.common.io.Files;
import org.HdrHistogram.ConcurrentHistogram;
import org.HdrHistogram.Histogram;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sniper.histogram.dataObject.meta.BaseMeta;
import sniper.histogram.dataObject.meta.Meta;
import sniper.histogram.dataObject.result.HistogramResult;

import javax.xml.bind.DatatypeConverter;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.zip.Deflater;

import static java.nio.ByteOrder.BIG_ENDIAN;

/**
 * Created by peiliping on 16-7-15.
 */
public class BaseHistogramService {

    protected static Logger LOG = LoggerFactory.getLogger(BaseHistogramService.class);

    protected final ConcurrentHashMap<String, Pair<Meta, Histogram>> writerMap = new ConcurrentHashMap<>();

    protected final ConcurrentSkipListMap<String, Pair<Meta, Histogram>> readerMap = new ConcurrentSkipListMap<>();

    protected final long lowest;

    protected final long highest;

    protected final int precisions;

    public BaseHistogramService(long lowest, long highest, int precisions) {
        this.lowest = lowest;
        this.highest = highest;
        this.precisions = precisions;
    }

    public BaseHistogramService() {
        this(1, Long.MAX_VALUE, 3);
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
        return BaseMeta.builder().nameSpace(nameSpace).metric(metric).build().buildKey();
    }

    protected Meta buildMeta(String nameSpace, String metric, long timestamp) {
        return BaseMeta.builder().nameSpace(nameSpace).metric(metric).build();
    }

    protected Histogram buildHistogram(long startTime) {
        Histogram hsm = new ConcurrentHistogram(lowest, highest, precisions);
        hsm.setAutoResize(true);
        hsm.setStartTimeStamp(startTime);
        return hsm;
    }

    public ConcurrentNavigableMap<String, Pair<Meta, Histogram>> acquireAllData() {
        return readerMap.clone();
    }

    public void shuffle(ConcurrentNavigableMap<String, Pair<Meta, Histogram>> part, String path) throws IOException {
        File file = new File(path);
        for (Map.Entry<String, Pair<Meta, Histogram>> val : part.entrySet()) {
            Meta baseMeta = val.getValue().getLeft();
            Histogram hsm = val.getValue().getRight();
            hsm.setEndTimeStamp(baseMeta.getLastModifyTime().get());
            HistogramResult result = buildResult(baseMeta, hsm);
            Files.append(JSON.toJSONString(result) + "\n", file, Charsets.UTF_8);
            readerMap.remove(val.getKey());
            writerMap.remove(val.getKey());
        }
    }

    protected HistogramResult buildResult(Meta baseMeta, Histogram hsm) {
        return HistogramResult.builder().nameSpace(baseMeta.getNameSpace()).metric(baseMeta.getMetric()).timeWindow(null).startTime(hsm.getStartTimeStamp())
                .endTime(hsm.getEndTimeStamp()).totalCount(hsm.getTotalCount()).mean(hsm.getMean()).min(hsm.getMinValue()).max(hsm.getMaxValue()).p1(hsm.getValueAtPercentile(1))
                .p99(hsm.getValueAtPercentile(99)).footprint(buildFootPrint(hsm)).histogram(compressData(hsm)).build();
    }


    protected String compressData(Histogram hsm) {
        ByteBuffer targetBuffer = ByteBuffer.allocate(hsm.getNeededByteBufferCapacity()).order(BIG_ENDIAN);
        targetBuffer.clear();
        int compressedLength = hsm.encodeIntoCompressedByteBuffer(targetBuffer, Deflater.BEST_COMPRESSION);
        byte[] compressedArray = Arrays.copyOf(targetBuffer.array(), compressedLength);
        return DatatypeConverter.printBase64Binary(compressedArray);
    }

    protected long[] buildFootPrint(Histogram hsm) {
        long[] fp = new long[21];
        for (int i = 0; i < 21; i++)
            fp[i] = hsm.getValueAtPercentile(i * 5);
        return fp;
    }

}
