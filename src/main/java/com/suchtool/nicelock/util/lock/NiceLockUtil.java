package com.suchtool.nicelock.util.lock;

import java.util.concurrent.TimeUnit;

public interface NiceLockUtil {
    boolean tryLock(String key, long timeout, TimeUnit unit);

    void unlock(String key);
}
