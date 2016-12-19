package phoenix.lru;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import lombok.Getter;
import lombok.Setter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LRUCacheManager {

    private static Logger                                    logger  = LoggerFactory.getLogger(LRUCacheManager.class); ;

    private static final ConcurrentHashMap<String, LRUCache> MANAGER = new ConcurrentHashMap<String, LRUCache>();

    public synchronized static void regist(String key, LRUCache cache) {
        if (MANAGER.contains(key)) {
            MANAGER.put(key + "|" + cache.toString(), cache);
        } else {
            MANAGER.put(key, cache);
        }
    }

    @Setter
    @Getter
    private boolean needLog      = false;
    @Setter
    @Getter
    private long    scheduleTime = 2 * 60 * 1000;

    private Timer   timer;

    public void init() {
        if (needLog) {
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    toLog(true);
                }
            }, 0, scheduleTime);
        }
    }

    private void toLog(boolean clean2Zero) {
        Iterator<Entry<String, LRUCache>> it = MANAGER.entrySet().iterator();
        Entry<String, LRUCache> e;
        while (it.hasNext()) {
            e = it.next();
            logger.warn(e.getKey() + "\t" + e.getValue().toLog());
            if (clean2Zero)
                e.getValue().cleanStats();
        }
    }
}