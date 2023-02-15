/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */

package com.ethan.web.logger.core.config;

import com.ethan.web.apilog.core.service.ApiErrorLogFrameworkService;
import com.ethan.web.web.core.handler.GlobalExceptionHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties
// @EnableConfigurationProperties({WebProperties.class, XssProperties.class})
public class WebAutoConfiguration {

}
