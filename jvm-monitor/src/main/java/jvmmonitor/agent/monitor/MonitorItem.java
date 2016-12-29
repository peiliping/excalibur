package jvmmonitor.agent.monitor;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import jvmmonitor.agent.Config;
import jvmmonitor.agent.Util;
import jvmmonitor.agent.flag.JVMFlagItem;
import jvmmonitor.agent.module.AbstractModule;
import jvmmonitor.agent.module.IModule;
import lombok.Builder;
import lombok.Getter;
import sun.jvmstat.monitor.MonitoredVm;

import java.util.List;
import java.util.Map;

/**
 * Created by peiliping on 16-12-20.
 */
@Builder @Getter public class MonitorItem {

    private int pid;

    private String mainClass;

    private transient MonitoredVm monitoredVm;

    private transient List<IModule> modules;

    private List<JVMFlagItem> flags;

    private String javaHome;

    private String javaVersion;

    private String vmName;

    private String vmVendor;

    private String vmVersion;

    private String vmMode;

    public void initBaseInfo() {
        this.javaHome = Util.getValueFromMonitoredVm(monitoredVm, "java.property.java.home");
        this.javaVersion = Util.getValueFromMonitoredVm(monitoredVm, "java.property.java.version");
        this.vmName = Util.getValueFromMonitoredVm(monitoredVm, "java.property.java.vm.name");
        this.vmVendor = Util.getValueFromMonitoredVm(monitoredVm, "java.property.java.vm.specification.vendor");
        this.vmVersion = Util.getValueFromMonitoredVm(monitoredVm, "java.property.java.vm.specification.version");
        this.vmMode = Util.getValueFromMonitoredVm(monitoredVm, "java.property.java.vm.info");
    }

    public void initJVMFlags() {
        this.flags = Util.parseFlags(String.valueOf(pid));
    }

    public void initModules(Config config) throws Exception {
        this.modules = Lists.newArrayList();
        for (String moduleName : config.getModules()) {
            IModule m = AbstractModule.build(Config.MODULES_CONS.get(moduleName), moduleName, this);
            modules.add(m);
        }
    }

    public void run() {
        long t = System.currentTimeMillis();
        for (IModule module : this.modules) {
            module.monitor(t);
            if (module.changed()) {
                module.transform(t);
            }
        }
    }

    private final Map<String, Map<String, long[][]>> resultBuffer = Maps.newHashMap();

    public Map<String, Map<String, long[][]>> getMetrics() {
        this.resultBuffer.clear();
        for (IModule module : this.modules) {
            this.resultBuffer.put(module.getModuleName(), module.pullData());
        }
        return this.resultBuffer;
    }

}
