package com.ethan.agent.transformer;

import com.ethan.agent.adaptor.ApplicationContextAdvice;
import com.ethan.agent.factory.AbstractDevBuddyTransformer;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.NamedElement;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.JavaModule;

import java.security.ProtectionDomain;

/**
 * @author EthanCao
 * @description spring-creed-agent
 * @date 20/11/24
 */
public class ApplicationContextTransformer extends AbstractDevBuddyTransformer<NamedElement> {
    public ApplicationContextTransformer() {
        super(ElementMatchers.named("org.springframework.context.support.AbstractApplicationContext"));
    }

    @Override
    public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder, TypeDescription typeDescription, ClassLoader classLoader, JavaModule module, ProtectionDomain protectionDomain) {
        //MethodDelegation.withDefaultConfiguration().to()
        // 完全忽略目标方法
        //Advice.to() 允许在目标的执行方法前后插入逻辑，而不是完全接管方法
        // 完全忽略目标方法
        return builder.method(ElementMatchers.named("prepareBeanFactory")).intercept(
                Advice.to(ApplicationContextAdvice.class));
    }
}
