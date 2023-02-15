/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */

package com.ethan.web.apilog.config;

import com.ethan.web.apilog.core.service.ApiErrorLogFrameworkService;
import com.ethan.web.apilog.core.service.ApiErrorLogFrameworkServiceImpl;
import com.ethan.web.logger.core.api.ApiErrorLogApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApilogConfig {
    @Bean
    public ApiErrorLogFrameworkService apiErrorLogFrameworkService(ApiErrorLogApi apiErrorLogApi) {
        return new ApiErrorLogFrameworkServiceImpl(apiErrorLogApi);
    }
}
