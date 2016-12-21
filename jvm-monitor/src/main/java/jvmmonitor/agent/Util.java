package jvmmonitor.agent;

import com.google.common.collect.Sets;
import sun.jvmstat.monitor.LongMonitor;
import sun.jvmstat.monitor.MonitorException;
import sun.jvmstat.monitor.MonitoredVm;
import sun.jvmstat.monitor.StringMonitor;

import java.util.Set;

/**
 * Created by peiliping on 16-12-19.
 */
public class Util {

    public static Set<Integer> parse2IntSet(String args) {
        Set<Integer> result = Sets.newHashSet();
        if (args != null && args.trim().length() > 0) {
            String[] pidsStr = args.split(",");
            for (String pidStr : pidsStr) {
                result.add(Integer.valueOf(pidStr));
            }
        }
        return result;
    }

    public static Set<String> parse2StringSet(String args) {
        Set<String> result = Sets.newHashSet();
        if (args != null && args.trim().length() > 0) {
            String[] ekws = args.split(",");
            for (String ekw : ekws) {
                result.add(ekw);
            }
        }
        return result;
    }

    public static String getValueFromMonitoredVm(MonitoredVm mvm, String key) {
        StringMonitor sm = null;
        try {
            sm = (StringMonitor) mvm.findByName(key);
        } catch (MonitorException e) {
            e.printStackTrace();
        }
        return sm == null ? "Unknown" : sm.stringValue();
    }

    public static long getLongValueFromMonitoredVm(MonitoredVm mvm, String key, long def) {
        LongMonitor lm = null;
        try {
            lm = (LongMonitor) mvm.findByName(key);
        } catch (MonitorException e) {
            e.printStackTrace();
        }
        return lm == null ? def : lm.longValue();
    }

}
