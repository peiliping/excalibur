package jvmmonitor.agent.flag;

import com.google.common.collect.Maps;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

/**
 * Created by peiliping on 16-12-29.
 */
@Setter @Getter public class FlagsContainer {

    private Map<String, String> meta = Maps.newHashMap();

    private Map<String, List<JVMFlagItem>> data = Maps.newHashMap();
}
