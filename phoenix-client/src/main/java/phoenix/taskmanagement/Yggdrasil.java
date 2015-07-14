package phoenix.taskmanagement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.Validate;

import phoenix.taskmanagement.resource.Resource;
import phoenix.taskmanagement.task.Task;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.TreeTraverser;

public class Yggdrasil {

    private final Task          root   = new Task();

    private Map<String, Task>   resMap = new HashMap<String, Task>();

    private TreeTraverser<Task> core   = new TreeTraverser<Task>() {
                                           @Override
                                           public Iterable<Task> children(Task root) {
                                               return root.subTasks;
                                           }
                                       };

    public boolean addTask(Task t) {
        if (t.getPreResource().size() == 0) {
            root.subTasks.add(t);
            t.getPriority().setLevel(1);
            addResMap(t);
            return true;
        } else if (canBeAdd(t)) {
            List<Task> tmpResult = new ArrayList<Task>();
            for (Resource r : t.getPreResource()) {
                tmpResult.add(resMap.get(r.toString()));
            }
            Collections.sort(tmpResult, new java.util.Comparator<Task>() {
                @Override
                public int compare(Task o1, Task o2) {
                    return Integer.compare(o1.getPriority().getLevel(), o2.getPriority().getLevel());
                }
            });
            Task parent = tmpResult.get(0);
            parent.subTasks.add(t);
            t.getPriority().setLevel(parent.getPriority().getLevel() + 1);
            addResMap(t);
            return true;
        }
        return false;
    }

    private void addResMap(Task t) {
        for (Resource r : t.getSufResource()) {
            if (resMap.containsKey(r.toString()))
                Validate.isTrue(false, "Task Conflict");
            resMap.put(r.toString(), t);
        }
    }

    public boolean canBeAdd(Task t) {
        for (Resource r : t.getPreResource()) {
            if (!resMap.containsKey(r.toString())) {
                return false;
            }
        }
        return true;
    }

    public void running() {
        for (Task task : core.breadthFirstTraversal(root)) {
            System.out.println(JSON.toJSONString(task));
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
        }
    }
}
