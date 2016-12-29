package jvmmonitor.agent.flag;

import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;

/**
 * Created by peiliping on 16-12-29.
 */
@Builder @Getter public class FlagsContainer {

    private Map<String, String> meta;

    private Map<String, List<JVMFlagItem>> data;
}
