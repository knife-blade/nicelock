package com.suchtool.nicelock.configuration;

import com.suchtool.nicelock.annotation.EnableNiceLock;
import com.suchtool.nicelock.aspect.NiceLockAspect;
import com.suchtool.nicelock.constant.StorageTypeEnum;
import com.suchtool.nicelock.property.NiceLockProperty;
import com.suchtool.nicelock.util.lock.NiceLockUtil;
import com.suchtool.nicelock.util.lock.impl.NiceLockUtilLocalImpl;
import com.suchtool.nicelock.util.lock.impl.NiceLockUtilRedisImpl;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportAware;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.lang.Nullable;

@Configuration(value = "com.suthtool.nicelock.niceLockConfiguration", proxyBeanMethods = false)
public class NiceLockConfiguration implements ImportAware {
    @Nullable
    protected AnnotationAttributes enableNiceLock;

    public void setImportMetadata(AnnotationMetadata importMetadata) {
        this.enableNiceLock = AnnotationAttributes.fromMap(
                importMetadata.getAnnotationAttributes(EnableNiceLock.class.getName(), false));
    }

    @Bean(name = "com.suchtool.nicelock.niceLockProperty")
    @ConfigurationProperties(prefix = "suchtool.nicelock")
    public NiceLockProperty niceLockProperty() {
        return new NiceLockProperty();
    }

    @Bean(name = "com.suchtool.nicelock.niceLockUtil")
    public NiceLockUtil niceLockUtil(NiceLockProperty niceLockProperty) {
        if (StorageTypeEnum.REDIS.equals(niceLockProperty.getStorageType())) {
            return new NiceLockUtilRedisImpl();
        } else {
            return new NiceLockUtilLocalImpl();
        }
    }

    @Bean(name = "com.suchtool.nicelock.niceLockAspect")
    public NiceLockAspect niceLockAspect(NiceLockUtil niceLockUtil,
                                         NiceLockProperty niceLockProperty) {
        int order = Ordered.LOWEST_PRECEDENCE;
        if (enableNiceLock != null) {
            order = enableNiceLock.<Integer>getNumber("order");
        }

        return new NiceLockAspect(niceLockProperty, niceLockUtil, order);
    }
}
