package com.suchtool.nicelock.exception;

/**
 * 已锁定异常
 */
public class LockFailException extends RuntimeException {
    public LockFailException() {
        super();
    }

    public LockFailException(String message) {
        super(message);
    }

    public LockFailException(String message, Throwable cause) {
        super(message, cause);
    }
}