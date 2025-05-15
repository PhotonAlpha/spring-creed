package com.ethan.service;

import com.ethan.controller.activity.activity.ActivityInfoVo;
import com.ethan.resolver.ProxyExchangeResolver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.webflux.ProxyExchange;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class ActivityService {
    public Mono<ActivityInfoVo> getActivityInfo(ProxyExchange<byte[]> proxy, ServerWebExchange exchange, String name) {
        log.info("getActivityInfo:{}", name);
        ProxyExchangeResolver<String, Mono<ActivityInfoVo>> resolver = ProxyExchangeResolver.get(ActivityInfoVo.class);
        return resolver.apply(proxy, exchange, "/activity/info/" + name);
    }
}
