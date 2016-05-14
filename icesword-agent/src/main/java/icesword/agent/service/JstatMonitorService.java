package icesword.agent.service;

import icesword.agent.data.process.JvmItem;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class JstatMonitorService {

    private ExecutorService                   executor   = Executors.newFixedThreadPool(20);
    private ConcurrentMap<Integer, Future<?>> processing = new ConcurrentHashMap<Integer, Future<?>>();

    public synchronized void cleanDoneFuture() {
        for (Map.Entry<Integer, Future<?>> one : processing.entrySet()) {
            if (one.getValue().isCancelled() || one.getValue().isDone()) {
                processing.remove(one.getKey());
            }
        }
    }

    public synchronized void addJVMs(List<JvmItem> jvms, long interval) {
        for (JvmItem item : jvms) {
            if (!processing.containsKey(item.pid)) {
                Future<?> task = executor.submit(JstatWorker.builder().item(item).interval(interval).build());
                processing.put(item.pid, task);
            }
        }
    }
}
