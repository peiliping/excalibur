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
        return seq & 1;
    }

    protected int nextCursor() {
        return (seq + 1) & 1;
    }

    public void monitor() {
        int cr = cursor();
        for (Map.Entry<String, LongMonitor> entry : MONITORS.entrySet()) {
            DATA.get(entry.getKey())[cr] = entry.getValue().longValue();
        }
        this.seq++;
    }

    protected void _output(String key, long value) {
        System.out.printf("%-10s\t%-20s\t:\t%d\n", getModuleName(), key, value);
    }

    protected long getOriginVal(String metric) {
        return DATA.get(metric)[nextCursor()];
    }

    protected long getDeltaVal(String metric) {
        return seq > 2 ? (DATA.get(metric)[nextCursor()] - DATA.get(metric)[cursor()]) : 0;
    }

    protected long handleTimePrecision(long time) {
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
                if (getDeltaVal(ncmn) != 0) {
                    return false;
                }
            }
        }
        return true;
    }
}
