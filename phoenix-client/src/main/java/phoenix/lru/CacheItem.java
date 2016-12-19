package phoenix.lru;

public class CacheItem {

    private Object value;

    private long createTime;

    public CacheItem(Object value) {
        this.value = value;
        this.createTime = System.currentTimeMillis();
    }

    public boolean isTimeOut(long expiretime) {
        return expiretime == Long.MAX_VALUE ? false : (System.currentTimeMillis() - createTime > expiretime);
    }

    public Object getValue() {
        return value;
    }

    public void destroy() {
        value = null;
    }
}