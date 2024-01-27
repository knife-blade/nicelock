package com.suchtool.nicelock.aspect.context;

/**
 * 接口日志的上下文信息的读写
 */
public class NiceLockContextThreadLocal {
    /**
     * 构造函数私有
     */
    private NiceLockContextThreadLocal() {
    }

    private static final ThreadLocal<NiceLockContext>
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
    public static void write(NiceLockContext niceLockContext) {
        DISTRIBUTION_LOCK_CONTEXT_THREAD_LOCAL.set(niceLockContext);
    }

    /**
     * 获取当前日志上下文信息
     */
    public static NiceLockContext read() {
        NiceLockContext niceLockContext = DISTRIBUTION_LOCK_CONTEXT_THREAD_LOCAL.get();
        if (niceLockContext == null) {
            niceLockContext = new NiceLockContext();
        }
        return niceLockContext;
    }
}