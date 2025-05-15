package com.ethan.agent.util;

import lombok.Getter;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.DefaultConnectionKeepAliveStrategy;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.socket.ConnectionSocketFactory;
import org.apache.hc.client5.http.socket.PlainConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.TrustAllStrategy;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.HttpVersion;
import org.apache.hc.core5.http.config.Registry;
import org.apache.hc.core5.http.config.RegistryBuilder;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.message.BasicClassicHttpResponse;
import org.apache.hc.core5.http.message.BasicHttpResponse;
import org.apache.hc.core5.ssl.SSLContexts;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import javax.net.ssl.SSLContext;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * @author EthanCao
 * @description spring-boot-example-jdk11
 * @date 24/12/24
 */
@Slf4j
@UtilityClass
public class CloseableHttpClientTemplate {
    public CloseableHttpClient getHttpClient() {
        return HttpClientInstance.INSTANCE.getHttpClient();
    }

    public ClassicHttpResponse exchange(ClassicHttpRequest httpRequest) {
        if (Objects.isNull(httpRequest)) {
            return constructErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY, "Invalid HttpRequest");
        }
        httpRequest.addHeader(HttpHeaders.CONNECTION, "close");
        try {
            return getHttpClient().execute(httpRequest);
        } catch (Exception e) {
            log.error("@.@[error from mock server:]@.@", e);
            return constructErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }



    public ClassicHttpResponse constructErrorResponse(HttpStatus status, String msg) {
        BasicClassicHttpResponse response = new BasicClassicHttpResponse(status.value(), status.getReasonPhrase());
        response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        response.setEntity(new StringEntity(String.format("{\"errorMessage\":\"%s\"}", msg), StandardCharsets.UTF_8));
        return response;
    }

    /**
     * this is an internal HttpClient for mock server API call
     */
    @Getter
    enum HttpClientInstance {
        INSTANCE;
        private final CloseableHttpClient httpClient;
        HttpClientInstance() {
            log.info("@.@[--- Initializing closeableHttpClient ---]@.@");
            try {
                SSLContext sslContext = SSLContexts.custom()
                        .loadTrustMaterial(TrustAllStrategy.INSTANCE)
                        .build();
                final Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                        .register("http", PlainConnectionSocketFactory.INSTANCE)
                        .register("https", new SSLConnectionSocketFactory(sslContext))
                        .build();
                PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
                connectionManager.setDefaultMaxPerRoute(20);
                connectionManager.setMaxTotal(30);

                // for solve the java.net.SocketException: Connection reset error due to the HTTP persistent connection,
                this.httpClient = HttpClients.custom()
                        .setConnectionManager(connectionManager)
                        .setDefaultRequestConfig(RequestConfig.custom()
                                .build())
                        .setKeepAliveStrategy(new DefaultConnectionKeepAliveStrategy())
                        .build();

            } catch (Exception e) {
                log.error("@.@[An error occurred while initializing the CloseableHttpClient:{}]@.@", e.getMessage());
                throw new UnsupportedOperationException(e);
            }
        }

    }
}
