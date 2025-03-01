package com.ethan.agent.transformer;

import com.ethan.agent.adaptor.resttemplate.RestTemplateInterceptor;
import com.ethan.agent.exception.CreedBuddyException;
import com.ethan.agent.factory.AbstractDevBuddyTransformer;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class RestTemplateTransformer extends AbstractDevBuddyTransformer<NamedElement> {
    public RestTemplateTransformer() {
        super(ElementMatchers.nameStartsWith("com.ethan").and(
                ElementMatchers.nameEndsWith("RestTemplateConfiguration")
                        .or(ElementMatchers.nameEndsWith("RestTemplateConfig"))
        ));
    }

    @Override
    public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder, TypeDescription typeDescription, ClassLoader classLoader, JavaModule module, ProtectionDomain protectionDomain) {
        // log.info("::typeDescription:{}", typeDescription);
        try {
            return builder
                    .method(ElementMatchers.returns(Class.forName("org.springframework.web.client.RestTemplate")))
                    // .intercept(Advice.to(RestTemplateAdvice.class));
                    .intercept(MethodDelegation.to(RestTemplateInterceptor.class));
        } catch (ClassNotFoundException e) {
            throw new CreedBuddyException(e);
        }

    }
}
