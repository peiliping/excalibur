package meepo.tools;

public abstract class IWorker implements Runnable {

    protected volatile boolean run = true;

    @Override
    public void run() {
        while (run) {
            work();
        }
    }

    public abstract void work();

}
