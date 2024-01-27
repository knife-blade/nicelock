package com.suchtool.nicelock.annotation;

import com.suchtool.nicelock.configuration.NiceLockConfigurationSelector;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(NiceLockConfigurationSelector.class)
public @interface EnableNiceLock {
    int order() default Ordered.LOWEST_PRECEDENCE;
}
