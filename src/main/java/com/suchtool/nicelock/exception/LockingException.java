package com.suchtool.nicelock.exception;

/**
 * 已锁定异常
 */
public class LockingException extends RuntimeException {
    public LockingException() {
        super();
    }

    public LockingException(String message) {
        super(message);
    }

    public LockingException(String message, Throwable cause) {
        super(message, cause);
    }
}