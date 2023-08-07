package com.ethan.core.config;

import org.apache.http.HeaderElement;
import org.apache.http.HeaderElementIterator;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContexts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import java.util.concurrent.TimeUnit;

@Configuration
public class BeanConfig {
  /**
   * Algorithm Reference: https://github.com/auth0/java-jwt/blob/master/lib/src/main/java/com/auth0/jwt/algorithms/Algorithm.java
   * @return JWTVerifier
   * @Deprecated because of have more powerful tools
   */
  /*@Bean
  public JWTVerifier jwtVerifier() {
    Algorithm algorithm = Algorithm.HMAC512(secret);
    return JWT.require(algorithm)
        .withIssuer(issuer)
        .acceptLeeway(leeway)
        .build();
  }*/

  private static final Logger log = LoggerFactory.getLogger(BeanConfig.class);
    /**
     * send request time limit milliseconds
     */
    private Integer connectTimeout = 5_000;
    /**
     * fetching data time limit milliseconds
     *
     * 当连上目标服务器， 请求处于长时间等待的时候，会用到此参数，如果超过2.5mins = 150000, 还未返回，将会得到以下的异常
     * [java.net.SocketTimeoutException: Read timed out]
     */
    @Value("${sfw.socket.timeout:600000}")
    private Integer socketTimeout;
    /**
     * connect thread pool time limit milliseconds
     */
    private Integer connectionRequestTimeout = 5_000;
    /**
     * min thread pool
     */
    private Integer defaultMaxPerRoute = 100;
    /**
     * max thread pool
     */
    private Integer maxTotal = 300;
    private static final int DEFAULT_KEEP_ALIVE_TIME_MILLIS = 20 * 1000;

    protected PoolingHttpClientConnectionManager poolingHttpClientConnectionManager(SSLConnectionSocketFactory csf) {
        Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("https", csf)
                .register("http", new PlainConnectionSocketFactory())
                .build();
        PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(registry);
        connManager.setMaxTotal(maxTotal);
        connManager.setDefaultMaxPerRoute(defaultMaxPerRoute);
        return connManager;
    }

    protected RequestConfig requestConfig() {
        return RequestConfig.custom()
                .setConnectTimeout(connectTimeout)
                .setSocketTimeout(socketTimeout)
                .setConnectionRequestTimeout(connectionRequestTimeout).build();
    }

    // httpclient KeepAliveStrategy
    protected ConnectionKeepAliveStrategy connectionKeepAliveStrategy() {
        return (HttpResponse response, HttpContext context) -> {
            HeaderElementIterator it = new BasicHeaderElementIterator
                    (response.headerIterator(HTTP.CONN_KEEP_ALIVE));
            while (it.hasNext()) {
                HeaderElement he = it.nextElement();
                String param = he.getName();
                String value = he.getValue();
                if (value != null && param.equalsIgnoreCase("timeout")) {
                    return Long.parseLong(value) * 1000;
                }
            }
            return DEFAULT_KEEP_ALIVE_TIME_MILLIS;
        };
    }

    protected CloseableHttpClient httpClient() throws Exception {
        TrustStrategy acceptingTrustStrategy = (cert, authType) -> true;
        SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(null, acceptingTrustStrategy).build();
        SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);
        // HostnameVerifier strategy
        final HostnameVerifier verifier = (String s, SSLSession sslSession) -> s.equals(sslSession.getPeerHost());
        // HttpClients final config
        CloseableHttpClient httpClient = HttpClients.custom()
                .setConnectionManager(poolingHttpClientConnectionManager(csf))
                .setSSLSocketFactory(csf)
                .setSSLHostnameVerifier(verifier)
                .setKeepAliveStrategy(connectionKeepAliveStrategy())
                .setDefaultRequestConfig(requestConfig())
                .setConnectionTimeToLive(1000, TimeUnit.MILLISECONDS) // solve the "Connection is still allocated issue"
                .setRetryHandler(new DefaultHttpRequestRetryHandler(3, true))
                .build();
        return httpClient;
    }


    @Bean
    public RestTemplate restTemplate() throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        try {
            CloseableHttpClient httpClient = httpClient();
            HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
            requestFactory.setHttpClient(httpClient);
            restTemplate.setRequestFactory(requestFactory);
        } catch (Exception e) {
            log.error("init restTemplate", e);
            throw e;
        }
        return restTemplate;
    }
}
