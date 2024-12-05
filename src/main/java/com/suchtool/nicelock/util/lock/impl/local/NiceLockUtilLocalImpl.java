package com.suchtool.nicelock.util.lock.impl.local;

import com.suchtool.nicelock.util.lock.NiceLockUtil;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class NiceLockUtilLocalImpl implements NiceLockUtil {
    private final Map<String, NiceLockLocalLockWrapper> lockMap = new ConcurrentHashMap<>();

    @Override
    public boolean tryLock(String key, long timeout, TimeUnit unit) {
        NiceLockLocalLockWrapper wrapper = getOrCreateLockWrapper(key);

        boolean locked = false;

        try {
            Lock lock = wrapper.getLock();
            locked = lock.tryLock(timeout, TimeUnit.MILLISECONDS);
            wrapper.getCount().getAndIncrement();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return locked;
    }

    @Override
    public synchronized void unlock(String key) {
        NiceLockLocalLockWrapper wrapper = getLockWrapper(key);
        if (wrapper == null) {
            return;
        }

        synchronized (wrapper) {
            int i = wrapper.getCount().decrementAndGet();
            if (i <= 0) {
                Lock lock = wrapper.getLock();
                lock.unlock();
                lockMap.remove(key);
            }
        }
    }

    private synchronized NiceLockLocalLockWrapper getOrCreateLockWrapper(String key) {
        NiceLockLocalLockWrapper wrapper = lockMap.get(key);
        if (wrapper == null) {
            wrapper = new NiceLockLocalLockWrapper();
            wrapper.setLock(new ReentrantLock());
        }

        return wrapper;
    }

    private NiceLockLocalLockWrapper getLockWrapper(String key) {
        return lockMap.get(key);
    }
}
