package com.creed.authorization;


import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.approval.ApprovalStore;
import org.springframework.security.oauth2.provider.approval.TokenApprovalStore;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableAuthorizationServer
public class OAuth2AuthorizationServer extends AuthorizationServerConfigurerAdapter {
  private static final String QQ_RESOURCE_ID = "qq";
  private final AuthenticationManager authenticationManager;

  public OAuth2AuthorizationServer(AuthenticationManager authenticationManager) {
    this.authenticationManager = authenticationManager;
  }

  @Bean
  public ApprovalStore approvalStore(TokenStore tokenStore) {
    TokenApprovalStore store = new TokenApprovalStore();
    store.setTokenStore(tokenStore);
    return store;
  }

  //@Autowired
  //RedisConnectionFactory redisConnectionFactory;

  @Bean
  public TokenStore tokenStore() {
    return new InMemoryTokenStore();
    // 需要使用 redis 的话，放开这里
//            return new RedisTokenStore(redisConnectionFactory);
  }

  /**
   * {@link org.springframework.security.oauth2.provider.client.ClientCredentialsTokenEndpointFilter}
   * @param clients
   * @throws Exception
   */
  @Override
  public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
    clients.inMemory()
        .withClient("clientapp").secret("112233") // Client 账号、密码。
        .redirectUris("http://localhost:8080/api/admin") // 配置回调地址，选填。
        .authorizedGrantTypes("authorization_code", "refresh_token") // 授权码模式
        //.autoApprove(true)
        .scopes("read_userinfo", "read_contacts") // 可授权的 Scope
//                .and().withClient() // 可以继续配置新的 Client
    ;
  }

  @Override
  public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
    security.realm(QQ_RESOURCE_ID).allowFormAuthenticationForClients();
  }

  @Override
  public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
    endpoints.tokenStore(tokenStore())
        .authenticationManager(authenticationManager)
        .allowedTokenEndpointRequestMethods(HttpMethod.GET, HttpMethod.POST);

    //配置TokenService参数
    DefaultTokenServices tokenService = new DefaultTokenServices();
    tokenService.setTokenStore(endpoints.getTokenStore());
    tokenService.setSupportRefreshToken(true);
    tokenService.setClientDetailsService(endpoints.getClientDetailsService());
    tokenService.setTokenEnhancer(endpoints.getTokenEnhancer());
    //1小时
    tokenService.setAccessTokenValiditySeconds((int) TimeUnit.HOURS.toSeconds(1));
    //1小时
    tokenService.setRefreshTokenValiditySeconds((int) TimeUnit.HOURS.toSeconds(1));
    // 该字段设置设置refresh token是否重复使用,true:reuse;false:no reuse.
    tokenService.setReuseRefreshToken(false);
    endpoints.tokenServices(tokenService);
  }
}
