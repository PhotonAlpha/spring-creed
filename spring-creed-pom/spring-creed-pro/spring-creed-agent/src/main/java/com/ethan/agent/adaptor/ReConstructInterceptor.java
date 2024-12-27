package com.ethan.agent.adaptor;

import com.ethan.agent.exception.CreedBuddyException;
import com.ethan.agent.util.PrivateProxyFactory;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.dynamic.scaffold.subclass.ConstructorStrategy;
import net.bytebuddy.implementation.FixedValue;
import net.bytebuddy.implementation.StubMethod;
import net.bytebuddy.implementation.bind.annotation.Origin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * 私有构造方法覆盖
 * @author EthanCao
 * @description spring-creed-agent
 * @date 18/11/24
 */
public class ReConstructInterceptor {
    public static final Logger log = LoggerFactory.getLogger(ReConstructInterceptor.class);

    public static PrivateProxyFactory apiProxyFactoryIntercept(@Origin Method method) {
        log.info("replacing with Creed Buddy PrivateProxyFactory methodName:{}", method.getName());
        //如果存在一个类，构造方法是私有的，如何去创建一个新的类来代理呢？
        // 1. 创建一个subclass
        // 2. StubMethod.INSTANCE 表示该方法不执行任何逻辑
        Class<?> loadedClass = new ByteBuddy()
                .subclass(PrivateProxyFactory.class, ConstructorStrategy.Default.NO_CONSTRUCTORS)
                .defineConstructor()
                // .withParameters(String.class, String[].class, int.class, Properties.class)
                // .defineConstructor(Visibility.PUBLIC)
                .intercept(StubMethod.INSTANCE)
                .make()
                .load(PrivateProxyFactory.class.getClassLoader(), ClassLoadingStrategy.Default.WRAPPER)
                .getLoaded();
        //通过反射创建对象
        try {
            Constructor<?> constructor = loadedClass.getDeclaredConstructor();
            constructor.setAccessible(true);
            log.info("constructor inject successful");
            return (PrivateProxyFactory) constructor.newInstance();
        } catch (Exception e) {
            log.error("Exception", e);
            throw new CreedBuddyException(e);
        }
    }




}
