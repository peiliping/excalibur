package jvmmonitor.agent.module;

import com.google.common.collect.Maps;
import jvmmonitor.agent.Util;
import jvmmonitor.agent.monitor.MonitorItem;
import lombok.Getter;
import sun.jvmstat.monitor.LongMonitor;
import sun.jvmstat.monitor.MonitorException;

import java.util.Map;

/**
 * Created by peiliping on 16-12-21.
 */
public abstract class AbstractModule implements IModule {

    public static AbstractModule build(Class<? extends AbstractModule> clazz, String moduleName, MonitorItem item) throws Exception {
        return clazz.getConstructor(String.class, MonitorItem.class).newInstance(moduleName, item);
    }

    @Getter protected String moduleName;

    protected MonitorItem item;

    protected long precision = 0;


    protected String[] noChangeMetricNames;

    protected int metricValuesNum = 2;


    private final Map<String, String> metricsName = Maps.newHashMap();

    protected final Map<String, LongMonitor> monitors = Maps.newHashMap();


    protected final Map<String, long[][]> temporaryData = Maps.newHashMap();

    protected int tempDataSize = 2;

    protected int temporarySeq = 0;


    protected final Map<String, long[][]> resultDataRBuffer = Maps.newHashMap();

    protected int resultDataSize = 64;

    protected int dataWSeq = 0;

    protected int dataRSeq = 0;

    public AbstractModule(String moduleName, MonitorItem item) {
        this.moduleName = moduleName;
        this.item = item;
        this.precision = Util.getLongValueFromMonitoredVm(item.getMonitoredVm(), "sun.os.hrt.frequency", 1000000000) / 1000000;
    }

    protected void addMetric(String metricName, String perfDataName) {
        try {
            this.metricsName.put(metricName, perfDataName);
            LongMonitor lm = (LongMonitor) this.item.getMonitoredVm().findByName(perfDataName);
            if (lm != null) {
                this.monitors.put(metricName, lm);
                long ts[][] = new long[tempDataSize][2];
                for (int i = 0; i < tempDataSize; i++) {
                    ts[i] = new long[] {0, 0};
                }
                this.temporaryData.put(metricName, ts);
            }
        } catch (MonitorException e) {
            e.printStackTrace();
        }
    }

    protected int cursor4TempData() {
        return this.temporarySeq & (this.tempDataSize - 1);
    }

    protected int lastCursor4TempData(int n) {
        return (this.temporarySeq - n) & (this.tempDataSize - 1);
    }

    protected Long getOriginVal(String metric) {
        long[][] temp = this.temporaryData.get(metric);
        return temp != null ? temp[lastCursor4TempData(1)][1] : null;
    }

    protected Long getDeltaVal(String metric) {
        long[][] temp = this.temporaryData.get(metric);
        if (temp == null)
            return null;
        return this.temporarySeq > 1 ? (temp[lastCursor4TempData(1)][1] - temp[lastCursor4TempData(2)][1]) : null;
    }

    protected Long handleTimePrecision(Long time) {
        return (time != null) ? time / precision : null;
    }

    protected int cursor4Data() {
        return this.dataWSeq & (this.resultDataSize - 1);
    }

    protected int nextCursor4Data(int n) {
        return (this.dataRSeq + n) & (this.resultDataSize - 1);
    }

    private int dataLength() {
        return this.dataWSeq - this.dataRSeq;
    }

    public void monitor(long timestamp) {
        int cr = cursor4TempData();
        for (Map.Entry<String, LongMonitor> entry : this.monitors.entrySet()) {
            this.temporaryData.get(entry.getKey())[cr][0] = timestamp;
            this.temporaryData.get(entry.getKey())[cr][1] = entry.getValue().longValue();
        }
        this.temporarySeq++;
    }

    public boolean changed() {
        if (this.noChangeMetricNames == null) {
            return true;
        } else {
            for (String ncmn : this.noChangeMetricNames) {
                Long t = getDeltaVal(ncmn);
                if (t != null && t != 0)
                    return true;
            }
        }
        return false;
    }

    protected void commit() {
        this.dataWSeq++;
    }

    protected void store(String key, long timestamp, Long... values) {
        if (values == null)
            return;
        long[][] temp = this.resultDataRBuffer.get(key);
        if (temp == null) {
            long ts[][] = new long[this.resultDataSize][this.metricValuesNum];
            for (int i = 0; i < this.resultDataSize; i++) {
                ts[i] = new long[this.metricValuesNum];
            }
            this.resultDataRBuffer.put(key, ts);
            temp = ts;
        }
        long[] item = temp[cursor4Data()];
        item[0] = timestamp;
        for (int i = 0; i < values.length; i++) {
            item[i + 1] = (values[i] == null ? 0 : values[i]);
        }
    }

    private final Map<String, long[][]> pullBuffer = Maps.newHashMap();

    public Map<String, long[][]> pullData() {
        this.pullData().clear();
        if (dataLength() == 0)
            return this.pullBuffer;
        for (Map.Entry<String, long[][]> item : this.resultDataRBuffer.entrySet()) {
            long ts[][] = new long[dataLength()][this.metricValuesNum];
            for (int i = 0; i < dataLength(); i++) {
                ts[i] = item.getValue()[nextCursor4Data(i)];
            }
            this.pullBuffer.put(item.getKey(), ts);
        }
        this.dataRSeq = this.dataWSeq;
        return this.pullBuffer;
    }
}
