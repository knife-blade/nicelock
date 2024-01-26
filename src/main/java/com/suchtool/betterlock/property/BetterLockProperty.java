package com.suchtool.betterlock.property;

import lombok.Data;

@Data
public class BetterLockProperty {
    /**
     * 存到Redis里的key的前缀
     */
    private String keyPrefix;
}
