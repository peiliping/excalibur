package phoenix.taskmanagement;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import phoenix.taskmanagement.task.Task;

import com.google.common.collect.TreeTraverser;

public class Yggdrasil {

    private final Task             root   = new Task();

    private Map<String, Set<Task>> preMap = new HashMap<String, Set<Task>>();
    private Map<String, Set<Task>> sufMap = new HashMap<String, Set<Task>>();

    private TreeTraverser<Task>    core   = new TreeTraverser<Task>() {
                                              @Override
                                              public Iterable<Task> children(Task root) {
                                                  return root.subTasks;
                                              }
                                          };

    public void addTask(Task t) {

    }

    public void running() {
        for (Task task : core.breadthFirstTraversal(root)) {

        }
    }
}
