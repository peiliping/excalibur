package jvmmonitor.agent.monitor;

import com.google.common.collect.Maps;
import jvmmonitor.agent.Config;
import jvmmonitor.agent.Util;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * Created by peiliping on 16-12-26.
 */
@Getter @Setter public class DataContainer {

    private Map<String, String> meta = Maps.newHashMap();

    private Map<String, Map<String, Map<String, long[][]>>> data = Maps.newHashMap();

    public void initMeta(Config config) {
        this.meta.put("ip", Util.getLocalIP());
        this.meta.put("type", "metric");
        this.meta.put("mode", config.getMode());
        if (config.getMode().equals("test")) {
            this.meta.put("app", config.getAppName());
        }
    }

}
