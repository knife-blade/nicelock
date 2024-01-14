package com.knife.lock.annotation;
 
import java.lang.annotation.*;
 
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface KnifeLock {
    /**
     * 锁的key。（SpEL）
     */
    String[] keys();

    /**
     * 获取锁的超时时间。单位：毫秒
     */
    long acquireTimeout() default 0L;

    /**
     * 错误提示。若定义了exception，则作为其message。
     */
    String message() default "正在操作，请稍候重试";

    /**
     * 报错时抛出的异常
     */
    Class<? extends Exception> exception() default RuntimeException.class;
}