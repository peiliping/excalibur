package jakiro.sequence.seq;

import java.util.concurrent.atomic.AtomicLong;

public class SequenceRange {

    private final long min;

    public long getMin() {
        return min;
    }

    private final long max;

    public long getMax() {
        return max;
    }

    private volatile boolean over = false;

    public boolean isOver() {
        return over;
    }

    private final AtomicLong value;

    public SequenceRange(long min, long max) {
        this.min = min;
        this.max = max;
        this.value = new AtomicLong(min);
    }

    public long getAndIncrement() {
        long currentValue = value.getAndIncrement();
        if (currentValue > max) {
            over = true;
            return -1;
        }
        return currentValue;
    }
}
