package com.ethan.service;

import com.ethan.controller.product.vo.ProductInfoVo;
import com.ethan.resolver.ProxyExchangeResolver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.webflux.ProxyExchange;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.function.BiPredicate;

@Service
@Slf4j
public class ProductService {
    public Mono<ProductInfoVo> getProductInfo(ProxyExchange<byte[]> proxy, ServerWebExchange exchange, String name) {
        var productResolver = ProxyExchangeResolver.get(ProductInfoVo.class);
        return productResolver.apply(proxy, exchange, "/product/info/" + name);

        // ServerHttpRequest request = exchange.getRequest();
        // String currentPath = request.getURI().toString().replaceAll(request.getPath().value(), "");
        // return proxy.uri(currentPath + "/product/info/" + name)
        //         // 解决出现 FULL_REQUEST(decodeResult: failure(java.lang.IllegalArgumentException: text is empty (possibly HTTP/0.9)) 问题
        //         .header(HttpHeaders.CONTENT_LENGTH, "0")
        //         .get(o -> {
        //     byte[] bytes = o.getBody();
        //     ProductInfoVo userInfoVo = JacksonUtils.parseObject(bytes, new TypeReference<ProductInfoVo>() {
        //     });
        //     log.info("ProductInfoVo:{}", userInfoVo);
        //     return ResponseEntity.ok(userInfoVo);
        // }).mapNotNull(HttpEntity::getBody);
    }

    public BiPredicate<Long, ProductInfoVo> productInStock() {
        log.info("productInStock");
        return (stock, resp) ->
                 Objects.nonNull(resp)
                    && resp.getStock() >= stock;
    }
}
