package earthshaker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import sun.jvmstat.monitor.MonitorException;
import sun.jvmstat.monitor.MonitoredHost;
import sun.jvmstat.monitor.MonitoredVm;
import sun.jvmstat.monitor.MonitoredVmUtil;
import sun.jvmstat.monitor.VmIdentifier;
import sun.tools.attach.HotSpotVirtualMachine;
import sun.tools.jps.Arguments;

import com.sun.tools.attach.VirtualMachine;

public class App {

    private static Arguments   JPS_ARGUMENTS = new Arguments(new String[] {"-lmv"});
    private static Set<String> whiteList     = new HashSet<String>();
    private static String      team          = "";
    private static String      app           = "";
    private static String      ip            = "";

    public static void main(String[] args) throws IOException {
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
                    jmap(jvmItem.pid + "", false, jvmItem);
                }
            }
        } catch (MonitorException e) {
            e.printStackTrace();
        }
    }

    private static final String LIVE_OBJECTS_OPTION = "-live";
    private static final String ALL_OBJECTS_OPTION  = "-all";

    private static void jmap(String pid, boolean live, JvmItem jvmitem) throws IOException {
        long timestamp = System.currentTimeMillis();
        VirtualMachine vm = attach(pid);
        InputStream in = ((HotSpotVirtualMachine) vm).heapHisto(live ? LIVE_OBJECTS_OPTION : ALL_OBJECTS_OPTION);
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String line = null;
        int lineNum = 0;
        while ((line = br.readLine()) != null) {
            lineNum++;
            if (lineNum > 3 && lineNum < 100) {
                line = line.replaceAll("[' ']+", " ");
                String[] tmp = line.split(" ");
                StringBuffer sb = new StringBuffer();
                sb.append("{");
                sb.append("\"app\":\"").append(app).append("\",");
                sb.append("\"byteSize\":").append(Long.valueOf(tmp[3])).append(",");
                sb.append("\"count\":").append(Long.valueOf(tmp[2])).append(",");
                sb.append("\"dataType\":\"").append(tmp[4]).append("\",");
                sb.append("\"ip\":\"").append(ip).append("\",");
                sb.append("\"orderId\":").append(lineNum - 3).append(",");
                sb.append("\"team\":\"").append(team).append("\",");
                sb.append("\"timestamp\":").append(timestamp);
                sb.append("}");
                System.out.println(sb.toString());
            }
        }
        br.close();
        in.close();
        vm.detach();
    }

    private static VirtualMachine attach(String pid) {
        try {
            return VirtualMachine.attach(pid);
        } catch (Exception x) {
            x.printStackTrace();
            System.exit(1);
            return null;
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
            List<String> as = Arrays.asList(args);
            team = as.get(0);
            app = as.get(1);
            ip = as.get(2);
            if (as.size() > 3)
                whiteList.addAll(as.subList(3, as.size()));
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
            jvmItem.mainClass = "" + MonitoredVmUtil.mainClass(vm, JPS_ARGUMENTS.showLongPaths());
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
