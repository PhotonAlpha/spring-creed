package com.ethan.agent;

import net.bytebuddy.asm.Advice;
import net.bytebuddy.implementation.bytecode.assign.Assigner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author EthanCao
 * @description spring-creed-agent
 * @date 9/12/24
 */
public class TestControllerInterceptor {
    public static final Logger log = LoggerFactory.getLogger(TestControllerInterceptor.class);
    @Advice.OnMethodExit // 在方法返回时执行
    public static void intercept(@Advice.Origin String methodName, @Advice.Return(readOnly = false, typing = Assigner.Typing.DYNAMIC) Object returned) {
        String versionStr = "version11";
        log.info("@.@[OnMethodExit JWTVerifierAdvice Name:{} hello world【{}]]@.@", methodName, versionStr);
        returned = "hello world【" + versionStr + "]";
    }
}
