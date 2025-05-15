package com.ethan.agent.factory;

import com.ethan.agent.transformer.ApplicationContextTransformer;
import com.ethan.agent.transformer.DataSourceTransformer;
import com.ethan.agent.transformer.HttpClientTransformer;
import com.ethan.agent.transformer.RestTemplateTransformer;
import lombok.experimental.UtilityClass;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.matcher.ElementMatcher;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author EthanCao
 * @description spring-creed-agent
 * @date 19/11/24
 */
@UtilityClass
public class TransformFactory {
    private static final ConcurrentMap<ElementMatcher, AgentBuilder.Transformer> REGISTER = new ConcurrentHashMap<>();

    public <T> void register(ElementMatcher.Junction<T> matcher, AgentBuilder.Transformer transformer) {
        REGISTER.put(matcher, transformer);
    }

    public ConcurrentMap<ElementMatcher, AgentBuilder.Transformer> getRegister() {
        return REGISTER;
    }


    public void init() {
        // var timeTransformer = new TimeTransformer();
        // TransformFactory.register(timeTransformer.getMatcher(), timeTransformer);
        var templateTransformer = new RestTemplateTransformer();
        TransformFactory.register(templateTransformer.getMatcher(), templateTransformer);
        var clientTransformer = new HttpClientTransformer();
        TransformFactory.register(clientTransformer.getMatcher(), clientTransformer);
        // 数据库替换
        var dataSourceTransformer = new DataSourceTransformer();
        TransformFactory.register(dataSourceTransformer.getMatcher(), dataSourceTransformer);
        // Spring boot context
        var contextTransformer = new ApplicationContextTransformer();
        TransformFactory.register(contextTransformer.getMatcher(), contextTransformer);
    }
}
