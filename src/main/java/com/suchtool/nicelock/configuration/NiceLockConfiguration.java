package com.suchtool.nicelock.configuration;

import com.suchtool.nicelock.annotation.EnableNiceLock;
import com.suchtool.nicelock.aspect.NiceLockAspect;
import com.suchtool.nicelock.property.NiceLockProperty;
import org.redisson.api.RedissonClient;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportAware;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.lang.Nullable;

@Configuration(proxyBeanMethods = false)
public class NiceLockConfiguration implements ImportAware {
    @Nullable
    protected AnnotationAttributes enableNiceLock;

    public void setImportMetadata(AnnotationMetadata importMetadata) {
        this.enableNiceLock = AnnotationAttributes.fromMap(
                importMetadata.getAnnotationAttributes(EnableNiceLock.class.getName(), false));
    }

    @Bean(name = "com.suchtool.nicelock.niceLockAspect")
    public NiceLockAspect niceLockAspect(RedissonClient redissonClient,
                                         NiceLockProperty niceLockProperty) {
        Integer order = Ordered.LOWEST_PRECEDENCE;
        if (enableNiceLock != null) {
            order = enableNiceLock.<Integer>getNumber("order");
        }

        return new NiceLockAspect(niceLockProperty, redissonClient, order);
    }

    @Bean(name = "com.suchtool.nicelock.niceLockProperty")
    @ConfigurationProperties(prefix = "suchtool.nicelock")
    public NiceLockProperty niceLockProperty() {
        return new NiceLockProperty();
    }
}
