package com.suchtool.nicelock.util.lock.impl;

import com.suchtool.nicelock.util.lock.NiceLockUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class NiceLockUtilLocalImpl implements NiceLockUtil {
    private final Map<String, Lock> lockMap = new HashMap<>();

    @Override
    public boolean tryLock(String key, long timeout, TimeUnit unit) {
        Lock lock = getLock(key);

        boolean locked = false;

        try {
            locked = lock.tryLock(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return locked;
    }

    @Override
    public void unlock(String key) {
        Lock lock = getLock(key);
        lockMap.remove(key);
        lock.unlock();
    }

    private Lock getLock(String key) {
        return lockMap.computeIfAbsent(key, k -> new ReentrantLock());
    }
}
