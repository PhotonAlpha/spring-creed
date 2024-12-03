package com.ethan.agent.transformer;

import com.ethan.agent.adaptor.database.DataSourceInterceptor;
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
public class DataSourceTransformer extends AbstractDevBuddyTransformer<NamedElement> {
    public DataSourceTransformer() {
        super(ElementMatchers.nameStartsWith("com.ethan"));
    }

    @Override
    public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder, TypeDescription typeDescription, ClassLoader classLoader, JavaModule module, ProtectionDomain protectionDomain) {
        log.debug("::DataSourceTransformer:{}", typeDescription);
        try {
            var elementMatcher = ElementMatchers.returns(Class.forName("javax.sql.DataSource"));
            return builder
                    .method(elementMatcher)
                    .intercept(MethodDelegation.to(DataSourceInterceptor.class));
        } catch (ClassNotFoundException e) {
            throw new CreedBuddyException(e);
        }

    }
}
