package phoenix.taskmanagement.basic;

import lombok.Getter;
import phoenix.taskmanagement.result.SimpleResult;

public abstract class IAction extends Item {

    @Getter
    protected SimpleResult result;

    public abstract void action();

}
