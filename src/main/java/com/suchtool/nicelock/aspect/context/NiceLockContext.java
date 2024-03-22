package com.suchtool.nicelock.aspect.context;

import lombok.Data;

/**
 * 分布式锁的上下文信息
 */
@Data
public class NiceLockContext {
    /**
     * 键
     */
    private String key;

}