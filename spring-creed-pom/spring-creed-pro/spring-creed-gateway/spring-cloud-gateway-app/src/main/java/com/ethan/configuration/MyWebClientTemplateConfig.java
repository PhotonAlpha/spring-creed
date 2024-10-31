package com.ethan.configuration;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.core5.http.HeaderElements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.gateway.webflux.config.ProxyExchangeArgumentResolver;
import org.springframework.cloud.gateway.webflux.config.ProxyProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLParameters;
import java.time.Duration;
import java.util.Optional;
import java.util.function.Consumer;

import static reactor.netty.resources.ConnectionProvider.DEFAULT_POOL_MAX_CONNECTIONS;

/**
 */
@Configuration
@Slf4j
public class MyWebClientTemplateConfig {


    // Returns the connection lease request timeout used when requesting a connection from the connection manager. A timeout value of zero is interpreted as a disabled timeout.
    // 从连接池获取的超时时间，可以通过限制连接池数量来复现maxTotalConnections
    @Value("${httpclient.config.connection-request-timeout:8000}")
    private int connectionRequestTimeout;




    /**
     * This method creates RestTemplate for Google firebase HTTP v1 API implementation
     *
     *
     * @return RestTemplate
     */
    @Bean
    public WebClient webClient() throws SSLException {
        log.info("initializing firebaseOAuth2RestTemplate");
        SslContext sslContext = SslContextBuilder.forClient()
                .build();


        HttpClient httpClient = HttpClient.create(ConnectionProvider.builder("cpg-pgspwebclient-connectionProvider")
                        .maxConnections(DEFAULT_POOL_MAX_CONNECTIONS)
                        .metrics(true)
                        .build()
                )
                .secure(sslContextSpec ->
                        sslContextSpec.sslContext(sslContext)
                                .handlerConfigurator(sslHandler -> {
                                    SSLEngine engine = sslHandler.engine();
                                    SSLParameters newSslParameters = engine.getSSLParameters(); // 返回的是一个新对象
                                    // 参考：https://github.com/AdoptOpenJDK/openjdk-jdk11/blob/master/src/java.net.http/share/classes/jdk/internal/net/http/AbstractAsyncSSLConnection.java#L116
                                    newSslParameters.setEndpointIdentificationAlgorithm(null);
                                    engine.setSSLParameters(newSslParameters);
                                })
                )
                .responseTimeout(Duration.ofMillis(connectionRequestTimeout));
        ReactorClientHttpConnector clientHttpConnector = new ReactorClientHttpConnector(httpClient);

        return WebClient.builder()
                .clientConnector(clientHttpConnector)
                .defaultHeaders(defaultHeadersConsumer())
                .filter(new RequestLoggingFilterFunction("spring-creed"))
                .build();
        // restTemplate.setErrorHandler(new ExtractingResponseErrorHandler());
        // restTemplate.setInterceptors(List.of(new MyRestTemplateHttpRequestInterceptor()));

        // 缓存response，在interceptor中可以实现多次读取
        // restTemplate.setRequestFactory(new BufferingClientHttpRequestFactory(httpRequestFactory));

        // return restTemplate;
    }


    private Consumer<HttpHeaders> defaultHeadersConsumer() {
        return headers ->
            headers.add(HttpHeaders.CONNECTION, HeaderElements.CLOSE);
    }


    @Bean
    public ProxyExchangeArgumentResolver proxyExchangeArgumentResolver(ProxyProperties proxy) throws SSLException {
        ProxyExchangeArgumentResolver resolver = new ProxyExchangeArgumentResolver(webClient());
        resolver.setHeaders(proxy.convertHeaders());
        resolver.setAutoForwardedHeaders(proxy.getAutoForward());
        resolver.setSensitive(proxy.getSensitive()); // can be null
        return resolver;
    }

}
