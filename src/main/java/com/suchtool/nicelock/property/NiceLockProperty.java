package com.suchtool.nicelock.property;

import com.suchtool.nicelock.constant.StorageTypeEnum;
import lombok.Data;

@Data
public class NiceLockProperty {
    /**
     * 存储方式
     */
    private StorageTypeEnum storageType = StorageTypeEnum.LOCAL;

    /**
     * 存到Redis里的key的前缀
     */
    private String keyPrefix = "nicelock";
}
