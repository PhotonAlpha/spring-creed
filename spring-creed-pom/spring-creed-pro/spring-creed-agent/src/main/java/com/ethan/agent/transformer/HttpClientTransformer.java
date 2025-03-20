package com.ethan.agent.transformer;

import com.ethan.agent.adaptor.HttpClient5Interceptor;
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
public class HttpClientTransformer extends AbstractDevBuddyTransformer<NamedElement> {
    public HttpClientTransformer() {
        super(
            ElementMatchers.nameStartsWith("com.ethan")
                    .and(ElementMatchers.nameEndsWith("Config"))
        );
    }

    @Override
    public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder, TypeDescription typeDescription, ClassLoader classLoader, JavaModule module, ProtectionDomain protectionDomain) {
        log.debug("@.@[::typeDescription:{}]@.@", typeDescription);
        try {
            var elementMatcher = ElementMatchers.returns(Class.forName("org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory"))
                    .or(ElementMatchers.returns(Class.forName("javax.net.ssl.SSLContext")));
            return builder
                    .method(elementMatcher)
                    .intercept(MethodDelegation.to(HttpClient5Interceptor.class));
        } catch (ClassNotFoundException e) {
            throw new CreedBuddyException(e);
        }
    }
}
