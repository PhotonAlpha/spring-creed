package com.ethan.agent.adaptor;

import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

/**
 * 时间统计拦截器，虚拟机维度的aop
 * @author EthanCao
 * @description spring-creed-agent
 * @date 19/11/24
 */
public class TimeInterceptor {
    public static final Logger log = LoggerFactory.getLogger(TimeInterceptor.class);
    /**
     * 进行方法拦截, 注意这里可以对所有修饰符的修饰的方法（包含private的方法）进行拦截
     *
     * @param method   待处理方法
     * @param callable 原方法执行
     * @return 执行结果
     */
    @RuntimeType
    public static Object intercept(@Origin Method method, @SuperCall Callable<?> callable) throws Exception {
        long start = System.currentTimeMillis();
        log.info("@.@[Time statistic: before method name:{} invoke!]@.@", method.getName());
        try {
            return callable.call();
        } catch (Exception e) {
            log.info("@.@[<<TimeInterceptor>>{}]@.@", e.getMessage());
            throw e;
        } finally {
            log.info("@.@[Time statistic: after method name:{} invoke!:::took {} millisecond.]@.@", method.getName(), (System.currentTimeMillis() - start));
        }
    }
}
