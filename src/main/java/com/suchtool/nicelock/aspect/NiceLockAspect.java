package com.suchtool.nicelock.aspect;

import com.suchtool.nicelock.annotation.NiceLock;
import com.suchtool.nicelock.aspect.context.NiceLockContext;
import com.suchtool.nicelock.aspect.context.NiceLockContextThreadLocal;
import com.suchtool.nicelock.property.NiceLockProperty;
import com.suchtool.nicelock.util.lock.NiceLockUtil;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.Ordered;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.concurrent.TimeUnit;

@Aspect
public class NiceLockAspect implements Ordered {
    private static final ExpressionParser PARSER = new SpelExpressionParser();

    private static final ParameterNameDiscoverer NAME_DISCOVERER = new DefaultParameterNameDiscoverer();

    private static final String SEPARATOR = ",";

    private final NiceLockProperty niceLockProperty;

    private final NiceLockUtil niceLockUtil;

    private final int order;

    public NiceLockAspect(NiceLockProperty niceLockProperty,
                          NiceLockUtil niceLockUtil,
                          int order) {
        this.niceLockProperty = niceLockProperty;
        this.niceLockUtil = niceLockUtil;
        this.order = order;
    }

    @Override
    public int getOrder() {
        return order;
    }

    @Pointcut("@annotation(com.suchtool.nicelock.annotation.NiceLock)")
    public void pointcut() {
    }

    @Before("pointcut()")
    public void before(JoinPoint joinPoint) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();

        NiceLock distributionLock = method.getAnnotation(NiceLock.class);
        String[] keys = distributionLock.keys();

        Object[] args = joinPoint.getArgs();

        String key = assembleFullKey(methodSignature, args, keys);
        boolean locked = niceLockUtil.tryLock(key, distributionLock.acquireTimeout(),
                TimeUnit.MILLISECONDS);
        if (!locked) {
            Class<?> exceptionClass = distributionLock.exception();
            Throwable throwable = (Throwable) exceptionClass.newInstance();
            Field messageField = Throwable.class.getDeclaredField("detailMessage");
            messageField.setAccessible(true);
            messageField.set(throwable, distributionLock.message());
            messageField.setAccessible(false);
            throw throwable;
        }

        NiceLockContext context = new NiceLockContext();
        context.setKey(key);
        NiceLockContextThreadLocal.write(context);
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
        NiceLockContext context = NiceLockContextThreadLocal.read();
        String key = context.getKey();
        niceLockUtil.unlock(key);
        NiceLockContextThreadLocal.clear();
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

        String fullKey = niceLockProperty.getKeyPrefix() + ":" + uniqueMethod;
        if (keys != null && keys.length > 0) {
            fullKey = fullKey
                    + ":" + calculateKey(method, args, keys);
        }

        return fullKey;
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