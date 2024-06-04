package com.ethan.service;

import com.ethan.controller.userlogin.vo.UserInfoVo;
import com.ethan.resolver.ProxyExchangeResolver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.webflux.ProxyExchange;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
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
        ProxyExchangeResolver<String, Mono<UserInfoVo>> resolver = ProxyExchangeResolver.get();
        return resolver.apply(proxy, exchange, "/user-info/status/" + name);
    }
    public Predicate<UserInfoVo> hasLogin() {
        return resp ->
                 Objects.nonNull(resp)
                        && Boolean.TRUE.equals(resp.getActive())
                        && Boolean.FALSE.equals(resp.getLocked())
                        && StringUtils.hasText(resp.getName());
    }
}
