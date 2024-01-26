package com.suchtool.betterlock.configuration;

import com.suchtool.betterlock.annotation.EnableBetterLock;
import com.suchtool.betterlock.aspect.BetterLockAspect;
import com.suchtool.betterlock.property.BetterLockProperty;
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
public class BetterLockConfiguration implements ImportAware {
    @Nullable
    protected AnnotationAttributes enableBetterLock;

    public void setImportMetadata(AnnotationMetadata importMetadata) {
        this.enableBetterLock = AnnotationAttributes.fromMap(
                importMetadata.getAnnotationAttributes(EnableBetterLock.class.getName(), false));
    }

    @Bean(name = "com.suchtool.betterlock.betterLockAspect")
    public BetterLockAspect betterLockAspect(RedissonClient redissonClient) {
        Integer order = Ordered.LOWEST_PRECEDENCE;
        if (enableBetterLock != null) {
            order = enableBetterLock.<Integer>getNumber("order");
        }

        return new BetterLockAspect(redissonClient, order);
    }

    @Bean(name = "com.suchtool.betterlock.betterLockProperty")
    @ConfigurationProperties(prefix = "suchtool.betterlock")
    public BetterLockProperty betterLockProperty() {
        return new BetterLockProperty();
    }
}
