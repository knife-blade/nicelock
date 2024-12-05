package com.suchtool.nicelock.util.lock.impl.local;

import lombok.Data;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;

@Data
public class NiceLockLocalLockWrapper {
    private AtomicInteger count = new AtomicInteger(0);

    private Lock lock;
}
