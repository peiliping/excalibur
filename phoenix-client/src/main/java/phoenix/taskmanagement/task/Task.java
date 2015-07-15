package phoenix.taskmanagement.task;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.commons.lang3.StringUtils;

import lombok.Getter;
import phoenix.taskmanagement.basic.IAction;
import phoenix.taskmanagement.basic.Item;
import phoenix.taskmanagement.conditon.ICondition;
import phoenix.taskmanagement.priority.Priority;
import phoenix.taskmanagement.resource.Resource;
import phoenix.taskmanagement.resource.ResourceType;
import phoenix.taskmanagement.result.SimpleProccessResult;

public class Task extends Item implements Callable<Boolean> {

    @Getter
    protected transient SimpleProccessResult result;
    @Getter
    protected transient Priority             priority;
    @Getter
    protected List<Resource>                 preResource = new ArrayList<Resource>();
    @Getter
    protected transient List<ICondition>     conditions  = new ArrayList<ICondition>();
    @Getter
    protected transient List<IAction>        preActions  = new ArrayList<IAction>();
    @Getter
    protected transient List<IAction>        mainActions = new ArrayList<IAction>();
    @Getter
    protected transient List<IAction>        sufActions  = new ArrayList<IAction>();
    @Getter
    protected List<Resource>                 sufResource = new ArrayList<Resource>();
    @Getter
    public transient List<Task>              subTasks    = new ArrayList<Task>();

    @Override
    public Boolean call() throws Exception {
        return null;
    }

    public static Task build(String prpath, String srpath) {
        Task t = new Task();
        t.priority = new Priority(0, 1);
        if (StringUtils.isNotBlank(prpath))
            t.getPreResource().add(new Resource(ResourceType.HDFS, prpath));
        if (StringUtils.isNotBlank(srpath))
            t.getSufResource().add(new Resource(ResourceType.HDFS, srpath));
        return t;
    }
}
