package com.ethan.example.aop;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.aopalliance.aop.Advice;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractPointcutAdvisor;
import org.springframework.aop.support.ComposablePointcut;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.interceptor.CacheOperationSource;
import org.springframework.core.Ordered;

@Getter
@EqualsAndHashCode(callSuper = true)
public class CacheAnnotationAdvisor extends AbstractPointcutAdvisor {

    private final Advice advice;

    private final Pointcut pointcut;

    public CacheAnnotationAdvisor(CacheOperationSource cacheOperationSource) {
        this.advice = new CacheAnnotationInterceptor(cacheOperationSource);
        this.pointcut = this.buildPointcut();
        this.setOrder(Ordered.HIGHEST_PRECEDENCE);
    }

    protected Pointcut buildPointcut() {
        // Pointcut classPointcut = new AnnotationMatchingPointcut(DataPermission.class, true);
        Pointcut methodPointcut = new AnnotationMatchingPointcut(null, Cacheable.class, true);
        // return new ComposablePointcut(classPointcut).union(methodPointcut);
        return new ComposablePointcut(methodPointcut);
    }
}
