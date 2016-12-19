package phoenix.lru;

import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;
import com.googlecode.concurrentlinkedhashmap.Weighers;

public class LRUCache {

    public final String                                title;

    public AtomicBoolean                               IN_USE          = new AtomicBoolean(false);

    public static final long                           DEFAULT_EXPIRE  = 1000 * 60 * 60;

    public static final int                            DEFAULT_MAXSIZE = 5000;

    private AtomicLong                                 expiretime      = new AtomicLong(0);

    private ConcurrentLinkedHashMap<Object, CacheItem> cache;

    private AtomicLong                                 in              = new AtomicLong(0);

    private AtomicLong                                 hit             = new AtomicLong(0);

    private AtomicLong                                 maxsize         = new AtomicLong(0);

    public LRUCache(String title) {
        this(title, DEFAULT_MAXSIZE);
    }

    public LRUCache(String title, int maxsize) {
        this(title, maxsize, DEFAULT_EXPIRE);
    }

    public LRUCache(String title, int maxsize, long expiretime) {
        this.title = title;
        this.expiretime.set(expiretime);
        this.maxsize.set(maxsize);
        cache = new ConcurrentLinkedHashMap.Builder<Object, CacheItem>().maximumWeightedCapacity(maxsize).weigher(Weighers.singleton()).build();
        LRUCacheManager.regist(title, this);
    }

    public void clear() {
        in.set(0);
        hit.set(0);
        if (cache != null) {
            cache.clear();
        }
    }

    public void cleanStats() {
        in.set(0);
        hit.set(0);
    }

    public Object get(Object key) {

        if (!IN_USE.get()) {
            return null;
        }

        in.addAndGet(1);
        CacheItem item = cache.get(key);
        if (item != null) {
            if (item.isTimeOut(expiretime.get())) {
                cache.remove(key);
            } else {
                hit.addAndGet(1);
                return item.getValue();
            }
        }
        return null;
    }

    public void put(Object key, Object value) {
        if (!IN_USE.get() || cache.size() > maxsize.longValue() * 2) {
            return;
        }
        cache.put(key, new CacheItem(value));
    }

    public Object putIfAbsent(Object key, Object value) {
        if (!IN_USE.get() || cache.size() > maxsize.longValue() * 2) {
            return null;
        }
        return cache.putIfAbsent(key, new CacheItem(value));
    }

    public Object remove(Object key) {
        return cache.remove(key);
    }

    public String toLog() {
        return "title:" + title + " in:" + in + " hit:" + hit + " size:" + cache.size();
    }

    public Set<Object> dumpKey() {
        return cache.keySet();
    }

    public int size() {
        return cache.size();
    }

    public void configOnline(long expiretime, int maxsize) {
        this.expiretime.set(expiretime);
        this.cache.setCapacity(maxsize);
    }
}