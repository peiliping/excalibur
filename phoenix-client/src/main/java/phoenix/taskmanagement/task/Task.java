package phoenix.taskmanagement.task;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import phoenix.taskmanagement.basic.IAction;
import phoenix.taskmanagement.basic.Item;
import phoenix.taskmanagement.conditon.ICondition;
import phoenix.taskmanagement.priority.Priority;
import phoenix.taskmanagement.result.SimpleProccessResult;

public class Task extends Item implements Callable<Boolean> {

    protected SimpleProccessResult result;

    protected Priority             priority;

    protected List<ICondition>     conditions  = new ArrayList<ICondition>();

    protected List<IAction>        preActions  = new ArrayList<IAction>();

    protected List<IAction>        mainActions = new ArrayList<IAction>();

    protected List<IAction>        sufActions  = new ArrayList<IAction>();

    @Override
    public Boolean call() throws Exception {
        return null;
    }

}
