package com.suchtool.betterlock.configuration;

import com.suchtool.betterlock.annotation.EnableBetterLock;
import com.suchtool.betterlock.aspect.BetterLockAspect;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportAware;
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
        if (this.enableBetterLock == null) {
            throw new IllegalArgumentException(
                    "@EnableBetterLock is not present on importing class " + importMetadata.getClassName());
        }
    }

    @Bean(name = "com.suchtool.betterlock.betterLockAspect")
    public BetterLockAspect betterLockAspect(RedissonClient redissonClient) {
        Integer order = enableBetterLock.<Integer>getNumber("order");

        return new BetterLockAspect(redissonClient, order);
    }
}
