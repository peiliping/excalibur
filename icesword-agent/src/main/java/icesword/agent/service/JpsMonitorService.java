package icesword.agent.service;

import icesword.agent.data.JvmItem;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import sun.jvmstat.monitor.MonitorException;
import sun.jvmstat.monitor.MonitoredHost;
import sun.jvmstat.monitor.MonitoredVm;
import sun.jvmstat.monitor.MonitoredVmUtil;
import sun.jvmstat.monitor.VmIdentifier;
import sun.tools.jps.Arguments;

public class JpsMonitorService {

    public static Arguments JPS_ARGUMENTS = new Arguments(new String[] {"-lmv"});

    public static List<JvmItem> findWorkerJVM() {
        List<JvmItem> result = new ArrayList<JvmItem>();
        try {
            MonitoredHost monitoredHost = MonitoredHost.getMonitoredHost(JPS_ARGUMENTS.hostId());
            Set<?> jvms = monitoredHost.activeVms();
            for (Iterator<?> jvm = jvms.iterator(); jvm.hasNext();) {
                JvmItem jvmItem = new JvmItem(((Integer) jvm.next()).intValue());
                buildJvmItem(monitoredHost, jvmItem);
                if (filterJvm(jvmItem)) {
                    result.add(jvmItem);
                }
            }
        } catch (MonitorException e) {
            e.printStackTrace();
        }
        return result;
    }


    public static boolean filterJvm(JvmItem jvmItem) {
        if (jvmItem.mainClass.startsWith("sun.tools"))
            return false;
        if (jvmItem.mainClass.equals("icesword.agent.App"))
            return false;
        // if (jvmItem.mainClass.contains("jmap-ex.jar"))
        // return false;
        return true;
    }

    public static void buildJvmItem(MonitoredHost monitoredHost, JvmItem jvmItem) {
        MonitoredVm vm = null;
        String vmidString = "//" + jvmItem.pid + "?mode=r";
        String errorString = null;
        try {
            errorString = " -- process information unavailable";
            VmIdentifier id = new VmIdentifier(vmidString);
            vm = monitoredHost.getMonitoredVm(id, 0);
            errorString = " -- main class information unavailable";
            jvmItem.mainClass = "" + MonitoredVmUtil.mainClass(vm, JpsMonitorService.JPS_ARGUMENTS.showLongPaths());
            if (JpsMonitorService.JPS_ARGUMENTS.showMainArgs()) {
                errorString = " -- main args information unavailable";
                String mainArgs = MonitoredVmUtil.mainArgs(vm);
                if (mainArgs != null && mainArgs.length() > 0) {
                    jvmItem.mainArgs = mainArgs;
                }
            }
            if (JpsMonitorService.JPS_ARGUMENTS.showVmArgs()) {
                errorString = " -- jvm args information unavailable";
                String jvmArgs = MonitoredVmUtil.jvmArgs(vm);
                if (jvmArgs != null && jvmArgs.length() > 0) {
                    jvmItem.vmArgs = jvmArgs;
                }
            }
            if (JpsMonitorService.JPS_ARGUMENTS.showVmFlags()) {
                errorString = " -- jvm flags information unavailable";
                String jvmFlags = MonitoredVmUtil.jvmFlags(vm);
                if (jvmFlags != null && jvmFlags.length() > 0) {
                    jvmItem.vmFlags = jvmFlags;
                }
            }
            errorString = " -- detach failed";
            monitoredHost.detach(vm);
            errorString = null;
        } catch (Exception e) {
            System.out.println(errorString);
            e.printStackTrace();
        }
    }
}
