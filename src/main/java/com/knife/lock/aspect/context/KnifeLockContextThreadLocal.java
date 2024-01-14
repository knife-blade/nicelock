package com.knife.lock.aspect.context;

/**
 * 接口日志的上下文信息的读写
 */
public class KnifeLockContextThreadLocal {
    /**
     * 构造函数私有
     */
    private KnifeLockContextThreadLocal() {
    }

    private static final ThreadLocal<KnifeLockContext>
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
    public static void write(KnifeLockContext knifeLockContext) {
        DISTRIBUTION_LOCK_CONTEXT_THREAD_LOCAL.set(knifeLockContext);
    }

    /**
     * 获取当前日志上下文信息
     */
    public static KnifeLockContext read() {
        KnifeLockContext knifeLockContext = DISTRIBUTION_LOCK_CONTEXT_THREAD_LOCAL.get();
        if (knifeLockContext == null) {
            knifeLockContext = new KnifeLockContext();
        }
        return knifeLockContext;
    }
}