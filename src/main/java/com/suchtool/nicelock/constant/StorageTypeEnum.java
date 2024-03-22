package com.suchtool.nicelock.constant;

import lombok.Getter;

@Getter
public enum StorageTypeEnum {
    LOCAL("本地"),
    REDIS("Redis"),
    ;

    private final String description;

    StorageTypeEnum(String description) {
        this.description = description;
    }
}
