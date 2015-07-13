package phoenix.taskmanagement.task;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import phoenix.taskmanagement.basic.IAction;
import phoenix.taskmanagement.basic.Item;
import phoenix.taskmanagement.conditon.ICondition;
import phoenix.taskmanagement.priority.Priority;
import phoenix.taskmanagement.resource.Resource;
import phoenix.taskmanagement.result.SimpleProccessResult;

public class Task extends Item implements Callable<Boolean> {

    protected SimpleProccessResult result;

    protected Priority             priority;

    protected List<Resource>       preResource = new ArrayList<Resource>();

    protected List<ICondition>     conditions  = new ArrayList<ICondition>();

    protected List<IAction>        preActions  = new ArrayList<IAction>();

    protected List<IAction>        mainActions = new ArrayList<IAction>();

    protected List<IAction>        sufActions  = new ArrayList<IAction>();

    protected List<Resource>       sufResource = new ArrayList<Resource>();

    public List<Task>              subTasks    = new ArrayList<Task>();

    @Override
    public Boolean call() throws Exception {
        return null;
    }

}
