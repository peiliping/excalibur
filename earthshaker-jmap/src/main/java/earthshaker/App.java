package earthshaker;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import sun.jvmstat.monitor.MonitorException;
import sun.jvmstat.monitor.MonitoredHost;
import sun.jvmstat.monitor.MonitoredVm;
import sun.jvmstat.monitor.MonitoredVmUtil;
import sun.jvmstat.monitor.VmIdentifier;
import sun.tools.jps.Arguments;
import earthshaker.jps.JvmItem;

public class App {

    private static Arguments   JPS_ARGUMENTS = new Arguments(new String[] {"-lmv"});
    private static Set<String> whiteList     = new HashSet<String>();

    public static void main(String[] args) {
        parseArgs(args);
        if (checkTimeDelay(500)) {
            System.out.println("Now is too busy to analyse memory , wait a minute !");
            System.exit(-1);
        }
        try {
            MonitoredHost monitoredHost = MonitoredHost.getMonitoredHost(JPS_ARGUMENTS.hostId());
            Set<?> jvms = monitoredHost.activeVms();
            for (Iterator<?> jvm = jvms.iterator(); jvm.hasNext();) {
                JvmItem jvmItem = new JvmItem();
                jvmItem.pid = ((Integer) jvm.next()).intValue();
                buildJvmItem(monitoredHost, jvmItem);
                if (filterJvm(jvmItem)) {
                    System.out.println(jvmItem.pid);
                    // TODO jmap pid
                }
            }
        } catch (MonitorException e) {
            e.printStackTrace();
        }
    }

    private static boolean checkTimeDelay(long n) {
        long start = System.currentTimeMillis();
        try {
            Thread.sleep(n);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        long end = System.currentTimeMillis();
        return (end - start) - n > n;
    }

    private static void parseArgs(String[] args) {
        if (args != null && args.length > 0) {
            whiteList.addAll(Arrays.asList(args));
        }
    }

    private static boolean filterJvm(JvmItem jvmItem) {
        if (jvmItem.mainClass.equals("earthshaker.App"))
            return false;
        if (jvmItem.mainClass.startsWith("sun.tools"))
            return false;
        if (!whiteList.isEmpty() && !whiteList.contains(jvmItem.mainClass))
            return false;
        return true;
    }

    private static void buildJvmItem(MonitoredHost monitoredHost, JvmItem jvmItem) {
        MonitoredVm vm = null;
        String vmidString = "//" + jvmItem.pid + "?mode=r";
        String errorString = null;
        try {
            errorString = " -- process information unavailable";
            VmIdentifier id = new VmIdentifier(vmidString);
            vm = monitoredHost.getMonitoredVm(id, 0);
            errorString = " -- main class information unavailable";
            jvmItem.mainClass = " " + MonitoredVmUtil.mainClass(vm, JPS_ARGUMENTS.showLongPaths());
            if (JPS_ARGUMENTS.showMainArgs()) {
                errorString = " -- main args information unavailable";
                String mainArgs = MonitoredVmUtil.mainArgs(vm);
                if (mainArgs != null && mainArgs.length() > 0) {
                    jvmItem.mainArgs = mainArgs;
                }
            }
            if (JPS_ARGUMENTS.showVmArgs()) {
                errorString = " -- jvm args information unavailable";
                String jvmArgs = MonitoredVmUtil.jvmArgs(vm);
                if (jvmArgs != null && jvmArgs.length() > 0) {
                    jvmItem.vmArgs = jvmArgs;
                }
            }
            if (JPS_ARGUMENTS.showVmFlags()) {
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
