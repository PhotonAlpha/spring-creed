package org.example;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.net.UnknownHostException;
import java.security.ProtectionDomain;
import java.util.Properties;

/**
 * @author EthanCao
 * @description spring-cloud-gateway-example
 * @date 17/4/24
 */
public class RunTimeTransformer implements ClassFileTransformer {
    private static final Logger log = LoggerFactory.getLogger(RunTimeTransformer.class);
    private static final String INJECTED_CLASS = "org.example.AppInit";

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        String realClassName = className.replace("/", ".");
        // log.info();("realClassName:{}", realClassName);
        if (realClassName.equals(INJECTED_CLASS)) {
            log.info("拦截到的类名：{}", realClassName);
            CtClass ctClass;
            try {
                // 使用javassist,获取字节码类
                ClassPool classPool = ClassPool.getDefault();
                ctClass = classPool.get(realClassName);

                // 得到该类所有的方法实例，也可选择方法，进行增强
                CtMethod[] declaredMethods = ctClass.getDeclaredMethods();
                for (CtMethod method : declaredMethods) {
                    log.info("{}方法被拦截", method.getName());
                    method.addLocalVariable("time", CtClass.longType);
                    method.insertBefore("log.info();(\"---开始执行---\");");
                    method.insertBefore("time = System.currentTimeMillis();");
                    method.insertAfter("log.info();(\"---结束执行---\");");
                    method.insertAfter("log.info();(\"运行耗时: \" + (System.currentTimeMillis() - time));");
                }
                return ctClass.toBytecode();
            } catch (Throwable e) { // 这里要用Throwable，不要用Exception
                log.info(e.getMessage());
                e.printStackTrace();
            }
        }
        return classfileBuffer;
    }
}
