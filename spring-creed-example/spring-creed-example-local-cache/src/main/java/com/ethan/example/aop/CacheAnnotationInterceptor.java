package com.ethan.example.aop;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.interceptor.CacheOperationSource;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.MethodClassKey;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.util.Assert;
import org.springframework.util.StopWatch;

import java.lang.reflect.Method;

import static com.ethan.example.aop.CacheContextHolder.get;
import static com.ethan.example.aop.CacheContextHolder.remove;


@Slf4j
public class CacheAnnotationInterceptor implements MethodInterceptor {
    @Getter
    private final CacheOperationSource cacheOperationSource;

    private final SpelExpressionParser parser;


    public CacheAnnotationInterceptor(CacheOperationSource cacheOperationSource) {
        this.cacheOperationSource = cacheOperationSource;
        this.parser = new SpelExpressionParser();
    }

    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        final StopWatch STOP_WATCH = new StopWatch("CacheMonitor");
        Cacheable cacheable = this.findAnnotation(methodInvocation);
        Method method = methodInvocation.getMethod();
        String expressionString = cacheable.key();
        Object cacheKey = getCacheKey(methodInvocation, method, expressionString);
        try {
            STOP_WATCH.start();
            // 执行逻辑

            log.info("cacheKey:{}", cacheKey);
            Object result = methodInvocation.proceed();
            Boolean cacheHint = get(cacheKey);
            log.info("obtain result:{} from cache:{}", result, cacheHint);
            return result;
        } finally {
            STOP_WATCH.stop();
            log.info("obtain result cost:{} ms", STOP_WATCH.lastTaskInfo().getTimeMillis());
            remove(cacheKey);
        }
    }
    private Object getCacheKey(MethodInvocation methodInvocation, Method method, String expressionString) {
        Object target = methodInvocation.getThis();
        Assert.state(target != null, "Target must not be null");
        MethodBasedEvaluationContext context = new MethodBasedEvaluationContext(target, method, methodInvocation.getArguments(), new DefaultParameterNameDiscoverer());
        var expression = parser.parseExpression(expressionString);
        return expression.getValue(context);
    }

    private Cacheable findAnnotation(MethodInvocation methodInvocation) {
        // 1. 从缓存中获取
        Method method = methodInvocation.getMethod();
        Object targetObject = methodInvocation.getThis();
        Class<?> clazz = targetObject != null ? targetObject.getClass() : method.getDeclaringClass();
        MethodClassKey methodClassKey = new MethodClassKey(method, clazz);
        // DataPermission dataPermission = dataPermissionCache.get(methodClassKey);
        // if (dataPermission != null) {
        //     return dataPermission != DATA_PERMISSION_NULL ? dataPermission : null;
        // }

        // 2.1 从方法中获取
        Cacheable cacheable = AnnotationUtils.findAnnotation(method, Cacheable.class);
        // 2.2 从类上获取
        if (cacheable == null) {
            cacheable = AnnotationUtils.findAnnotation(clazz, Cacheable.class);
        }
        // // 2.3 添加到缓存中
        // dataPermissionCache.put(methodClassKey, dataPermission != null ? dataPermission : DATA_PERMISSION_NULL);
        return cacheable;
    }

}
