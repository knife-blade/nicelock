package com.suchtool.nicelock.annotation;
 
import com.suchtool.nicelock.exception.NiceLockLockFailException;

import java.lang.annotation.*;
 
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NiceLock {
    /**
     * 锁的key。（支持SpEL）
     * <p>如果参数是对象，这样写：#对象名.字段名，例如：#user.userName</p>
     * <p>如果参数不是对象，这样写：#字段名。例如：#orderNo</p>
     */
    String[] keys();

    /**
     * 获取锁的超时时间。单位：毫秒
     */
    long acquireTimeout() default 0L;

    /**
     * 错误提示。若定义了exception，则作为其message。
     */
    String message() default "正在处理，请稍候重试";

    /**
     * 报错时抛出的异常
     */
    Class<? extends Exception> exception() default NiceLockLockFailException.class;
}