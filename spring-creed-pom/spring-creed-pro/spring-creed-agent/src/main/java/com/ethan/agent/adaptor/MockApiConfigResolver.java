package com.ethan.agent.adaptor;

import com.ethan.agent.adaptor.apache.MockApiConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.config.ConfigDataEnvironmentPostProcessor;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertySource;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;

/**
 * 手动读取 yml配置
 * 参考源码：
 *      1. {@link ConfigDataEnvironmentPostProcessor#postProcessEnvironment(ConfigurableEnvironment, SpringApplication)}
 *      2. org.springframework.boot.context.config.ConfigDataEnvironment#processAndApply()
 * @author EthanCao
 * @description spring-creed-agent
 * @date 26/11/24
 */
public interface MockApiConfigResolver {
    default MockApiConfig resolveProperties(ApplicationContext applicationContext) throws IOException {
        var environment = applicationContext.getBean(ConfigurableEnvironment.class);
        var locationPath = environment.getProperty("spring.config.location", "classpath:/");
        String mockYaml = "application-mock.yml";
        var defaultResourceLoader = new DefaultResourceLoader();
        var yamlPropertySourceLoader = new YamlPropertySourceLoader();
        String[] configLocations;
        if (locationPath.contains(",")) {
            configLocations = locationPath.split(",");
        } else {
            configLocations = locationPath.split(";");
        }
        var configLocationList = Arrays.asList(configLocations);
        Collections.reverse(configLocationList);

        Resource resource = null;
        for (String location : configLocationList) {
            String configPath;
            if (location.endsWith("/")) {
                configPath = location + mockYaml;
            } else {
                configPath = location;
            }
            resource = defaultResourceLoader.getResource(configPath);
            if (resource.exists()) {
                break;
            }
        }
        if (Objects.isNull(resource) || !resource.exists()) {
            //skip
            return null;
        }
        PropertySource<?> propertySource = yamlPropertySourceLoader.load("yaml mock", resource).get(0);
        var binder = new Binder(ConfigurationPropertySource.from(propertySource));

        return binder.bind("mock", Bindable.of(MockApiConfig.class)).orElse(new MockApiConfig());
    }

}
