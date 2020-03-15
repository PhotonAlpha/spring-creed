/**
 * describe: copy right by @author
 *
 * @author xxx
 * @date 2020/03/15
 */
package com.ethan.cloud.config;

import lombok.extern.slf4j.Slf4j;
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
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import java.security.KeyStore;

@Slf4j
@Configuration
public class TemplateConfig {
  private static final int DEFAULT_KEEP_ALIVE_TIME_MILLIS = 20 * 1000;

  /** httpclient 连接池配置 线程安全。
    * 默认情况下httpclient对象使用的连接管理类，内部维护一个HttpClientConnection连接池，连接池以HttpRoute为单位保持多个连接以供使用，
   * 每次请求会根据HttpRoute优先从池中获取连接，获取不到的情况下在连接数未超过配置时才会创建新连接。
   */
  protected PoolingHttpClientConnectionManager poolingHttpClientConnectionManager(SSLConnectionSocketFactory csf, HttpPoolConfig httpPoolConfig) {
    Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
        .register("https", csf)
        .register("http", new PlainConnectionSocketFactory())
        .build();

    PoolingHttpClientConnectionManager  connManager = new PoolingHttpClientConnectionManager(registry);
    connManager.setMaxTotal(httpPoolConfig.getMaxTotal());
    connManager.setDefaultMaxPerRoute(httpPoolConfig.getDefaultMaxPerRoute());
    return connManager;
  }
/** httpclient 请求参数配置
// 这个类属性很多，所以使用了典型的构建器模式，通过内部类RequestConfig.Builder的build()方法来创建RequestConfig对象。
// 几个常用的属性：
//    ConnectTimeout：建立连接的超时时间

//    ConnectionRequestTimeout：从连接池中获取连接超时时间

//    SocketTimeout：socket传输超时时间

  //    StaleConnectionCheckEnabled：是否启用检查失效连接，这个属性在4.4版本中被PoolingHttpClientConnectionManager中的#setValidateAfterInactivity(int)替换掉了。
 */
  protected RequestConfig requestConfig(HttpPoolConfig httpPoolConfig) {
    return RequestConfig.custom()
        .setConnectTimeout(httpPoolConfig.getConnectTimeout())
        .setSocketTimeout(httpPoolConfig.getSocketTimeout())
        .setConnectionRequestTimeout(httpPoolConfig.getConnectionRequestTimeout()).build();
  }
  // httpclient KeepAliveStrategy配置
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

  protected CloseableHttpClient httpClient(KeyStore clientStore, KeyStore serverStore, String serverStorePwd, HttpPoolConfig httpPoolConfig) throws Exception {
    SSLContext sslContext = org.apache.http.ssl.SSLContexts.custom()
        // private key
        .loadKeyMaterial(serverStore, serverStorePwd.toCharArray())
        // public key
        .loadTrustMaterial(clientStore, new TrustSelfSignedStrategy())
        .build();
    // HostnameVerifier strategy
    final HostnameVerifier verifier = NoopHostnameVerifier.INSTANCE;
   /* (String s, SSLSession sslSession) -> {
      log.info("SSL Verify host:{} <--> peerHost:{}", s , sslSession.getPeerHost());
      return s.equals(sslSession.getPeerHost());
    };*/

    SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext, verifier);
    // HttpClients final config
    CloseableHttpClient httpClient = HttpClients.custom()
        .setConnectionManager(poolingHttpClientConnectionManager(csf, httpPoolConfig))
        .setSSLSocketFactory(csf)
        .setSSLHostnameVerifier(verifier)
        .setKeepAliveStrategy(connectionKeepAliveStrategy())
        .setDefaultRequestConfig(requestConfig(httpPoolConfig))
        .build();
    return httpClient;
  }

  protected CloseableHttpClient httpClient(HttpPoolConfig httpPoolConfig) {
    SSLConnectionSocketFactory csf = SSLConnectionSocketFactory.getSocketFactory();

    // HttpClients final config
    CloseableHttpClient httpClient = HttpClients.custom()
        .setConnectionManager(poolingHttpClientConnectionManager(csf, httpPoolConfig))
        .setSSLSocketFactory(csf)
        .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
        .setKeepAliveStrategy(connectionKeepAliveStrategy())
        .setDefaultRequestConfig(requestConfig(httpPoolConfig))
        .build();
    return httpClient;
  }

  /*@Bean
  public RestTemplate restTemplate(HttpPoolConfig httpPoolConfig) {
    String trustStorePath = "/custom_test.jks";
    String storePath = "/custom_test_trust.jks";
    RestTemplate restTemplate = new RestTemplate();
    try (FileInputStream trustFileStream = new FileInputStream(trustStorePath);
         FileInputStream serverFileStream = new FileInputStream(storePath)) {
      String pwd = "password";
      KeyStore clientStore = KeyStore.getInstance(KeyStore.getDefaultType());
      clientStore.load(trustFileStream, pwd.toCharArray());

      String storePwd = "password";
      KeyStore serverStore = KeyStore.getInstance(KeyStore.getDefaultType());
      serverStore.load(serverFileStream, storePwd.toCharArray());
      // set server and client auth
      CloseableHttpClient httpClient = httpClient(clientStore, serverStore, storePwd, httpPoolConfig);

      HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
      requestFactory.setHttpClient(httpClient);

      restTemplate.setRequestFactory(requestFactory);
    } catch (Exception e) {
      log.error("init SSL restTemplate Failure", e);
    }
    return restTemplate;
  }*/
  @Bean
  public RestTemplate restTemplate(HttpPoolConfig httpPoolConfig) {
    RestTemplate restTemplate = new RestTemplate();
    CloseableHttpClient httpClient = httpClient(httpPoolConfig);

    HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
    requestFactory.setHttpClient(httpClient);

    restTemplate.setRequestFactory(requestFactory);

    return restTemplate;
  }
}
