package com.knife.lock.aspect;

import com.knife.lock.annotation.KnifeLock;
import com.knife.lock.aspect.context.KnifeLockContext;
import com.knife.lock.aspect.context.KnifeLockContextThreadLocal;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.core.annotation.Order;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.concurrent.TimeUnit;

@Aspect
@Component
@Order(1)  //确保本AOP在@Transactional之前执行，@Transactional的顺序是Integer最大值
public class KnifeLockAspect {
    private static final ExpressionParser PARSER = new SpelExpressionParser();

    private static final ParameterNameDiscoverer NAME_DISCOVERER = new DefaultParameterNameDiscoverer();

    private static final String SEPARATOR = ",";

    @Value("${custom.distribution.lock.prefix:custom:distribution:lock}")
    private String keyPrefix;

    @Autowired
    private RedissonClient redissonClient;

    @Pointcut("@annotation(com.knife.lock.annotation.KnifeLock)")
    public void pointcut() {
    }

    @Before("pointcut()")
    public void before() {
    }

    @Around("pointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();

        KnifeLock distributionLock = method.getAnnotation(KnifeLock.class);
        String[] keys = distributionLock.keys();

        Object[] args = joinPoint.getArgs();

        String key = assembleFullKey(methodSignature, args, keys);
        RLock lock = redissonClient.getLock(key);
        boolean locked = false;

        if (distributionLock.acquireTimeout() == 0L) {
            locked = lock.tryLock();
        } else {
            locked = lock.tryLock(distributionLock.acquireTimeout(), TimeUnit.MILLISECONDS);
        }
        if (!locked) {
            Class<?> exceptionClass = distributionLock.exception();
            Throwable throwable = (Throwable) exceptionClass.newInstance();
            Field messageField = Throwable.class.getDeclaredField("detailMessage");
            messageField.setAccessible(true);
            messageField.set(throwable, distributionLock.message());
            messageField.setAccessible(false);
            throw throwable;
        }

        KnifeLockContext context = new KnifeLockContext();
        context.setKey(key);
        context.setLock(lock);
        KnifeLockContextThreadLocal.write(context);

        Object object = joinPoint.proceed();

        return object;
    }

    @AfterReturning(value = "pointcut()", returning = "returnValue")
    public void afterReturning(JoinPoint joinPoint, Object returnValue) {
        unlockAndClear();
    }

    @AfterThrowing(value = "pointcut()", throwing = "throwingValue")
    public void afterThrowing(JoinPoint joinPoint, Throwable throwingValue) {
        unlockAndClear();
    }

    /**
     * 无法获得返回值，只能获得参数
     */
    @After("pointcut()")
    public void after() {
    }

    private void unlockAndClear() {
        KnifeLockContext context = KnifeLockContextThreadLocal.read();
        RLock lock = context.getLock();
        if (lock != null) {
            lock.unlock();
        }
        KnifeLockContextThreadLocal.clear();
    }

    private String assembleFullKey(MethodSignature methodSignature,
                                   Object[] args,
                                   String[] keys) {
        Method method = methodSignature.getMethod();
        // Class<?> declaringClass = method.getDeclaringClass();

        // 方法全名（包含返回值、类、方法名、参数类型等）
        String methodGenericString = method.toGenericString();
        // 去掉返回值信息，只取类、方法名、参数类型等
        String[] strings = methodGenericString.split(" ");
        String uniqueMethod = strings[strings.length - 1];

        return keyPrefix
                + ":" + uniqueMethod
                + ":" + calculateKey(method, args, keys);
    }

    private String calculateKey(Method method, Object[] args, String[] definitionKeys) {
        EvaluationContext context = new MethodBasedEvaluationContext(
                null, method, args, NAME_DISCOVERER);

        List<String> definitionValueList = new ArrayList<>(definitionKeys.length);
        for (String definitionKey : definitionKeys) {
            if (definitionKey != null && !definitionKey.isEmpty()) {
                String key = PARSER.parseExpression(definitionKey).getValue(context, String.class);
                definitionValueList.add(key);
            }
        }

        StringJoiner stringJoiner = new StringJoiner(SEPARATOR);
        for (String value : definitionValueList) {
            stringJoiner.add(value);
        }
        return stringJoiner.toString();
    }
}