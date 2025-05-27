package com.ethan.example.loadbalancer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.BootstrapContext;
import org.springframework.boot.BootstrapRegistry;
import org.springframework.boot.BootstrapRegistryInitializer;
import org.springframework.boot.context.config.ConfigDataLocation;
import org.springframework.boot.context.config.ConfigDataLocationResolverContext;
import org.springframework.boot.context.config.Profiles;
import org.springframework.boot.context.properties.bind.BindHandler;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.cloud.client.discovery.simple.SimpleDiscoveryClient;
import org.springframework.cloud.client.discovery.simple.SimpleDiscoveryProperties;
import org.springframework.cloud.config.client.ConfigClientProperties;
import org.springframework.cloud.config.client.ConfigClientRequestTemplateFactory;
import org.springframework.cloud.config.client.ConfigServerConfigDataLocationResolver;
import org.springframework.cloud.config.client.ConfigServerInstanceProvider;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * @author EthanCao
 * @description spring-creed
 * @date 13/5/25
 * {@link ConfigServerConfigDataLocationResolver#resolveProfileSpecific(ConfigDataLocationResolverContext, ConfigDataLocation, Profiles)}
 * 注册 SimpleDiscoveryClient 作为服务发现配置
 */
@Slf4j
public class SimpleDiscoveryClientBootstrapRegistry implements BootstrapRegistryInitializer {
    /**
     * {@link org.springframework.cloud.consul.discovery.configclient.ConsulConfigServerBootstrapper#initialize(BootstrapRegistry)}
     * {@link ConfigServerConfigDataLocationResolver#resolveProfileSpecific(ConfigDataLocationResolverContext, ConfigDataLocation, Profiles)}
     * @param registry the registry to initialize
     */
    @Override
    public void initialize(BootstrapRegistry registry) {
        registry.registerIfAbsent(RestTemplate.class, context -> {
            ConfigClientRequestTemplateFactory factory = context.get(ConfigClientRequestTemplateFactory.class);
            RestTemplate restTemplate = factory.create();
            //自定义RestTemplate 配置
            return restTemplate;
        });

        registry.registerIfAbsent(SimpleDiscoveryClient.class, context -> {
            if (!isDiscoveryEnabled(context)) {
                return null;
            }
            ConfigServerConfigDataLocationResolver.PropertyResolver propertyResolver = getPropertyResolver(context);
            SimpleDiscoveryProperties properties = propertyResolver.resolveConfigurationProperties(
                    "spring.cloud.discovery.client.simple", SimpleDiscoveryProperties.class,
                    SimpleDiscoveryProperties::new);
            return new SimpleDiscoveryClient(properties);
        });
        // promote discovery client if created
        registry.addCloseListener(event -> {
            if (!isDiscoveryEnabled(event.getBootstrapContext())) {
                return;
            }
            SimpleDiscoveryClient discoveryClient = event.getBootstrapContext().get(SimpleDiscoveryClient.class);
            if (discoveryClient != null) {
                event.getApplicationContext()
                        .getBeanFactory()
                        .registerSingleton("simpleDiscoveryClient", discoveryClient);
            }
        });

        registry.registerIfAbsent(ConfigServerInstanceProvider.Function.class, context -> {
            if (!isDiscoveryEnabled(context)) {
                return id -> Collections.emptyList();
            }
            SimpleDiscoveryClient discoveryClient = context.get(SimpleDiscoveryClient.class);
            return discoveryClient::getInstances;
        });

    }
    private static ConfigServerConfigDataLocationResolver.PropertyResolver getPropertyResolver(
            BootstrapContext context) {
        return context.getOrElseSupply(ConfigServerConfigDataLocationResolver.PropertyResolver.class,
                () -> new ConfigServerConfigDataLocationResolver.PropertyResolver(context.get(Binder.class),
                        context.getOrElse(BindHandler.class, null)));
    }

    public static boolean isDiscoveryEnabled(BootstrapContext bootstrapContext) {
        ConfigServerConfigDataLocationResolver.PropertyResolver propertyResolver = getPropertyResolver(
                bootstrapContext);
        return propertyResolver.get(ConfigClientProperties.CONFIG_DISCOVERY_ENABLED, Boolean.class, false);
    }
}
