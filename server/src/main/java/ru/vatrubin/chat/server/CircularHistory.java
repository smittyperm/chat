package ru.vatrubin.chat.server;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

class CircularHistory {
    private int circleSize;
    private int position;
    private String[] array;
    private boolean haveCircle;
    private ReadWriteLock lock;

    CircularHistory(int circleSize) {
        this.circleSize = circleSize;
        array = new String[circleSize];
        lock = new ReentrantReadWriteLock();
    }

    public void add(String message) {
        lock.writeLock().lock();
        try {
            array[position] = message;
            haveCircle = haveCircle || (position + 1) >= circleSize;
            position = (position + 1) % circleSize;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public String[] getArray() {
        String[] result = new String[haveCircle ? circleSize : position];
        lock.readLock().lock();
        try {
            for (int i = 0; i < result.length; i++) {
                if (i <= position) {
                    result[i] = array[haveCircle ? circleSize - position - 1 + i : i];
                } else {
                    result[i] = array[i];
                }
            }
            int j = 0;
            if (haveCircle) {
                for (int i = position; i < circleSize; i++) {
                    result[j++] = array[i];
                }
            }
            for (int i = 0; i < position; i++) {
                result[j++] = array[i];
            }
        } finally {
            lock.readLock().unlock();
        }
        return result;
    }

    public void clear() {
        position = 0;
        haveCircle = false;
        for (int i = 0; i < array.length; i++) {
            array[i] = null;
        }
    }
}
