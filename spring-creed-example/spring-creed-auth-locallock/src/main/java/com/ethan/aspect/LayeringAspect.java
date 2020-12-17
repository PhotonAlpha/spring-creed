package com.ethan.aspect;

import com.ethan.annotation.Cacheable;
import com.ethan.expression.CacheOperationExpressionEvaluator;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.context.expression.AnnotatedElementKey;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.expression.EvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * 缓存拦截，用于注册方法信息
 *
 * @author yuhao.wang
 */
@Aspect
@Component
public class LayeringAspect {
    /**
     * SpEL表达式计算器
     */
    private final CacheOperationExpressionEvaluator evaluator = new CacheOperationExpressionEvaluator();

    @Pointcut("@annotation(com.ethan.annotation.Cacheable)")
    public void cacheablePointcut() {
    }

    @Around("cacheablePointcut()")
    public Object cacheablePointcut(ProceedingJoinPoint joinPoint) throws Throwable {
        // 获取method
        Method method = this.getSpecificmethod(joinPoint);
        Cacheable cacheable = AnnotationUtils.findAnnotation(method, Cacheable.class);


        // 获取到类名
        String targetName = joinPoint.getTarget().getClass().getName();
        System.out.println("代理的类是:" + targetName);
        // 获取到参数
        Object[] args = joinPoint.getArgs();
        System.out.println("传入的参数是:" + Arrays.toString(args));
        // 获取到方法签名，进而获得方法
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        System.out.println("增强的方法名字是:" + method.getName());
        // 获取参数类型
        Class<?>[] parameterTypes = method.getParameterTypes();
        System.out.println("参数类型是:" + parameterTypes.toString());

        Object target = joinPoint.getTarget();

        Class<?> targetClass = AopProxyUtils.ultimateTargetClass(target);
        if (targetClass == null) {
            targetClass = target.getClass();
        }

        EvaluationContext evaluationContext = evaluator.createEvaluationContext(method, args, joinPoint.getTarget(),
            targetClass, CacheOperationExpressionEvaluator.NO_RESULT);

        AnnotatedElementKey methodCacheKey = new AnnotatedElementKey(method, targetClass);

        String keySpEl = cacheable.key();
        Object keyValue = evaluator.key(keySpEl, methodCacheKey, evaluationContext);

        Object result = joinPoint.proceed();

        return result;
    }

    private Method getSpecificmethod(ProceedingJoinPoint pjp) {
        MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
        Method method = methodSignature.getMethod();
        // The method may be on an interface, but we need attributes from the
        // target class. If the target class is null, the method will be
        // unchanged.
        Class<?> targetClass = AopProxyUtils.ultimateTargetClass(pjp.getTarget());
        if (targetClass == null && pjp.getTarget() != null) {
            targetClass = pjp.getTarget().getClass();
        }
        Method specificMethod = ClassUtils.getMostSpecificMethod(method, targetClass);
        // If we are dealing with method with generic parameters, find the
        // original method.
        specificMethod = BridgeMethodResolver.findBridgedMethod(specificMethod);
        return specificMethod;
    }
}