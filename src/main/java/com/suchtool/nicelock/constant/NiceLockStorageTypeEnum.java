package com.suchtool.nicelock.constant;

import lombok.Getter;

@Getter
public enum NiceLockStorageTypeEnum {
    LOCAL("本地"),
    REDIS("Redis"),
    ;

    private final String description;

    NiceLockStorageTypeEnum(String description) {
        this.description = description;
    }
}
