package com.ethan.service;

import com.ethan.controller.recipient.vo.RecipientInfoVo;
import com.ethan.resolver.ProxyExchangeResolver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.webflux.ProxyExchange;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class RecipientService {
    public Mono<RecipientInfoVo> getRecipientInfo(ProxyExchange<byte[]> proxy, ServerWebExchange exchange, String name) {
        log.info("getRecipientInfo:{}", name);
        ProxyExchangeResolver<String, Mono<RecipientInfoVo>> resolver = ProxyExchangeResolver.get();
        return resolver.apply(proxy, exchange, "/recipient/recipient/" + name);
    }
}
