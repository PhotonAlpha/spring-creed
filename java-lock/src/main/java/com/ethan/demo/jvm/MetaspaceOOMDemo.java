package com.ethan.demo.jvm;

import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * @className: MetaspaceOOMDemo
 * @author: Ethan
 * @date: 28/5/2021
 * -XX:MetaspaceSize=8m -XX:MaxMetaspaceSize=8m
 **/
public class MetaspaceOOMDemo {
    static class OOMTest {

    }
    public static void main(String[] args) {
        int i = 1;
        try {
            while (true) {
                i++;
                Enhancer enhancer = new Enhancer();
                enhancer.setSuperclass(OOMTest.class);
                enhancer.setUseCache(false);
                enhancer.setCallback((MethodInterceptor) (o, method, objects, methodProxy) -> {

                    return methodProxy.invokeSuper(o, args);
                });
                enhancer.create();
            }

        } catch (Throwable e) {
            System.out.println(i + "次之后发生了异常");
            e.printStackTrace();
        }
    }
}
