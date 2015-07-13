package phoenix.taskmanagement.exec;

import phoenix.taskmanagement.basic.IAction;

public abstract class IExec extends IAction {

    public abstract void exec();

    @Override
    public void action() {
        exec();
    }
}
