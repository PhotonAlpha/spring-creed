package com.ethan.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author EthanCao
 * @description spring-creed-agent
 * @date 2/4/24
 *
 *  * 解决 POST 请求 body 只能读取一次问题
 *  * 将原有的 body 数据读取出来
 *  * 使用 ServerHttpRequestDecorator 包装 Request
 *  * 重写 getBody() 方法，将包装后的请求放入过滤链传递下去
 *  * 此后过滤器通过 ServerWebExchange#getRequest()#getBody() 时，实际调用重写后的 getBody()
 *  * 获取到的 body 数据为缓存的数据
 *  * <p>
 *  * 过滤器优先级必须为 HIGHEST_PRECEDENCE 最高级，避免某些过滤器提前读取 body
 *  *
 *  * @author author
 *  * @date 2021-12-07
 *  * {@see <a href="https://juejin.cn/post/7039235092169883661"/>}
 */
@Component
public class CacheBodyGlobalFilter implements GlobalFilter, Ordered {
    Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        if (exchange.getRequest().getHeaders().getContentType() == null) {
            return chain.filter(exchange);
        } else {
            return DataBufferUtils.join(exchange.getRequest().getBody())
                .flatMap(dataBuffer -> {
                    DataBufferUtils.retain(dataBuffer);
                    Flux<DataBuffer> cachedFlux = Flux
                            .defer(() -> Flux.just(dataBuffer.slice(0, dataBuffer.readableByteCount())));
                    ServerHttpRequest mutatedRequest = new ServerHttpRequestDecorator(exchange.getRequest()) {
                        @Override
                        public Flux<DataBuffer> getBody() {
                            return cachedFlux;
                        }
                    };
                    return chain.filter(exchange.mutate().request(mutatedRequest).build());
                });
        }
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
