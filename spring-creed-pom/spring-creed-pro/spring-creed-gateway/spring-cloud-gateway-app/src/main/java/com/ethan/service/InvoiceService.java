package com.ethan.service;

import com.ethan.controller.invoice.invoice.InvoiceInfoVo;
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
public class InvoiceService {
    public Mono<InvoiceInfoVo> getInvoiceInfo(ProxyExchange<byte[]> proxy, ServerWebExchange exchange, String name) {
        log.info("getInvoiceInfo:{}", name);
        ProxyExchangeResolver<String, Mono<InvoiceInfoVo>> resolver = ProxyExchangeResolver.get(InvoiceInfoVo.class);
        return resolver.apply(proxy, exchange, "/invoice/info/" + name);
    }
}
