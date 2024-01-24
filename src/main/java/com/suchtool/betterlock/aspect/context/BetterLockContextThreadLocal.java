package com.suchtool.betterlock.aspect.context;

/**
 * 接口日志的上下文信息的读写
 */
public class BetterLockContextThreadLocal {
    /**
     * 构造函数私有
     */
    private BetterLockContextThreadLocal() {
    }

    private static final ThreadLocal<BetterLockContext>
            DISTRIBUTION_LOCK_CONTEXT_THREAD_LOCAL = new ThreadLocal<>();

    /**
     * 清除日志上下文信息
     */
    public static void clear() {
        DISTRIBUTION_LOCK_CONTEXT_THREAD_LOCAL.remove();
    }

    /**
     * 存储日志上下文信息
     */
    public static void write(BetterLockContext betterLockContext) {
        DISTRIBUTION_LOCK_CONTEXT_THREAD_LOCAL.set(betterLockContext);
    }

    /**
     * 获取当前日志上下文信息
     */
    public static BetterLockContext read() {
        BetterLockContext betterLockContext = DISTRIBUTION_LOCK_CONTEXT_THREAD_LOCAL.get();
        if (betterLockContext == null) {
            betterLockContext = new BetterLockContext();
        }
        return betterLockContext;
    }
}