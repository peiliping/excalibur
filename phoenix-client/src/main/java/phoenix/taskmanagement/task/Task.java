package phoenix.taskmanagement.task;

import java.util.ArrayList;
import java.util.List;

import phoenix.taskmanagement.Item;
import phoenix.taskmanagement.conditon.ICondition;
import phoenix.taskmanagement.exec.IAction;
import phoenix.taskmanagement.result.SimpleProccessResult;

public class Task extends Item {

    protected SimpleProccessResult result;

    protected List<ICondition>     conditions  = new ArrayList<ICondition>();

    protected List<IAction>        preActions  = new ArrayList<IAction>();

    protected List<IAction>        mainActions = new ArrayList<IAction>();

    protected List<IAction>        sufActions  = new ArrayList<IAction>();

}
