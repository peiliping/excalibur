package jvmmonitor.agent.monitor;

import lombok.Builder;
import lombok.Getter;

/**
 * Created by peiliping on 16-12-27.
 */
@Builder @Getter public class JVMFlagItem<V> {

    private String flagName;

    private V value;

    private boolean original;

    private String type;

}
