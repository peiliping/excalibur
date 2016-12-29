package jvmmonitor.agent.flag;

import lombok.Builder;
import lombok.Getter;

/**
 * Created by peiliping on 16-12-27.
 */
@Builder @Getter public class JVMFlagItem {

    private String flagName;

    private Object value;

    private boolean original;

    private String type;

}
