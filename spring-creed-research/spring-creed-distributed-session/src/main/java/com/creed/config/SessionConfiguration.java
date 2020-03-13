package com.creed.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.session.web.http.CookieHttpSessionIdResolver;
import org.springframework.session.web.http.DefaultCookieSerializer;
import org.springframework.session.web.http.HeaderHttpSessionIdResolver;
import org.springframework.session.web.http.HttpSessionIdResolver;
import org.springframework.session.web.http.SessionEventHttpSessionListenerAdapter;

import javax.servlet.http.HttpSessionListener;
import java.util.List;

@Configuration
@EnableRedisHttpSession(redisNamespace = "creed:session")
public class SessionConfiguration {
  /*@Bean
  public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
    // 创建 RedisTemplate 对象
    RedisTemplate<String, Object> template = new RedisTemplate<>();
    // 设置 RedisConnection 工厂。😈 它就是实现多种 Java Redis 客户端接入的秘密工厂。感兴趣的胖友，可以自己去撸下。
    template.setConnectionFactory(factory);

    // 使用 String 序列化方式，序列化 KEY 。
    template.setKeySerializer(RedisSerializer.string());

    // 使用 JSON 序列化方式（库是 Jackson ），序列化 VALUE 。
    template.setValueSerializer(RedisSerializer.json());
    return template;
  }*/
  /**
   * 创建 {@link org.springframework.session.data.redis.RedisOperationsSessionRepository} 使用的 RedisSerializer Bean 。
   *
   * 具体可以看看 {@link org.springframework.session.data.redis.config.annotation.web.http.RedisHttpSessionConfiguration#setDefaultRedisSerializer(RedisSerializer)} 方法，
   * 它会引入名字为 "springSessionDefaultRedisSerializer" 的 Bean 。
   *
   * @return RedisSerializer Bean
   * java.lang.ClassCastException: class java.util.LinkedHashMap cannot be cast to class org.springframework.security.web.csrf.CsrfToken (java.util.LinkedHashMap is in module java.base of loader 'bootstrap'; org.springframework.security.web.csrf.CsrfToken is in unnamed module of loader 'app')
   *
   * 相比 「3.3 SessionConfiguration」 来说，去掉了自定义的 JSON RedisSerializer Bean 的配置。原因是，HttpSession 的 attributes 属性，是 Map<String, Object> 类型。
   *
   *     在序列化 Session 到 Redis 中时，不存在问题。
   *     在反序列化 Redis 的 key-value 键值对成 Session 时，如果 attributes 的 value 存在 POJO 对象的时候，因为不知道该 value 是什么 POJO 对象，导致无法反序列化错误。
   */
/*  @Bean(name = "springSessionDefaultRedisSerializer")
  public RedisSerializer<Object> springSessionDefaultRedisSerializer() {
    return new GenericJackson2JsonRedisSerializer(new ObjectMapper());
  }*/

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

/*  @Bean
  public HeaderHttpSessionIdResolver sessionIdResolver() {
//        return HeaderHttpSessionIdResolver.xAuthToken();
//        return HeaderHttpSessionIdResolver.authenticationInfo();
    return new HeaderHttpSessionIdResolver("token");
  }*/
}
