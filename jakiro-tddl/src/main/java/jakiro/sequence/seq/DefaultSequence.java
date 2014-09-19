package jakiro.sequence.seq;

import jakiro.sequence.SequenceException;
import jakiro.sequence.dao.SequenceDao;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DefaultSequence implements Sequence {

    protected final Lock lock = new ReentrantLock();

    private SequenceDao  sequenceDao;

    public SequenceDao getSequenceDao() {
        return sequenceDao;
    }

    public void setSequenceDao(SequenceDao sequenceDao) {
        this.sequenceDao = sequenceDao;
    }

    private String keyName;

    public String getKeyName() {
        return keyName;
    }

    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }

    protected volatile SequenceRange currentRange;

    public long nextValue(String kn, int index, int total) throws SequenceException {

        kn = (kn == null ? keyName : kn);

        if (currentRange == null) {
            lock.lock();
            try {
                if (currentRange == null) {
                    currentRange = sequenceDao.nextRange(kn, index, total);
                }
            } finally {
                lock.unlock();
            }
        }
        long value = currentRange.getAndIncrement();
        if (value == -1) {
            lock.lock();
            try {
                for (;;) {
                    if (currentRange.isOver()) {
                        currentRange = sequenceDao.nextRange(kn, index, total);
                    }

                    value = currentRange.getAndIncrement();
                    if (value == -1) {
                        continue;
                    }

                    break;
                }
            } finally {
                lock.unlock();
            }
        }
        if (value < 0) {
            throw new SequenceException("Sequence value overflow, value = " + value);
        }
        return value;
    }
}
