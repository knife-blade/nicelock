package com.knife.lock.aspect.context;
import lombok.Data;
import org.redisson.api.RLock;

/**
 * 分布式锁的上下文信息
 */
@Data
public class KnifeLockContext {
    /**
     * 键
     */
    private String key;

    /**
     * 锁
     */
    private RLock lock;
}