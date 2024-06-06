package com.ethan.service;

import com.ethan.controller.order.order.OrderReqDto;
import com.ethan.controller.order.order.OrderVo;
import com.ethan.resolver.ProxyExchangeResolver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.webflux.ProxyExchange;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class OrderService {
    public Mono<OrderVo> payment(ProxyExchange<byte[]> proxy, ServerWebExchange exchange, OrderReqDto req) {
        log.info("submitOrder:{}", req);
        ProxyExchangeResolver<String, Mono<OrderVo>> resolver = ProxyExchangeResolver.post(req);
        return resolver.apply(proxy, exchange, "/order/submit");
    }
}
