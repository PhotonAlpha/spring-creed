package com.ethan.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.web.reactive.accept.RequestedContentTypeResolver;
import org.springframework.web.reactive.result.method.annotation.ResponseBodyResultHandler;

// @Configuration
public class WebFluxConfig {
	@Bean
	public ResponseBodyResultHandler responseWrapper(ServerCodecConfigurer serverCodecConfigurer, RequestedContentTypeResolver requestedContentTypeResolver) {
		return new GlobalResponseBodyHandler(serverCodecConfigurer.getWriters(), requestedContentTypeResolver);
	}
}
