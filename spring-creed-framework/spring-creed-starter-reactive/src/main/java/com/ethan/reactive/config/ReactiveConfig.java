/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */

package com.ethan.reactive.config;

import com.ethan.reactive.handler.ExampleHandlerFilterFunction;
import com.ethan.reactive.handler.PlayerHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Configuration
public class ReactiveConfig {
    @Bean
    public RouterFunction<ServerResponse> systemUsersRoute(PlayerHandler playerHandler) {
        return RouterFunctions
                .route(GET("/system/auth/users/{name}"), playerHandler::getName)
                .filter(new ExampleHandlerFilterFunction());
    }

    @Bean
    public RouterFunction<ServerResponse> routeRequest(PlayerHandler playerHandler) {
        return RouterFunctions
                .route(GET("/hello").and(RequestPredicates.accept(MediaType.TEXT_PLAIN)), this::handleRequest)
                ;
    }

    public Mono<ServerResponse> handleRequest(ServerRequest request) {
        return ok().bodyValue(sayHello(request));
    }

    private Mono<String> sayHello(ServerRequest request) {
        try {
            return Mono.just("Hello, " + request.queryParam("name").get());
        } catch (Exception e) {
            return Mono.error(e);
        }
    }


}
