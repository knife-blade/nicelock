package com.suchtool.nicelock.util.lock.impl.local;

import com.suchtool.nicelock.util.lock.NiceLockUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class NiceLockUtilLocalImpl implements NiceLockUtil {
    private final Map<String, NiceLockLocalLockWrapper> lockMap = new HashMap<>();

    @Override
    public boolean tryLock(String key, long timeout, TimeUnit unit) {
        NiceLockLocalLockWrapper wrapper = getOrCreateLockWrapper(key);

        boolean locked;

        try {
            locked = wrapper.getLock().tryLock(timeout, TimeUnit.MILLISECONDS);
            wrapper.setCount(wrapper.getCount() + 1);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return locked;
    }

    @Override
    public void unlock(String key) {
        NiceLockLocalLockWrapper wrapper = getLockWrapper(key);
        if (wrapper == null) {
            return;
        }

        synchronized (wrapper) {
            int i = wrapper.getCount() - 1;
            wrapper.setCount(i);

            Lock lock = wrapper.getLock();
            lock.unlock();

            if (i <= 0) {
                lockMap.remove(key);
            }
        }
    }

    private synchronized NiceLockLocalLockWrapper getOrCreateLockWrapper(String key) {
        NiceLockLocalLockWrapper wrapper = lockMap.get(key);
        if (wrapper == null) {
            wrapper = new NiceLockLocalLockWrapper();
            wrapper.setLock(new ReentrantLock());
            lockMap.put(key, wrapper);
        }

        return wrapper;
    }

    private NiceLockLocalLockWrapper getLockWrapper(String key) {
        return lockMap.get(key);
    }
}
