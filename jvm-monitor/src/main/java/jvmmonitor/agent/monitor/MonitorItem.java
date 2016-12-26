package jvmmonitor.agent.monitor;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import jvmmonitor.agent.Config;
import jvmmonitor.agent.module.AbstractModule;
import jvmmonitor.agent.module.IModule;
import jvmmonitor.agent.Util;
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

    private String javaHome;

    private String javaVersion;

    private String vmName;

    private String vmVendor;

    private String vmVersion;

    private String vmMode;

    private int counter = 1;

    public void initBaseInfo() {
        this.javaHome = Util.getValueFromMonitoredVm(monitoredVm, "java.property.java.home");
        this.javaVersion = Util.getValueFromMonitoredVm(monitoredVm, "java.property.java.version");
        this.vmName = Util.getValueFromMonitoredVm(monitoredVm, "java.property.java.vm.name");
        this.vmVendor = Util.getValueFromMonitoredVm(monitoredVm, "java.property.java.vm.specification.vendor");
        this.vmVersion = Util.getValueFromMonitoredVm(monitoredVm, "java.property.java.vm.specification.version");
        this.vmMode = Util.getValueFromMonitoredVm(monitoredVm, "java.property.java.vm.info");
    }

    public void initJVMFlags() {

    }

    public void initModules(Config config) throws Exception {
        this.modules = Lists.newArrayList();
        for (String moduleName : config.getModules()) {
            IModule m = AbstractModule.build(config.MODULES_CONS.get(moduleName), moduleName, this);
            m.init();
            modules.add(m);
        }
    }

    private final Map<String, Map<String, long[][]>> G = Maps.newHashMap();

    public void run() {
        long t = System.currentTimeMillis();
        boolean pull = (counter % 10 == 0);
        if (pull) {
            G.clear();
        }
        for (IModule module : modules) {
            module.monitor(t);
            if (module.noChange()) {
                //SKIP
            } else {
                module.output(t);
            }
            if (pull) {
                G.put(module.getModuleName(), module.pullData());
            }
        }
        counter++;
        if(pull){
            System.out.println(JSON.toJSONString(G));
        }
    }

}
