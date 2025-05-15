package com.ethan.service;

import com.ethan.controller.coupon.coupon.CouponVo;
import com.ethan.resolver.ProxyExchangeResolver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.webflux.ProxyExchange;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class CouponService {
    public Mono<CouponVo> getCoupon(ProxyExchange<byte[]> proxy, ServerWebExchange exchange, String name) {
        log.info("getCoupon:{}", name);
        ProxyExchangeResolver<String, Mono<CouponVo>> resolver = ProxyExchangeResolver.get(CouponVo.class);
        return resolver.apply(proxy, exchange, "/coupon/info/" + name);
    }
}
