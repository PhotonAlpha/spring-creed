package com.ethan.filter;

import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author EthanCao
 * @description spring-creed-agent 读取 Request 请求参数
 * @date 2/4/24
 */
@Component
public class ReadBodyFilter implements GlobalFilter, Ordered {
    Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return read(exchange, chain);
    }

    private Mono<Void> read(ServerWebExchange exchange, GatewayFilterChain chain) {

        ServerHttpRequest serverHttpRequest = exchange.getRequest();
        ServerHttpResponse originalResponse = exchange.getResponse();

        //如果是post请求，将请求体取出来，再写入
        HttpMethod method = serverHttpRequest.getMethod();
        //请求参数，post从请求里获取请求体
        String requestBodyStr = HttpMethod.POST.equals(method) ? resolveBodyFromRequest(serverHttpRequest) : null;
        DataBufferFactory bufferFactory = originalResponse.bufferFactory();

        ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {
            @Override
            public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                if (body instanceof Flux) {
                    Flux<? extends DataBuffer> fluxBody = (Flux<? extends DataBuffer>) body;

                    return super.writeWith(fluxBody.buffer().map(dataBuffers -> {//解决返回体分段传输
                        StringBuilder stringBuffer = new StringBuilder();
                        dataBuffers.forEach(dataBuffer -> {
                            byte[] content = new byte[dataBuffer.readableByteCount()];
                            dataBuffer.read(content);
                            DataBufferUtils.release(dataBuffer);
                            try {
                                stringBuffer.append(new String(content, StandardCharsets.UTF_8));
                            } catch (Exception e) {
                                log.error("--list.add--error", e);
                            }
                        });
                        String result = stringBuffer.toString();
                        // result就是response的值，想修改、查看就随意而为了
                        Map<String, String> urlParams = serverHttpRequest.getQueryParams().toSingleValueMap();

                        log.info("请求地址:【{}】请求参数:GET【{}】|POST:【\n{}\n】,响应数据:【\n{}\n】", serverHttpRequest.getURI(), urlParams, requestBodyStr, result);

                        byte[] uppedContent = new String(result.getBytes(), StandardCharsets.UTF_8).getBytes();
                        originalResponse.getHeaders().setContentLength(uppedContent.length);
                        return bufferFactory.wrap(uppedContent);
                    }));

                }
                // if body is not a flux. never got there.
                return super.writeWith(body);
            }
        };
        return chain.filter(exchange.mutate().response(decoratedResponse).build());
    }

    /**
     * 从Flux<DataBuffer>中获取字符串的方法
     *
     * @return 请求体
     */
    private String resolveBodyFromRequest(ServerHttpRequest serverHttpRequest) {
        //获取请求体
        Flux<DataBuffer> body = serverHttpRequest.getBody();
        AtomicReference<String> bodyRef = new AtomicReference<>();
        body.subscribe(buffer -> {
            CharBuffer charBuffer = StandardCharsets.UTF_8.decode(buffer.asByteBuffer());
            DataBufferUtils.release(buffer);
            bodyRef.set(charBuffer.toString());
        });
        //获取request body
        return bodyRef.get();
    }

    @Override
    public int getOrder() {
        return -2;
    }
}
