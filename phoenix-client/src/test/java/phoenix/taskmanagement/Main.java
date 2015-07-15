package phoenix.taskmanagement;

import phoenix.taskmanagement.task.Task;

public class Main {

    public static void main(String[] args) {

        Yggdrasil GG = new Yggdrasil();
        GG.addTask(Task.build(null, "/b"));
        GG.addTask(Task.build(null, "/c"));
        GG.addTask(Task.build("/a", "/d"));
        GG.addTask(Task.build("/b", "/e"));
        GG.addTask(Task.build(null, "/a"));
        GG.running();
    }
}
