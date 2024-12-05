package com.suchtool.nicelock.util.lock.impl.local;

import lombok.Data;

import java.util.concurrent.locks.Lock;

@Data
public class NiceLockLocalLockWrapper {
    private int count = 0;

    private Lock lock;
}
