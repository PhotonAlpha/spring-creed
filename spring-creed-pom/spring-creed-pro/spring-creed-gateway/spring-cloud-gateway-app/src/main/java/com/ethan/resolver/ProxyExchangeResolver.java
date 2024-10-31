package com.ethan.resolver;

import com.ethan.controller.product.vo.ProductInfoVo;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.webflux.ProxyExchange;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.function.Function;

@FunctionalInterface
public interface ProxyExchangeResolver<T, R> {
    /**
     *
     * @param proxy
     * @param exchange
     * @param t can be path or Function
     * @return
     */
    R apply(ProxyExchange<byte[]> proxy, ServerWebExchange exchange, T t) ;

    default <V> Function<V, R> compose(ProxyExchange<byte[]> proxy, ServerWebExchange exchange, ProxyExchangeResolver<? super V, ? extends T> before) {
        Objects.requireNonNull(before);
        return (V v) -> apply(proxy, exchange, before.apply(proxy, exchange, v));
    }

    default <V> Function<T, V> andThen(ProxyExchange<byte[]> proxy, ServerWebExchange exchange, ProxyExchangeResolver<? super R, ? extends V> after) {
        Objects.requireNonNull(after);
        return (T t) -> after.apply(proxy, exchange, apply(proxy, exchange, t));
    }

    /**
     *
     * @param responseType
     * @return
     * @param <V> response type
     */
    static <T, V> ProxyExchangeResolver<T, Mono<V>> get(Class<V> responseType) {
        return (proxy, exchange, path) -> {
            ServerHttpRequest request = exchange.getRequest();
            String currentPath = request.getURI().toString().replaceAll(request.getPath().value(), "");
            return proxy.uri(currentPath + path)
                    .header(HttpHeaders.CONTENT_LENGTH, "0")
                    .get(o -> {
                byte[] bytes = o.getBody();
                return ResponseEntity.ok(JacksonUtils.parseObject(bytes, responseType));
            }).mapNotNull(HttpEntity::getBody);
        };
    }

    /**
     *
     * @param req
     * @return
     * @param <T> will be path for post query
     * @param <R>
     */
    static <V, T, R> ProxyExchangeResolver<T, Mono<V>> post(R req, Class<V> responseType) {
        return (proxy, exchange, path) -> {
            ServerHttpRequest request = exchange.getRequest();
            String currentPath = request.getURI().toString().replaceAll(request.getPath().value(), "");
            return proxy.uri(currentPath + path).body(req)
                    .header(HttpHeaders.CONTENT_LENGTH, "0")
                    .post(o -> {
                byte[] bytes = o.getBody();
                return ResponseEntity.ok(JacksonUtils.parseObject(bytes, responseType));
            }).mapNotNull(HttpEntity::getBody);
        };
    }
}
