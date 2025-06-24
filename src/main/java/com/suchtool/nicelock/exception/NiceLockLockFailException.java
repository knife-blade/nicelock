package com.suchtool.nicelock.exception;

/**
 * 加锁失败异常
 */
public class NiceLockLockFailException extends RuntimeException {
    public NiceLockLockFailException() {
        super();
    }

    public NiceLockLockFailException(String message) {
        super(message);
    }

    public NiceLockLockFailException(String message, Throwable cause) {
        super(message, cause);
    }
}