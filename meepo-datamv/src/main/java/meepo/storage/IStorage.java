package meepo.storage;

import java.util.List;

import meepo.storage.plugin.IPlugin;

public interface IStorage<E> {

    public void add(E e);

    public E get();

    public List<E> get(int size);

    public int getCurrentSize();

    public void addPlugin(IPlugin<E> p);
}
