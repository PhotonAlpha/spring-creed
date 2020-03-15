/**
 * describe: copy right by @author
 *
 * @author xxx
 * @date 2020/03/15
 */
package com.ethan.cloud.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "httpclient")
public class HttpPoolConfig {
  /**
   * send request time limit milliseconds  请求连接池等待时间
   */
  private Integer connectTimeout = 5000;
  /**
   * fetching data time limit milliseconds 等待请求响应时间
   */
  private Integer socketTimeout = 3000000;
  /**
   * connect thread pool time limit milliseconds 请求超时时间，
   */
  private Integer connectionRequestTimeout = 1000;
  /**
   * min thread pool 最小连接数
   */
  private Integer defaultMaxPerRoute = 200;
  /**
   * max thread pool 最大连接数
   */
  private Integer maxTotal = 600;
}
