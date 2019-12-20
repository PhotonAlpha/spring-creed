package com.ethan.cache.redis.aspect;

import com.ethan.cache.redis.CacheSupport;
import org.apache.commons.lang3.ArrayUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@Aspect
public class CachingAnnotationsAspect {
  @Autowired
  private CacheSupport cacheSupport;

  private <T extends Annotation> List<T> getMethodAnnotations(AnnotatedElement ae, Class<T> annotationType) {
    List<T> anns = new ArrayList<T>(2);
    // look for raw annotation
    T ann = ae.getAnnotation(annotationType);
    if (ann != null) {
      anns.add(ann);
    }
    // look for meta-annotations
    for (Annotation metaAnn : ae.getAnnotations()) {
      ann = metaAnn.annotationType().getAnnotation(annotationType);
      if (ann != null) {
        anns.add(ann);
      }
    }
    return (anns.isEmpty() ? null : anns);
  }

  private Method getSpecificMethod(ProceedingJoinPoint pjp) {
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

  @Pointcut("@annotation(org.springframework.cache.annotation.Cacheable)")
  public void pointcut() {
  }

  @Around("pointcut()")
  public Object registerInvocation(ProceedingJoinPoint joinPoint) throws Throwable {
    Method method = this.getSpecificMethod(joinPoint);
    List<Cacheable> annotations = this.getMethodAnnotations(method, Cacheable.class);

    Set<String> cacheSet = new HashSet<>();
    String cacheKey = null;
    for (Cacheable cacheables : annotations) {
      if (ArrayUtils.isEmpty(cacheables.value())) {
        CacheConfig config = AnnotationUtils.findAnnotation(method.getDeclaringClass(), CacheConfig.class);
        cacheSet.addAll(Arrays.asList(config.cacheNames()));
      } else {
        cacheSet.addAll(Arrays.asList(cacheables.value()));
      }
      cacheKey = cacheables.key();
    }

    if (joinPoint.getSignature() instanceof MethodSignature) {
      Class<?>[] parameterTypes = ((MethodSignature) joinPoint.getSignature()).getParameterTypes();
      cacheSupport.registerInvocation(joinPoint.getTarget(), method, parameterTypes,
          joinPoint.getArgs(), cacheSet, cacheKey);
    }

    return joinPoint.proceed();
  }
}
