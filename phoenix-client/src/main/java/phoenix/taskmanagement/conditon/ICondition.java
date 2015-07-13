package phoenix.taskmanagement.conditon;

import phoenix.taskmanagement.basic.IAction;

public abstract class ICondition extends IAction {

    public abstract void check();

    @Override
    public void action() {
        check();
    }

}
