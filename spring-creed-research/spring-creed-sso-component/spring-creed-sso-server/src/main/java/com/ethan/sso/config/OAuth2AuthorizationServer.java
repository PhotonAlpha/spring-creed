/**
 * describe: copy right by @author
 *
 * @author xxx
 * @date 2020/04/08
 */
package com.ethan.sso.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;

@Configuration
@EnableAuthorizationServer
public class OAuth2AuthorizationServer extends AuthorizationServerConfigurerAdapter {
  @Autowired
  private AuthenticationManager authenticationManager;

  @Override
  public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
    /* 配置token获取合验证时的策略 */
    security.tokenKeyAccess("permitAll()").checkTokenAccess("isAuthenticated()");
  }

  /**
   * {@link org.springframework.security.oauth2.provider.client.ClientCredentialsTokenEndpointFilter}
   * @param clients
   * @throws Exception
   */
  @Override
  public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
    // 配置oauth2的 client信息
    // authorizedGrantTypes 有4种，这里只开启2种
    // secret密码配置从 Spring Security 5.0开始必须以 {bcrypt}+加密后的密码 这种格式填写

    //此处server可通过redis 或者 jdbc 验证，默认配置inMemory读取
    // 一般小项目设计可以使用 授权码模式 & 客户端模式
    clients.inMemory()
        .withClient("oauth2_client").secret("oauth2_client_secret") // Client 账号、密码。
        // .resourceIds(Resources.RESOURCE_ID)
        .redirectUris("http://localhost:8080/login") // 配置回调地址，选填。
        .authorizedGrantTypes("authorization_code", "refresh_token") // 授权码模式
        //.autoApprove(true)
        .autoApprove(".*")
        .scopes("read", "write") // 可授权的 Scope
    ;
  }

  @Override
  public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
    // 配置tokenStore
    endpoints.authenticationManager(authenticationManager).tokenStore(memoryTokenStore());
  }
  // 使用最基本的InMemoryTokenStore生成token
  @Bean
  public TokenStore memoryTokenStore() {
    return new InMemoryTokenStore();
  }

}
