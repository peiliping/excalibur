package jvmmonitor.agent.monitor;

import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * Created by peiliping on 16-12-26.
 */
@Getter @Setter public class DataContainer {

    private Map<String, String> meta = Maps.newHashMap();

    private Map<String, Map<String, Map<String, long[][]>>> data = Maps.newHashMap();

}
