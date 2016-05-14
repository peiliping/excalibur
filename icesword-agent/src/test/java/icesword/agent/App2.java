package icesword.agent;

import icesword.agent.data.process.JvmItem;
import icesword.agent.service.JpsMonitorService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import sun.jvmstat.monitor.MonitorException;
import sun.jvmstat.monitor.MonitoredHost;
import sun.tools.attach.HotSpotVirtualMachine;
import sun.tools.jps.Arguments;

import com.sun.tools.attach.VirtualMachine;

public class App2 {

    private static Arguments        JPS_ARGUMENTS = new Arguments(new String[] {"-lmv"});
    private static SimpleDateFormat sdf           = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static Set<String>      whiteList     = new HashSet<String>();
    private static String           team          = "";
    private static String           app           = "";
    private static String           ip            = "";

    public static void main(String[] args) throws IOException {
        parseArgs(args);
        if (checkTimeDelay(500)) {
            System.out.println("{\"reason\":\"Now is too busy to analyse memory , wait a minute !\"}");
            System.exit(-1);
        }
        try {
            MonitoredHost monitoredHost = MonitoredHost.getMonitoredHost(JPS_ARGUMENTS.hostId());
            Set<?> jvms = monitoredHost.activeVms();
            for (Iterator<?> jvm = jvms.iterator(); jvm.hasNext();) {
                JvmItem jvmItem = new JvmItem(((Integer) jvm.next()).intValue());
                JpsMonitorService.buildJvmItem(monitoredHost, jvmItem);
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
        Date d = new Date();
        String date = sdf.format(d);
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
                sb.append("\"processType\":\"").append(jvmitem.mainClass).append("\",");
                sb.append("\"date\":\"").append(date).append("\",");
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
        if (jvmItem.mainClass.contains("jmap-ex.jar"))
            return false;
        if (!whiteList.isEmpty() && !whiteList.contains(jvmItem.mainClass))
            return false;
        return true;
    }

}