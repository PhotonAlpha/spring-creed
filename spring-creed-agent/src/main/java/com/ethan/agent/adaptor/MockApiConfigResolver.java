package com.ethan.agent.adaptor;

import com.ethan.agent.adaptor.apache.MockApiConfig;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertyName;
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
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;

/**
 * @author EthanCao
 * @description spring-creed-agent
 * @date 26/11/24
 */
@FunctionalInterface
public interface MockApiConfigResolver {
    MockApiConfig apply(ApplicationContext applicationContext);

    static MockApiConfigResolver simple() {
        return (applicationContext) -> {
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
                System.out.println(">>resource:application-mock.yml not found! checking >> " + "./mock/" + mockYaml);
                resource = defaultResourceLoader.getResource("file:./mock/" + mockYaml);
                if (resource.exists()) {
                    System.out.println("Greetings! ./mock/application-mock.yml loading!");
                } else {
                    System.out.println("Ohhhhhhhh! ./mock/application-mock.yml not found!");
                }
            }
            if (Objects.isNull(resource) || !resource.exists()) {
                // skip
                return null;
            }
            try {
                PropertySource<?> propertySource = yamlPropertySourceLoader.load("yaml mock", resource).get(0);
                var binder = new Binder(ConfigurationPropertySource.from(propertySource));

                return binder.bind("mock", Bindable.of(MockApiConfig.class)).orElse(new MockApiConfig());
            } catch (IOException e) {
                //ignore
                e.printStackTrace();
            }
            return new MockApiConfig();
        };
    }

    static BiFunction<MockApiConfig, String, Boolean> compute() {
        BiPredicate<String, String> pathPredicate = (uri, convertedName) -> {
            var maskedContextPath = ConfigurationPropertyName.adapt(uri, '_').toString();
            return StringUtils.containsIgnoreCase(maskedContextPath, convertedName);
        };

        return (conf, convertedName) -> {
            boolean mockEnabled = Optional.ofNullable(conf.getServer()).map(MockApiConfig.ServerDetails::getEnabled).orElse(Boolean.FALSE);
            if (Boolean.TRUE.equals(mockEnabled)) {
                Boolean mockServerApiDefault = Optional.of(conf.getServer()).map(MockApiConfig.ServerDetails::getMockApiEnabled).orElse(Boolean.FALSE);
                // boolean redirectMockDisabled;
                if (Boolean.TRUE.equals(mockServerApiDefault)) {
                    // check exclude urls
                    List<String> excludeApis = Optional.ofNullable(conf.getExcludeApis()).orElse(Collections.emptyList());
                    mockEnabled = excludeApis.stream().noneMatch(path -> pathPredicate.test(path, convertedName));
                } else {
                    // check include urls
                    List<String> apis = Optional.ofNullable(conf.getApis()).orElse(Collections.emptyList());
                    mockEnabled = apis.stream().noneMatch(path -> pathPredicate.test(path, convertedName));
                }
            }
            return mockEnabled;
        };
    }

}
