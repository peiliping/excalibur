package phantomlancer.ringbuffer;

import java.util.List;

import phantomlancer.ringbuffer.RingBuffer.Mode;
import phantomlancer.ringbuffer.plugin.IPlugin;

public class RamRingBufferStorage<E> implements IStorage<E> {

    private RingBuffer<E> BUFFER;

    private IPlugin<E>    plugin;

    public RamRingBufferStorage(int size) {
        this.BUFFER = new RingBuffer<E>(size);
    }

    @Override
    public void add(E e) {
        BUFFER.add(plugin == null ? e : plugin.convert(e), Mode.MODE_BLOCKING);
    }

    @Override
    public E get() {
        return BUFFER.get(Mode.MODE_BLOCKING);
    }

    @Override
    public List<E> get(int size) {
        return BUFFER.get(Mode.MODE_BLOCKING, size, 500);
    }

    @Override
    public int getCurrentSize() {
        return BUFFER.curSize();
    }

    @Override
    public void addPlugin(IPlugin<E> p) {
        this.plugin = p;
    }

}
