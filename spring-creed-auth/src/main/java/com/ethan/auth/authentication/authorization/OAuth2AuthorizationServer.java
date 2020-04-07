package com.ethan.auth.authentication.authorization;


import com.ethan.auth.constants.Resources;
import lombok.CustomLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.oauth2.provider.approval.ApprovalStore;
import org.springframework.security.oauth2.provider.approval.TokenApprovalStore;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;

import java.util.concurrent.TimeUnit;

/**
 * spring OAuth2 配置
 */
@EnableAuthorizationServer
@Slf4j
public class OAuth2AuthorizationServer extends AuthorizationServerConfigurerAdapter {
  private final AuthenticationManager authenticationManager;

  public OAuth2AuthorizationServer(AuthenticationManager authenticationManager) {
    log.info("OAuth2AuthorizationServer start init.");
    this.authenticationManager = authenticationManager;
  }

  @Bean
  @ConditionalOnMissingBean(ApprovalStore.class)
  public ApprovalStore approvalStore(TokenStore tokenStore) {
    TokenApprovalStore store = new TokenApprovalStore();
    store.setTokenStore(tokenStore);
    return store;
  }

  /**
   * token 是在 {@link  org.springframework.security.oauth2.provider.token.AbstractTokenGranter#grant(String, TokenRequest)} 开始 生成token
   * getAccessToken(client, tokenRequest) 会调用services生成token {@link org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices}
   *
   * 默认token services实现 是在 {@link  DefaultTokenServices} 中调用
   *    OAuth2AccessToken accessToken = createAccessToken(authentication, refreshToken);
   * 		tokenStore.storeAccessToken(accessToken, authentication);
   *
   * 一下摘自源码的一段说明
   * 	Base implementation for token services using random UUID values for the access token and refresh token values. The
   *  main extension point for customizations is the {@link org.springframework.security.oauth2.provider.token.TokenEnhancer} which will be called after the access and
   *  refresh tokens have been generated but before they are stored.
   * @return
   */
  //@Autowired
  //RedisConnectionFactory redisConnectionFactory;

  @Bean
  @ConditionalOnMissingBean(TokenStore.class)
  public TokenStore tokenStore() {
    InMemoryTokenStore tokenStore = new InMemoryTokenStore();
    return tokenStore;
    // 需要使用 redis 的话，放开这里
    // return new RedisTokenStore(redisConnectionFactory);
  }

  /**
   * {@link org.springframework.security.oauth2.provider.client.ClientCredentialsTokenEndpointFilter}
   * @param clients
   * @throws Exception
   */
  @Override
  public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
    //此处server可通过redis 或者 jdbc 验证，默认配置inMemory读取
    // 一般小项目设计可以使用 授权码模式 & 客户端模式


    clients.inMemory()
        .withClient("clientapp").secret("{noop}112233") // Client 账号、密码。
        .resourceIds(Resources.RESOURCE_ID)
        .redirectUris("http://localhost:8080/auth/grant") // 配置回调地址，选填。
        .authorizedGrantTypes("authorization_code", "refresh_token") // 授权码模式
        //.autoApprove(true)
        .scopes("read_userinfo", "read_contacts") // 可授权的 Scope


//                .and().withClient() // 可以继续配置新的 Client

        .and().withClient("client_imp")
        .resourceIds(Resources.RESOURCE_ID)
        .authorizedGrantTypes("implicit")  // 简化模式
        .redirectUris("http://localhost:8080/auth/admin")
        .scopes("read_userinfo")
        .authorities("client")
        .secret("{noop}112233")

        .and().withClient("client_cre").secret("{noop}112233") // Client 账号、密码。
        .resourceIds(Resources.RESOURCE_ID)
        .authorizedGrantTypes("client_credentials", "refresh_token") // 客户端模式
        .scopes("read_userinfo")
        .authorities("oauth2")

        // 密码模式
        .and().withClient("client_pwd").secret("{noop}112233")
        .resourceIds(Resources.RESOURCE_ID)
        .authorizedGrantTypes("password", "refresh_token")  // 密码模式
        .scopes("read_userinfo")
        .authorities("oauth2");
  }

  /**
   * 如果 auth server 与 resource server分离，可以设置false
   * @param security
   * @throws Exception
   */
  @Override
  public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
    security.realm(Resources.RESOURCE_ID).allowFormAuthenticationForClients();
  }

  /**
   * {@link AuthorizationServerEndpointsConfigurer#userApprovalHandler()}
   *
   * @param endpoints
   * @throws Exception
   */
  @Override
  public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
    ApprovalStore approvalStore = approvalStore(tokenStore());

    endpoints.tokenStore(tokenStore())
        // ApprovalStoreUserApprovalHandler 配置， 格式为 scope.*
        .approvalStore(approvalStore)
        // customer exceptionTranslator
        //.exceptionTranslator(translator())


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

    // ！！！！！！！设置token增强器， 此处可以修改token格式
    //tokenService.setTokenEnhancer(new MyTokenEnhancer());

    endpoints.tokenServices(tokenService);
  }

}
