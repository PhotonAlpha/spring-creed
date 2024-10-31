package com.ethan.service;

import com.ethan.controller.userlogin.vo.UserInfoVo;
import com.ethan.resolver.JacksonUtils;
import com.ethan.resolver.ProxyExchangeResolver;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.webflux.ProxyExchange;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.Supplier;

@Service
@Slf4j
public class UserLoginService {
    public Mono<UserInfoVo> getUserInfo(ProxyExchange<byte[]> proxy, ServerWebExchange exchange, String name) {
        ProxyExchangeResolver<String, Mono<UserInfoVo>> resolver = ProxyExchangeResolver.get(UserInfoVo.class);
        return resolver.apply(proxy, exchange, "/user-info/status/" + name);

       /*  ServerHttpRequest request = exchange.getRequest();
        String currentPath = request.getURI().toString().replaceAll(request.getPath().value(), "");
        return proxy.uri(currentPath + "/user-info/status/" + name)
                // .header(HttpHeaders.CONTENT_LENGTH, "0")
                .get(o -> {
            byte[] bytes = o.getBody();
            UserInfoVo userInfoVo = JacksonUtils.parseObject(bytes, new TypeReference<UserInfoVo>() {
            });
            log.info("result:{}", userInfoVo);
            return ResponseEntity.ok(userInfoVo);
        }).mapNotNull(HttpEntity::getBody); */

    }
    public Predicate<UserInfoVo> hasLogin() {
        log.info("hasLogin");
        return resp ->
                 Objects.nonNull(resp)
                        && Boolean.TRUE.equals(resp.getActive())
                        && Boolean.FALSE.equals(resp.getLocked())
                        && StringUtils.hasText(resp.getName());
    }
}
