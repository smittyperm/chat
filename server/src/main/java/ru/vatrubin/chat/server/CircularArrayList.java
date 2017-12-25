package ru.vatrubin.chat.server;

import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CircularArrayList<E> extends ArrayList<E> {
    private Integer circleSize;
    private Lock lock = new ReentrantLock();

    CircularArrayList(Integer circleSize) {
        super(circleSize);
        this.circleSize = circleSize;
    }

    @Override
    public boolean add(E o) {
        lock.lock();
        if (circleSize <= super.size()) {
            remove(0);
        }
        boolean result =  super.add(o);
        lock.unlock();
        return result;
    }

    @Override
    public <T> T[] toArray(T[] a) {
        lock.lock();
        T[] result = super.toArray(a);
        lock.unlock();
        return result;
    }
}
