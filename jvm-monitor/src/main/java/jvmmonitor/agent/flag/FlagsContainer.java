package jvmmonitor.agent.flag;

import java.util.Map;

import jvmmonitor.agent.monitor.DataContainer;
import jvmmonitor.agent.monitor.MonitorItem;
import lombok.Getter;
import lombok.Setter;

import com.google.common.collect.Maps;

/**
 * Created by peiliping on 16-12-29.
 */
@Setter @Getter public class FlagsContainer {

    private Map<String, String> meta = Maps.newHashMap();

    private Map<String, Map<String, Object>> data = Maps.newHashMap();

    public FlagsContainer(DataContainer dc, MonitorItem item) {
        this.meta.putAll(dc.getMeta());
        this.meta.put("type", "flag");
        Map<String, Object> cn = Maps.newHashMap();
        cn.put("jvminfo", item.getJVMInfo());
        cn.put("flags", item.getFlags());
        this.data.put(item.getMainClass(), cn);
    }
}
