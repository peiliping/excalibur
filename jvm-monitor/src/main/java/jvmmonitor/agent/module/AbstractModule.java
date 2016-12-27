package jvmmonitor.agent.module;

import com.google.common.collect.Maps;
import jvmmonitor.agent.Util;
import jvmmonitor.agent.monitor.MonitorItem;
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

    protected String moduleName;

    protected MonitorItem item;

    protected String[] noChangeMetricNames;

    protected final Map<String, LongMonitor> MONITORS = Maps.newHashMap();

    protected final Map<String, String> METRICNAME = Maps.newHashMap();

    protected final Map<String, long[][]> TEMPORARYDATA = Maps.newHashMap();

    protected final int TEMPORARYSIZE = 2;

    protected int temporarySeq = 0;

    protected final Map<String, long[][]> DATA = Maps.newHashMap();

    protected final int DATASIZE = 64;

    protected int dataWSeq = 0;

    protected int dataRSeq = 0;

    protected long precision = 0;

    public AbstractModule(String moduleName, MonitorItem item) {
        this.moduleName = moduleName;
        this.item = item;
        this.precision = Util.getLongValueFromMonitoredVm(item.getMonitoredVm(), "sun.os.hrt.frequency", 1000000000) / 1000000;// JVM默认精度为纳秒, 监控程序的精度在微秒即可
    }

    public void init() {
        for (Map.Entry<String, String> entry : METRICNAME.entrySet()) {
            addMetrics(entry.getKey());
        }
    }

    protected void addMetrics(String... metrics) {
        for (String metric : metrics) {
            try {
                LongMonitor lm = (LongMonitor) item.getMonitoredVm().findByName(METRICNAME.get(metric));
                if (lm != null) {
                    MONITORS.put(metric, lm);
                    long ts[][] = new long[TEMPORARYSIZE][2];
                    for (int i = 0; i < TEMPORARYSIZE; i++) {
                        ts[i] = new long[] {0, 0};
                    }
                    TEMPORARYDATA.put(metric, ts);
                }
            } catch (MonitorException e) {
                e.printStackTrace();
            }
        }
    }

    protected int cursor() {
        return temporarySeq & (TEMPORARYSIZE - 1);
    }

    protected int lastCursor(int n) {
        return (temporarySeq - n) & (TEMPORARYSIZE - 1);
    }

    protected int cursor4Data() {
        return dataWSeq & (DATASIZE - 1);
    }

    protected int nextCursor4Data(int n) {
        return (dataRSeq + n) & (DATASIZE - 1);
    }

    private int dataLength() {
        return dataWSeq - dataRSeq;
    }

    public Map<String, long[][]> pullData() {
        if (dataLength() > 0) {
            Map<String, long[][]> result = Maps.newHashMap();
            for (Map.Entry<String, long[][]> item : DATA.entrySet()) {
                long ts[][] = new long[dataLength()][2];
                for (int i = 0; i < dataLength(); i++) {
                    ts[i] = item.getValue()[nextCursor4Data(i)];
                }
                result.put(item.getKey(), ts);
            }
            dataRSeq = dataWSeq;
            return result;
        }
        return null;
    }

    public void monitor(long timestamp) {
        int cr = cursor();
        for (Map.Entry<String, LongMonitor> entry : MONITORS.entrySet()) {
            TEMPORARYDATA.get(entry.getKey())[cr][0] = timestamp;
            TEMPORARYDATA.get(entry.getKey())[cr][1] = entry.getValue().longValue();
        }
        this.temporarySeq++;
    }

    public void output(long timestamp) {
        this.dataWSeq++;
    }

    protected void _output(String key, long timestamp, Long value) {
        if (value == null)
            return;
        if (DATA.get(key) == null) {
            long ts[][] = new long[DATASIZE][2];
            for (int i = 0; i < DATASIZE; i++) {
                ts[i] = new long[] {0, 0};
            }
            DATA.put(key, ts);
        }
        DATA.get(key)[cursor4Data()][0] = timestamp;
        DATA.get(key)[cursor4Data()][1] = value;
    }

    protected Long getOriginVal(String metric) {
        if (TEMPORARYDATA.get(metric) == null) {
            return null;
        }
        return TEMPORARYDATA.get(metric)[lastCursor(1)][1];
    }

    protected Long getDeltaVal(String metric) {
        if (TEMPORARYDATA.get(metric) == null) {
            return null;
        }
        return temporarySeq > 1 ? (TEMPORARYDATA.get(metric)[lastCursor(1)][1] - TEMPORARYDATA.get(metric)[lastCursor(2)][1]) : null;
    }

    protected Long handleTimePrecision(Long time) {
        if (time == null) {
            return null;
        }
        return time / precision;
    }

    public String getModuleName() {
        return this.moduleName;
    }

    public boolean noChange() {
        if (noChangeMetricNames == null) {
            return false;
        } else {
            for (String ncmn : noChangeMetricNames) {
                Long t = getDeltaVal(ncmn);
                if (t != null && t != 0) {
                    return false;
                }
            }
        }
        return true;
    }
}
