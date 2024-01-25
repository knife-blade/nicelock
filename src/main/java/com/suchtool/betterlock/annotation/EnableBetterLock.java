package com.suchtool.betterlock.annotation;

import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;
import org.springframework.transaction.annotation.TransactionManagementConfigurationSelector;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(TransactionManagementConfigurationSelector.class)
public @interface EnableBetterLock {
    int order() default Ordered.LOWEST_PRECEDENCE;
}
