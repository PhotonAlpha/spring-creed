package com.ethan.configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author EthanCao
 * @description spring-creed-agent
 * @date 23/4/24
 */
@Slf4j
// @Component
// @RequiredArgsConstructor
public class GlobalErrorExceptionHandler implements ErrorWebExceptionHandler {
    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        log.info("GlobalErrorExceptionHandler:{}", ex);
        return Mono.error(ex);
    }
}
