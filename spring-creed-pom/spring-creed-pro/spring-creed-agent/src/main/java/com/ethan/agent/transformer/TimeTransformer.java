package com.ethan.agent.transformer;

import com.ethan.agent.adaptor.TimeInterceptor;
import com.ethan.agent.factory.AbstractDevBuddyTransformer;
import net.bytebuddy.description.NamedElement;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.JavaModule;

import java.security.ProtectionDomain;

/**
 * @author EthanCao
 * @description spring-creed-agent
 * @date 19/11/24
 */
public class TimeTransformer extends AbstractDevBuddyTransformer<NamedElement> {
    public TimeTransformer() {
        super(ElementMatchers.nameStartsWith("com.uob"));
    }

    @Override
    public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder, TypeDescription typeDescription, ClassLoader classLoader, JavaModule module, ProtectionDomain protectionDomain) {
        return builder.method(ElementMatchers.any()).intercept(MethodDelegation.to(TimeInterceptor.class));
    }
}
