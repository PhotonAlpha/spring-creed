/**
 * describe: copy right by @author
 *
 * @author xxx
 * @date 2020/04/07
 */
package com.ethan.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.session.web.http.CookieHttpSessionIdResolver;
import org.springframework.session.web.http.DefaultCookieSerializer;
import org.springframework.session.web.http.HttpSessionIdResolver;

@Configuration
@EnableRedisHttpSession(redisNamespace = "creed:session")
public class SessionConfiguration {


  /**
   * 自定义 sessionid 在 Cookie 中，使用别的 KEY 呢，例如说 "JSESSIONID" 。我们可以通过自定义 CookieHttpSessionIdResolver Bean 来实现
   * @return
   */
  @Bean
  public HttpSessionIdResolver sessionIdResolver() {
    // 创建 CookieHttpSessionIdResolver 对象
    CookieHttpSessionIdResolver sessionIdResolver = new CookieHttpSessionIdResolver();

    // 创建 DefaultCookieSerializer 对象
    DefaultCookieSerializer cookieSerializer = new DefaultCookieSerializer();
    sessionIdResolver.setCookieSerializer(cookieSerializer); // 设置到 sessionIdResolver 中
    cookieSerializer.setCookieName("JSESSIONID");
    return sessionIdResolver;

    //return HeaderHttpSessionIdResolver.authenticationInfo();
    //return HeaderHttpSessionIdResolver.xAuthToken();
  }
}
