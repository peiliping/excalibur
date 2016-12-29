package jvmmonitor.agent.flag;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;

import com.google.common.collect.Maps;

/**
 * Created by peiliping on 16-12-29.
 */
@Setter @Getter public class FlagsContainer {

    private Map<String, String> meta = Maps.newHashMap();

    private Map<String, Map<String, Object>> data = Maps.newHashMap();
}
