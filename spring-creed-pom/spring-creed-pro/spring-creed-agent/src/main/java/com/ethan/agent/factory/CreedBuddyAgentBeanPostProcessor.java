package com.ethan.agent.factory;

import com.ethan.agent.adaptor.apache.MockResponseChainHandler;
import com.ethan.agent.adaptor.resttemplate.MockRestResponseHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertySource;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * BeanPostProcessor 接口用于在 Bean 实例化后对 Bean 进行增强或修改。它可以在 Bean 的初始化过程中对 Bean 进行后处理，例如对 Bean 进行代理、添加额外的功能等。BeanPostProcessor 在 Bean 实例化完成后执行，用于对 Bean 实例进行后处理。
 * https://juejin.cn/post/7252171811566780477
 *
 * @author EthanCao
 * @description spring-creed-agent
 * @date 20/11/24
 */
@Slf4j
public class CreedBuddyAgentBeanPostProcessor implements BeanPostProcessor {
    private final ApplicationContext applicationContext;

    public CreedBuddyAgentBeanPostProcessor(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        // log.debug("@.@[postProcessBeforeInitialization beanName:{}]@.@", beanName);

        if (bean instanceof DataSource ds) {
            log.info("@.@[replacing with Creed Buddy DataSource On Bean:{}]@.@", beanName);
            DataSourceProperties dataSourceProperties = null;
            try {
                dataSourceProperties = applicationContext.getBean(DataSourceProperties.class);
            } catch (BeansException e) {
                // ignore
                log.warn("@.@[DataSourceProperties not found]@.@");
            }
            if (Objects.isNull(dataSourceProperties)) {
                // trying to load from application.yml
                dataSourceProperties = resolveProperties();
            }
            if (Objects.isNull(dataSourceProperties)) {
                log.warn("@.@[empty DataSource loaded!]@.@");
                return DataSourceBuilder.create().build();
            }
            log.info("@.@[replacing with Creed Buddy JDBC url:{}]@.@", dataSourceProperties.getUrl());

            return DataSourceBuilder.create()
                    .driverClassName(dataSourceProperties.getDriverClassName())
                    .url(dataSourceProperties.getUrl())
                    .username(dataSourceProperties.getUsername())
                    .password(dataSourceProperties.getPassword())
                    .build();

        }

        return BeanPostProcessor.super.postProcessBeforeInitialization(bean, beanName);
    }
    DataSourceProperties resolveProperties() {
        var defaultResourceLoader = new DefaultResourceLoader();
        var yamlPropertySourceLoader = new YamlPropertySourceLoader();
        try {
            Resource resource = defaultResourceLoader.getResource("classpath:/application.yml");
            if (!resource.exists()) {
                //skip
                log.warn("@.@[classpath:/application.yml not found]@.@");
                return null;
            }
            PropertySource<?> propertySource = yamlPropertySourceLoader.load("dev env", resource).get(0);
            var binder = new Binder(ConfigurationPropertySource.from(propertySource));

            return binder.bind("spring.datasource", Bindable.of(DataSourceProperties.class)).orElse(null);
        } catch (IOException e) {
            log.error("@.@[IOException]@.@", e);
        }
        return null;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof RestTemplate rt) {
            List<ClientHttpRequestInterceptor> interceptors = rt.getInterceptors();
            interceptors.add(new MockRestResponseHandler(applicationContext));
            rt.setInterceptors(interceptors);

        }
        return bean;
    }
}
