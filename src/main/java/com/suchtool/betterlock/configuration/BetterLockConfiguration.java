package com.suchtool.betterlock.configuration;

import com.suchtool.betterlock.annotation.EnableBetterLock;
import com.suchtool.betterlock.aspect.BetterLockAspect;
import com.suchtool.betterutil.util.ApplicationContextHolder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportAware;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.lang.Nullable;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
public class BetterLockConfiguration implements ImportAware {
    @Nullable
    protected AnnotationAttributes enableBetterLock;

    @Override
    public void setImportMetadata(AnnotationMetadata importMetadata) {
        this.enableBetterLock = AnnotationAttributes.fromMap(
                importMetadata.getAnnotationAttributes(EnableBetterLock.class.getName(), false));
        if (this.enableBetterLock == null) {
            throw new IllegalArgumentException(
                    "@EnableBetterLock is not present on importing class " + importMetadata.getClassName());
        }
    }

    // @Bean(name = "com.suchtool.betterlock.betterLockAspect")
    // public BetterLockAspect betterLockAspect() {
    //     BetterLockAspect betterLockAspect = new BetterLockAspect();
    //     betterLockAspect.
    // }
}
