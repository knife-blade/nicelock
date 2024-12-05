package com.suchtool.nicelock.util.lock.impl.redis;

import com.suchtool.nicelock.util.lock.NiceLockUtil;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.TimeUnit;

public class NiceLockUtilRedisImpl implements NiceLockUtil {
    @Autowired
    private RedissonClient redissonClient;

    @Override
    public boolean tryLock(String key, long timeout, TimeUnit unit) {
        RLock lock = redissonClient.getLock(key);
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
        RLock lock = redissonClient.getLock(key);
        lock.unlock();
    }
}
