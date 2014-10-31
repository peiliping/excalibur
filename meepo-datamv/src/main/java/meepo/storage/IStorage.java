package meepo.storage;

import java.util.List;

public interface IStorage<E> {

    public void add(E e);

    public E get();

    public List<E> get(int size);

    public int getCurrentSize();

    public void addPlugin(IPlugin<E> p);
}
