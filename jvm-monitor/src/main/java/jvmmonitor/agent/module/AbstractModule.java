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

    protected final Map<String, long[]> DATA = Maps.newHashMap();

    protected int seq = 0;

    protected long precision = 0;

    protected final int bufferSize = 2;

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
                    DATA.put(metric, new long[] {0, 0});
                }
            } catch (MonitorException e) {
                e.printStackTrace();
            }
        }
    }

    protected int cursor() {
        return seq & (bufferSize - 1);
    }

    protected int lastCursor(int n) {
        return (seq - n) & (bufferSize - 1);
    }

    protected int nextCursor() {
        return (seq + 1) & (bufferSize - 1);
    }

    public void monitor() {
        int cr = cursor();
        for (Map.Entry<String, LongMonitor> entry : MONITORS.entrySet()) {
            DATA.get(entry.getKey())[cr] = entry.getValue().longValue();
        }
        this.seq++;
    }

    protected void _output(String key, Long value) {
        System.out.printf("%-10s\t%-20s\t:\t%d\n", getModuleName(), key, value);
    }

    protected Long getOriginVal(String metric) {
        if (DATA.get(metric) == null) {
            return null;
        }
        return DATA.get(metric)[lastCursor(1)];
    }

    protected Long getDeltaVal(String metric) {
        if (DATA.get(metric) == null) {
            return null;
        }
        return seq > 1 ? (DATA.get(metric)[lastCursor(1)] - DATA.get(metric)[lastCursor(2)]) : null;
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
        if (noChangeMetricNames != null) {
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
