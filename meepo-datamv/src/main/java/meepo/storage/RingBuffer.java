package meepo.storage;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RingBuffer<E> {

    private static final Logger LOG = LoggerFactory.getLogger(RingBuffer.class);
    private Object[] RING;
    private int      size;
    private int      mask;
    private long     blocktime;

    private AtomicReference<Integer> WP;
    private AtomicReference<Integer> RP;

    public RingBuffer(int size, long blocktime) {
        RING = new Object[size];
        Validate.isTrue(size > 2 && Integer.bitCount(size) == 1, "Size must be a power of 2 and greater than 2");
        this.size = size;
        this.mask = size - 1;
        this.blocktime = blocktime;
        WP = new AtomicReference<Integer>(0);
        RP = new AtomicReference<Integer>(0);
    }

    public RingBuffer(int size) {
        this(size, 10);
    }

    private boolean checkFull(Integer w) {
        return mod(w) == RP.get();
    }

    private boolean checkEmpty(Integer r) {
        return WP.get().equals(r);
    }

    public int curSize() {
        return (WP.get() + size - RP.get()) % size;
    }

    public void add(E e, int mode) {
        for (; ; ) {
            Integer cur = WP.get();
            if (checkFull(cur)) {
                if (mode == Mode.MODE_SKIP) {
                    return;
                }
                if (mode == Mode.MODE_BLOCKING) {
                    block();
                }
            } else {
                if (WP.compareAndSet(cur, mod(cur))) {
                    // while (RING[cur] != null) {//没有可能进入
                    // Thread.yield();
                    // }
                    RING[cur] = e;
                    return;
                }
            }
        }
    }

    @SuppressWarnings("unchecked") public E get(int mode) {
        for (; ; ) {
            Integer cur = RP.get();
            if (checkEmpty(cur)) {
                if (mode == Mode.MODE_SKIP) {
                    return null;
                }
                if (mode == Mode.MODE_BLOCKING) {
                    block();
                    return get(Mode.MODE_SKIP);
                }
            } else {
                Object o = RING[cur];
                if (RP.compareAndSet(cur, mod(cur))) {
                    while (o == null) {
                        Thread.yield();
                        o = RING[cur];
                    }
                    RING[cur] = null;
                    return (E) o;
                }
            }
        }
    }

    public List<E> get(int mode, int size, long timeout) {
        List<E> result = new LinkedList<E>();
        long t1 = System.currentTimeMillis();
        for (int i = 0; i < size; i++) {
            E e = get(mode);
            if (e != null) {
                result.add(e);
            }
            if (timeout > 0 && timeout < System.currentTimeMillis() - t1) {
                break;
            }
        }
        return result;
    }

    public int size() {
        return size;
    }

    private int mod(int i) {
        return (i + 1) & mask;
    }

    private void block() {
        try {
            Thread.sleep(blocktime);
        } catch (InterruptedException e) {
            LOG.error("RingBuffer Block:", e);
        }
    }

    public interface Mode {
        public static final int MODE_SKIP     = 0;
        public static final int MODE_BLOCKING = 1;
    }
}
