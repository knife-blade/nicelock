package com.suchtool.nicelock.property;

import lombok.Data;

@Data
public class NiceLockProperty {
    /**
     * 存到Redis里的key的前缀
     */
    private String keyPrefix;
}
