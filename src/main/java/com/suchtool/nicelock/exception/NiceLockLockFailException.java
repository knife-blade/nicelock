package com.suchtool.nicelock.exception;

/**
 * 已锁定异常
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