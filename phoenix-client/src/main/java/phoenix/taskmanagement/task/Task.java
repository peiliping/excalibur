package phoenix.taskmanagement.task;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import lombok.Getter;
import phoenix.taskmanagement.basic.IAction;
import phoenix.taskmanagement.basic.Item;
import phoenix.taskmanagement.conditon.ICondition;
import phoenix.taskmanagement.priority.Priority;
import phoenix.taskmanagement.resource.Resource;
import phoenix.taskmanagement.result.SimpleProccessResult;

public class Task extends Item implements Callable<Boolean> {

    @Getter
    protected SimpleProccessResult result;
    @Getter
    protected Priority             priority;
    @Getter
    protected List<Resource>       preResource = new ArrayList<Resource>();
    @Getter
    protected List<ICondition>     conditions  = new ArrayList<ICondition>();
    @Getter
    protected List<IAction>        preActions  = new ArrayList<IAction>();
    @Getter
    protected List<IAction>        mainActions = new ArrayList<IAction>();
    @Getter
    protected List<IAction>        sufActions  = new ArrayList<IAction>();
    @Getter
    protected List<Resource>       sufResource = new ArrayList<Resource>();
    @Getter
    public List<Task>              subTasks    = new ArrayList<Task>();

    @Override
    public Boolean call() throws Exception {
        return null;
    }

}
