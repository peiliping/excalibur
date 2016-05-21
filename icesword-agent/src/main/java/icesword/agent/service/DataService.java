package icesword.agent.service;

import icesword.agent.data.process.JstatItem;
import icesword.agent.data.process.JvmItem;
import icesword.agent.data.result.ResultData;

import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class DataService {

    private static AtomicLong                        CURRENT_POSITION = new AtomicLong(0);

    private static List<ResultData>                  MEMORY_DATA_POOL = Lists.newArrayList(new ResultData("JVMMemoryMetrics"), new ResultData("JVMMemoryMetrics"));
    private static List<ResultData>                  GC_DATA_POOL     = Lists.newArrayList(new ResultData("JVMGCMetrics"), new ResultData("JVMGCMetrics"));
    private static List<ResultData>                  AGE_DATA_POOL    = Lists.newArrayList(new ResultData("JVMAgeTableMetrics"), new ResultData("JVMAgeTableMetrics"));

    private static ConcurrentMap<Integer, JstatItem> CACHE            = Maps.newConcurrentMap();

    public static synchronized void oOOo() {
        CURRENT_POSITION.addAndGet(1);
    }

    public static synchronized void addData(JvmItem jvmItem, JstatItem jstatItem) {
        int p = Long.valueOf((CURRENT_POSITION.get() % 2)).intValue();
        if (CACHE.containsKey(jvmItem.pid)) {
            JstatItem now = jstatItem.compare(CACHE.get(jvmItem.pid));
            now.toIDataPool(MEMORY_DATA_POOL.get(p), GC_DATA_POOL.get(p), AGE_DATA_POOL.get(p));
        }
        CACHE.put(jvmItem.pid, jstatItem);
    }

    public static synchronized List<ResultData> getLastOne() {
        int p = Long.valueOf(((CURRENT_POSITION.get() + 1) % 2)).intValue();
        return Lists.newArrayList(MEMORY_DATA_POOL.get(p), GC_DATA_POOL.get(p), AGE_DATA_POOL.get(p));
    }

    public static synchronized void cleanLastOne() {
        int p = Long.valueOf(((CURRENT_POSITION.get() + 1) % 2)).intValue();
        GC_DATA_POOL.get(p).getData().clear();
        MEMORY_DATA_POOL.get(p).getData().clear();
        AGE_DATA_POOL.get(p).getData().clear();
    }

    public static synchronized void cleanCache(int pid) {
        CACHE.remove(pid);
    }

}
