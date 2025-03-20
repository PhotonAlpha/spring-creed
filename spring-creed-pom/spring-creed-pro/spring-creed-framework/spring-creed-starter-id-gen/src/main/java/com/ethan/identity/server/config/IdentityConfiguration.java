package com.ethan.identity.server.config;

import com.ethan.common.constant.WebFilterOrderEnum;
import com.ethan.identity.core.segment.dal.repository.SystemLeafAllocRepository;
import com.ethan.identity.core.segment.dal.service.IDAllocService;
import com.ethan.identity.core.segment.dal.service.impl.IDAllocServiceImpl;
import com.ethan.identity.server.exception.InitException;
import com.ethan.identity.server.filter.IdGenerateConsoleFilter;
import com.ethan.identity.server.service.SegmentService;
import com.ethan.identity.server.service.SnowflakeService;
import jakarta.servlet.Filter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.ViewResolver;

import java.util.Arrays;

/**
 * @author EthanCao ethan.caoq@foxmail.com
 * @description spring-creed-pro
 * @date 30/12/24
 */
@Configuration
public class IdentityConfiguration {
    @Bean
    @ConditionalOnProperty(prefix = "leaf.console", value = "enable", havingValue = "true")
    public FilterRegistrationBean<IdGenerateConsoleFilter> idGenerateConsoleFilterBean(@Autowired(required = false) SegmentService segmentService, @Autowired(required = false) SnowflakeService snowflakeService, ViewResolver viewResolver) {
        var idGenerateConsoleFilter = new IdGenerateConsoleFilter(segmentService, snowflakeService, viewResolver);
        FilterRegistrationBean<IdGenerateConsoleFilter> beanFactory = new FilterRegistrationBean<>(idGenerateConsoleFilter);
        beanFactory.setOrder(WebFilterOrderEnum.CORS_FILTER + 1);
        beanFactory.setEnabled(true);
        return beanFactory;
    }

    @Bean
    @ConditionalOnMissingBean
    public IdentityProperties identityProperties() {
        return new IdentityProperties();
    }
    @Bean
    @ConditionalOnProperty(prefix = "leaf.segment", value = "enable", havingValue = "true")
    public IDAllocService idAllocService(SystemLeafAllocRepository allocRepository) {
        return new IDAllocServiceImpl(allocRepository);
    }
    @Bean
    @ConditionalOnProperty(prefix = "leaf.segment", value = "enable", havingValue = "true")
    public SegmentService segmentService(IdentityProperties identityProperties, @Autowired(required = false) IDAllocService allocService) throws InitException {
        return new SegmentService(identityProperties, allocService);
    }
    @Bean
    @ConditionalOnProperty(prefix = "leaf.snowflake", value = "enable", havingValue = "true")
    public SnowflakeService snowflakeService(IdentityProperties identityProperties) throws InitException {
        return new SnowflakeService(identityProperties);
    }

}
