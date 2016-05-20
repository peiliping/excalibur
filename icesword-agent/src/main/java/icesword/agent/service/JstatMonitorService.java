package icesword.agent.service;

import icesword.agent.data.process.JvmItem;
import icesword.agent.util.Pair;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class JstatMonitorService {

    private ExecutorService                                      executor   = Executors.newFixedThreadPool(20);
    private ConcurrentMap<Integer, Pair<Future<?>, JstatWorker>> processing = new ConcurrentHashMap<Integer, Pair<Future<?>, JstatWorker>>();

    public boolean isExistPid(int pid) {
        return processing.containsKey(pid);
    }

    public synchronized void cleanDoneFuture() {
        Iterator<Map.Entry<Integer, Pair<Future<?>, JstatWorker>>> it = processing.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, Pair<Future<?>, JstatWorker>> one = it.next();
            if (one.getValue().getLeft().isCancelled() || one.getValue().getLeft().isDone()) {
                it.remove();
                DataService.cleanOneCache(one.getKey());
            }
        }
    }

    public synchronized void addJVMs(List<JvmItem> jvms, int interval) {
        for (JvmItem item : jvms) {
            if (!processing.containsKey(item.pid)) {
                JstatWorker worker = JstatWorker.builder().item(item).interval(interval).build();
                Future<?> task = executor.submit(worker);
                processing.put(item.pid, new Pair<Future<?>, JstatWorker>(task, worker));
            }
        }
    }

    public synchronized void killAllAttach() {
        for (Map.Entry<Integer, Pair<Future<?>, JstatWorker>> one : processing.entrySet()) {
            one.getValue().getRight().stop();
            try {
                Thread.sleep(10 * 1000);
            } catch (InterruptedException e) {
            }
        }
    }
}
