package com.ethan.sso.config;

import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerTokenServicesConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;

@Configuration
@EnableResourceServer
@EnableWebSecurity(debug = true)
public class OAuth2ResourceServer extends ResourceServerConfigurerAdapter {
  /**
   * ResourceServerSecurityConfigurer 可配置属性
   * tokenServices：ResourceServerTokenServices 类的实例，用来实现令牌业务逻辑服务
   * resourceId：这个资源服务的ID，这个属性是可选的，但是推荐设置并在授权服务中进行验证
   * tokenExtractor 令牌提取器用来提取请求中的令牌
   * 请求匹配器，用来设置需要进行保护的资源路径，默认的情况下是受保护资源服务的全部路径
   * 受保护资源的访问规则，默认的规则是简单的身份验证（plain authenticated）
   * 其他的自定义权限保护规则通过 HttpSecurity 来进行配置
   * 解析令牌方法：
   * 使用 DefaultTokenServices 在资源服务器本地配置令牌存储、解码、解析方式
   * 使用 RemoteTokenServices 资源服务器通过 HTTP 请求来解码令牌，每次都请求授权服务器端点 /oauth/check_token
   * 若授权服务器是 JWT 非对称加密，则需要请求授权服务器的 /oauth/token_key 来获取公钥 key 进行解码
   *
   * {@link org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerConfiguration} 会检查配置并引用
   * {@link ResourceServerTokenServicesConfiguration.RemoteTokenServicesConfiguration.TokenInfoServicesConfiguration#remoteTokenServices()}
   * 触发初始化 {@link org.springframework.security.oauth2.provider.token.RemoteTokenServices}
   *
   * 此处会引用配置此配置
   * {@link org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfiguration#tokenServices}
   *
   * 实际情况可以自定义 设置 redis tokenStore 设置到 DefaultTokenServices。
   * {@link org.springframework.security.oauth2.provider.token.DefaultTokenServices}
   *
   * @param resources
   * @throws Exception
   */
  @Override
  public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
    resources.stateless(true)
    .tokenStore(tokenStore());
    // 如果关闭 stateless，则 accessToken 使用时的 session id 会被记录，后续请求不携带 accessToken 也可以正常响应
  }

  @Bean
  public JwtAccessTokenConverter jwtTokenEnhancer(){
    JwtAccessTokenConverter converter= new JwtAccessTokenConverter ();
    Resource resource= new ClassPathResource("ethan-jwt.cer");
    String  publicKey;
    try {
      publicKey = new String(FileCopyUtils.copyToByteArray(resource.getInputStream()));
    } catch (IOException e) {
      throw new RuntimeException();
    }
    converter.setVerifierKey(publicKey);
    return converter;
  }
  @Bean
  public TokenStore tokenStore(){
    return new JwtTokenStore(jwtTokenEnhancer());
  }

}