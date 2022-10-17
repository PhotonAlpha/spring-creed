package com.ethan.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;

// @Configuration
public class WebFluxConfiguration implements WebFluxConfigurer {
	@Override
	public void addCorsMappings(CorsRegistry registry) {
		// 添加全局的 CORS 配置
		registry.addMapping("/**") // 匹配所有 URL ，相当于全局配置
				.allowedOrigins("*") // 允许所有请求来源
				.allowCredentials(true) // 允许发送 Cookie
				.allowedMethods("*") // 允许所有请求 Method
				.allowedHeaders("*") // 允许所有请求 Header
//                .exposedHeaders("*") // 允许所有响应 Header
				.maxAge(1800L); // 有效期 1800 秒，2 小时
	}
}
