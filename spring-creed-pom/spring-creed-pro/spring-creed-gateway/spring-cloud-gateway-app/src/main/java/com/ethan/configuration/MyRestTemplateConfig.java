package com.ethan.configuration;

import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.ConnectionKeepAliveStrategy;
import org.apache.hc.client5.http.DnsResolver;
import org.apache.hc.client5.http.HttpRoute;
import org.apache.hc.client5.http.SystemDefaultDnsResolver;
import org.apache.hc.client5.http.auth.AuthSchemeFactory;
import org.apache.hc.client5.http.auth.AuthScope;
import org.apache.hc.client5.http.auth.Credentials;
import org.apache.hc.client5.http.auth.CredentialsProvider;
import org.apache.hc.client5.http.auth.NTCredentials;
import org.apache.hc.client5.http.auth.StandardAuthScheme;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.config.TlsConfig;
import org.apache.hc.client5.http.cookie.BasicCookieStore;
import org.apache.hc.client5.http.cookie.CookieStore;
import org.apache.hc.client5.http.cookie.StandardCookieSpec;
import org.apache.hc.client5.http.impl.DefaultConnectionKeepAliveStrategy;
import org.apache.hc.client5.http.impl.auth.CredentialsProviderBuilder;
import org.apache.hc.client5.http.impl.auth.NTLMSchemeFactory;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.ManagedHttpClientConnectionFactory;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.io.ManagedHttpClientConnection;
import org.apache.hc.client5.http.socket.ConnectionSocketFactory;
import org.apache.hc.client5.http.socket.PlainConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HeaderElement;
import org.apache.hc.core5.http.HeaderElements;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.config.CharCodingConfig;
import org.apache.hc.core5.http.config.Http1Config;
import org.apache.hc.core5.http.config.Lookup;
import org.apache.hc.core5.http.config.Registry;
import org.apache.hc.core5.http.config.RegistryBuilder;
import org.apache.hc.core5.http.io.HttpConnectionFactory;
import org.apache.hc.core5.http.io.SocketConfig;
import org.apache.hc.core5.http.message.BasicHeader;
import org.apache.hc.core5.http.message.BasicHeaderElementIterator;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.hc.core5.http.ssl.TLS;
import org.apache.hc.core5.pool.PoolConcurrencyPolicy;
import org.apache.hc.core5.pool.PoolReusePolicy;
import org.apache.hc.core5.ssl.SSLContexts;
import org.apache.hc.core5.util.TimeValue;
import org.apache.hc.core5.util.Timeout;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.ExtractingResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 */
@Configuration
@Slf4j
public class MyRestTemplateConfig {
    /*
    可以配置的HTTP参数有：

     1）  http.conn-manager.timeout 当某一线程向连接池请求分配线程时，如果连接池已经没有可以分配的连接时，该线程将会被阻塞，直至http.conn-manager.timeout超时，抛出ConnectionPoolTimeoutException。

     2）  http.conn-manager.max-per-route 每个路由的最大连接数；

     3）  http.conn-manager.max-total 总的连接数；
     */
    // The maximum number of connections per route.
    @Value("${httpclient.config.connections-per-route:20}")
    private int connectionsPerRoute;

    //The maximum number of connections.
    @Value("${httpclient.config.max-total-connections:80}")
    private int maxTotalConnections;

    // Returns the connection lease request timeout used when requesting a connection from the connection manager. A timeout value of zero is interpreted as a disabled timeout.
    @Value("${httpclient.config.connection-request-timeout:120000}")
    private int connectionRequestTimeout;

    // The time to establish the connection with the remote host
    @Value("${httpclient.config.connect-timeout:10000}")
    private int connectionTimeout;

    // The time waiting for data – after establishing the connection; maximum time of inactivity between two data packets
    @Value("${httpclient.config.socket-timeout:180000}")
    private int socketTimeout;

    // The time for connection to live, the time unit is millisecond, the default value is always keep alive.
    @Value("${httpclient.config.connection-time-to-live:180000}")
    private int connectionTimeToLive;

    // The time for idle connection check, the time unit is millisecond.
    @Value("${httpclient.config.idle-connection-wait-time:300000}")
    private int idleConnectionWaitTime;

    // proxy host, To send a request through a proxy using RestTemplate
    @Value("${httpclient.config.proxy.host:}")
    private String proxyHost;

    // proxy port, To send a request through a proxy using RestTemplate
    @Value("${httpclient.config.proxy.port:8080}")
    private String proxyPort;

    /**
     * This method creates RestTemplate for Google firebase HTTP v1 API implementation
     *
     * @return RestTemplate
     */
    @Bean
    public RestTemplate restTemplate() {
        log.info("initializing firebaseOAuth2RestTemplate");
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(new ExtractingResponseErrorHandler());
        restTemplate.setInterceptors(List.of(new MyRestTemplateHttpRequestInterceptor()));

        HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        httpRequestFactory.setHttpClient(closeableHttpClient());
        // 缓存response，在interceptor中可以实现多次读取
        restTemplate.setRequestFactory(new BufferingClientHttpRequestFactory(httpRequestFactory));

        return restTemplate;
    }

    /**
     * Base implementation of HttpClient that also implements Closeable.
     *
     * @return CloseableHttpClient
     */
    @Bean
    public CloseableHttpClient closeableHttpClient() {
        log.info("--- Initializing firebaseOAuth2HttpClient for firebase endpoint ---");
            /*
            // Create HTTP/1.1 protocol configuration (创建HTTP1.1协议配置,基本用不到)
            final Http1Config h1Config = Http1Config.custom()
                    .setMaxHeaderCount(200) //
                    .setMaxLineLength(2000)
                    .build();
            // Create connection configuration (创建连接配置)
            final CharCodingConfig connectionConfig = CharCodingConfig.custom()
                    .setMalformedInputAction(CodingErrorAction.IGNORE) //该方法用于设置字符编码遇到错误输入时的处理行为,IGNORE：忽略错误输入，继续进行字符编码。
                    .setUnmappableInputAction(CodingErrorAction.IGNORE) //用于设置字符编码遇到无法映射的输入时的处理行为
                    .setCharset(StandardCharsets.UTF_8) //设置字符编码。可以指定请求和响应的字符编码
                    .build();


            // Use a custom connection factory to customize the process of
            // initialization of outgoing HTTP connections. Beside standard connection
            // configuration parameters HTTP connection factory can define message
            // parser / writer routines to be employed by individual connections.
            // (使用自定义连接工厂来自定义传出HTTP连接的初始化过程。除了标准连接配置参数之外，HTTP连接工厂还可以定义单个连接所使用的消息解析器/编写器例程。)
            // 通常情况下，不需要直接使用 HttpConnectionFactory 接口，而是通过 HttpClient 配置来创建 HTTP 连接
            final HttpConnectionFactory<ManagedHttpClientConnection> connFactory = new ManagedHttpClientConnectionFactory(
                    h1Config, connectionConfig, requestWriterFactory, responseParserFactory);
            */
            // SSL context for secure connections can be created either based on
            // system or application specific properties.
            //(安全连接的SSL上下文可以基于系统或应用程序特定的属性创建。)
            final SSLContext sslContext = SSLContexts.createSystemDefault();


            // Create a registry of custom connection socket factories for supported
            // protocol schemes.
            //(为支持的协议方案创建自定义连接套接字工厂的注册表。)
            final Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("http", PlainConnectionSocketFactory.INSTANCE)
                    .register("https", new SSLConnectionSocketFactory(sslContext))
                    .build();

            // Use custom DNS resolver to override the system DNS resolution.
            // 使用自定义 DNS 解析器覆盖系统 DNS 解析。
            final DnsResolver dnsResolver = new SystemDefaultDnsResolver() {
                @Override
                public InetAddress[] resolve(final String host) throws UnknownHostException {
                    if (host.equalsIgnoreCase("myhost")) {
                        return new InetAddress[] { InetAddress.getByAddress(new byte[] {127, 0, 0, 1}) };
                    } else {
                        return super.resolve(host);
                    }
                }

            };

            // Create a connection manager with custom configuration.
            // 使用自定义配置创建连接管理器。
            // PoolConcurrencyPolicy 连接池的并发性策略 STRICT：严格模式,在并发访问连接时，只有一个线程可以访问连接  LENIENT：宽松模式,在并发访问连接时，允许多个线程同时访问连接
            // PoolReusePolicy 连接池的连接重用策略:LIFO：后进先出，连接池返回连接时，优先选择最近返回的连接进行复用。 FIFO：先进先出，连接池返回连接时，优先选择最早返回的连接进行复用
            // TimeValue.ofMinutes(5) 时间值类型，用于设置连接在连接池中的最大空闲时间
            // dnsResolver DNS 解析器，用于解析主机名到 IP 地址
            // final PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(
            //                 socketFactoryRegistry, PoolConcurrencyPolicy.STRICT, PoolReusePolicy.LIFO, TimeValue.ofMinutes(5),
            //                 null, dnsResolver, null);

            final PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager();

            // Configure the connection manager to use socket configuration either
            // by default or for a specific host.
            // (将连接管理器配置为在默认情况下或针对特定主机使用套接字配置。)
            connManager.setDefaultSocketConfig(SocketConfig.custom()
                    .setTcpNoDelay(true)
                    .build());
            // Validate connections after 10 sec of inactivity
            // 10 秒不活动后验证连接
            connManager.setDefaultConnectionConfig(ConnectionConfig.custom()
                    .setConnectTimeout(Timeout.ofMilliseconds(connectionTimeout)) //与服务端建立连接超时时间
                    .setSocketTimeout(Timeout.ofMilliseconds(socketTimeout)) // 设置套接字超时时间，即等待响应数据的超时时间。
                    .setValidateAfterInactivity(TimeValue.ofSeconds(10)) // 连接验证间隔时间。当一个连接在池中空闲时间超过此值时，连接将被验证是否可用
                    .setTimeToLive(TimeValue.ofHours(1)) //定义连接可以保持活动状态或执行请求的总时间跨度。
                    .build());


            // Use TLS v1.3 only
            // 仅使用 TLS v1.3
            connManager.setDefaultTlsConfig(TlsConfig.custom() //用于配置 TLS 相关的参数
                    .setHandshakeTimeout(Timeout.ofSeconds(30)) //设置 TLS 握手超时时间
                    .setSupportedProtocols(TLS.V_1_3) //设置 TLS 握手超时时间
                    .build());

            // Configure total max or per route limits for persistent connections
            // that can be kept in the pool or leased by the connection manager.
            // 为可以保留在池中或由连接管理器租用的持久连接配置总的最大或每条路由限制。
            connManager.setMaxTotal(maxTotalConnections);
            connManager.setDefaultMaxPerRoute(connectionsPerRoute);
            // 自定义专属host
            // connManager.setMaxPerRoute(new HttpRoute(new HttpHost("somehost", 80)), 20);


            // Use custom cookie store if necessary.
            // 如有必要，使用自定义 cookie 存储
            final CookieStore cookieStore = new BasicCookieStore();
            // Use custom credentials provider if necessary.
            // 如有必要，请使用自定义凭据提供程序。
            final CredentialsProvider credentialsProvider = CredentialsProviderBuilder.create()
                    .add(new AuthScope(proxyHost, Integer.parseInt(proxyPort)), new NTCredentials("", "".toCharArray(), "", ""))
                    .build();

            // Create global request configuration
            // 创建全局请求配置
            final RequestConfig defaultRequestConfig = RequestConfig.custom()
                    // .setCookieSpec(StandardCookieSpec.STRICT)
                    // .setExpectContinueEnabled(true)
                    // .setTargetPreferredAuthSchemes(Arrays.asList(StandardAuthScheme.NTLM, StandardAuthScheme.DIGEST))
                    // .setProxyPreferredAuthSchemes(Arrays.asList(StandardAuthScheme.BASIC, StandardAuthScheme.NTLM))
                    .setConnectionRequestTimeout(Timeout.ofMilliseconds(connectionRequestTimeout))
                    // .setDefaultKeepAlive()
                    .build();
            // if upper not working , see https://stackoverflow.com/questions/77784888/apache-http-client-5-3-ntcredentials-no-longer-working
            Lookup<AuthSchemeFactory> authSchemeRegistry = RegistryBuilder.<AuthSchemeFactory>create()
                    .register(StandardAuthScheme.NTLM, new NTLMSchemeFactory()).build();

            // for solve the java.net.SocketException: Connection reset error due to the HTTP persistent connection,
            List<Header> headers = List.of(new BasicHeader(HttpHeaders.CONNECTION, HeaderElements.CLOSE));

            return HttpClients.custom()
                    .setDefaultHeaders(headers)
                    .setConnectionManager(connManager) //设置 HttpClient 的连接管理器
                    // .setDefaultCookieStore(cookieStore) //设置默认的 Cookie 存储
                    // .setDefaultCredentialsProvider(credentialsProvider) //设置默认的凭据提供器,即代理用户名密码
                    // .setProxy(new HttpHost("myproxy", 8080)) //设置代理服务器
                    // .setDefaultAuthSchemeRegistry()
                    .setDefaultRequestConfig(defaultRequestConfig) //设置默认的请求配置
                    .setKeepAliveStrategy(new DefaultConnectionKeepAliveStrategy())
                    .build();

    }

}
