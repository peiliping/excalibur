package meepo.storage;

import java.util.List;

import meepo.tools.RingBuffer;
import meepo.tools.RingBuffer.Mode;

public class RamRingBufferStorage<E> implements IStorage<E> {

    private RingBuffer<E> BUFFER;

    public RamRingBufferStorage(int size) {
        this.BUFFER = new RingBuffer<E>(size);
    }

    @Override
    public void add(E e) {
        BUFFER.add(e, Mode.MODE_BLOCKING);
    }

    @Override
    public E get() {
        return BUFFER.get(Mode.MODE_BLOCKING);
    }

    @Override
    public List<E> get(int size) {
        return BUFFER.get(Mode.MODE_BLOCKING, size, 1000);
    }

    @Override
    public int getCurrentSize() {
        return BUFFER.curSize();
    }

}
