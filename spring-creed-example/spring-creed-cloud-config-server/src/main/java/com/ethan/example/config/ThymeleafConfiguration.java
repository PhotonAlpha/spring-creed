package com.ethan.example.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.templatemode.TemplateMode;

@Configuration
public class ThymeleafConfiguration implements ApplicationContextAware {
    private ApplicationContext applicationContext;
    @Bean("txtSpringTemplateEngine")
    public SpringTemplateEngine txtSpringTemplateEngine(ThymeleafProperties properties) {
        SpringResourceTemplateResolver resolver = new SpringResourceTemplateResolver();
        resolver.setApplicationContext(this.applicationContext);
        resolver.setPrefix(properties.getPrefix());
        resolver.setSuffix(".txt");
        resolver.setTemplateMode(TemplateMode.TEXT);
        if (properties.getEncoding() != null) {
            resolver.setCharacterEncoding(properties.getEncoding().name());
        }
        resolver.setCacheable(properties.isCache());
        Integer order = properties.getTemplateResolverOrder();
        if (order != null) {
            resolver.setOrder(order);
        }
        resolver.setCheckExistence(properties.isCheckTemplate());
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(resolver);
        return templateEngine;
    }
    @Bean("springTemplateEngine")
    @Primary
    public SpringTemplateEngine springTemplateEngine(@Qualifier("defaultTemplateResolver") SpringResourceTemplateResolver defaultTemplateResolver) {
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(defaultTemplateResolver);
        return templateEngine;
    }

    @Primary
    @Bean(name = "defaultTemplateResolver")
    public SpringResourceTemplateResolver defaultTemplateResolver(ThymeleafProperties properties) {
        SpringResourceTemplateResolver resolver = new SpringResourceTemplateResolver();
        resolver.setApplicationContext(this.applicationContext);
        resolver.setPrefix(properties.getPrefix());
        resolver.setSuffix(properties.getSuffix());
        resolver.setTemplateMode(properties.getMode());
        if (properties.getEncoding() != null) {
            resolver.setCharacterEncoding(properties.getEncoding().name());
        }
        resolver.setCacheable(properties.isCache());
        Integer order = properties.getTemplateResolverOrder();
        if (order != null) {
            resolver.setOrder(order);
        }
        resolver.setCheckExistence(properties.isCheckTemplate());
        return resolver;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
