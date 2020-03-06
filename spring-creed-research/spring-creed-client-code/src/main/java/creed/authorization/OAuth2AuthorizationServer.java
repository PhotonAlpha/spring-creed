package creed.authorization;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
  private static final String DEMO_RESOURCE_ID = "order";
  private final AuthenticationManager authenticationManager;

  public OAuth2AuthorizationServer(AuthenticationManager authenticationManager) {
    this.authenticationManager = authenticationManager;
  }

  @Bean
  public ApprovalStore approvalStore() {
    TokenApprovalStore store = new TokenApprovalStore();
    store.setTokenStore(tokenStore());
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
    //        password 方案一：明文存储，用于测试，不能用于生产
            String finalSecret = "112233";
    //        password 方案二：用 BCrypt 对密码编码
    //        String finalSecret = new BCryptPasswordEncoder().encode("123456");
    // password 方案三：支持多种编码，通过密码的前缀区分编码方式
    //String finalSecret = "{bcrypt}"+new BCryptPasswordEncoder().encode("123456");
    //配置两个客户端,一个用于password认证一个用于client认证
    clients.inMemory()
        .withClient("client_1").secret(finalSecret) // Client 账号、密码。
        .resourceIds(DEMO_RESOURCE_ID)
        .authorizedGrantTypes("client_credentials", "refresh_token") // 授权码模式
        .scopes("select")
        .authorities("oauth2")
        .and().withClient("client_2")
        .resourceIds(DEMO_RESOURCE_ID)
        .authorizedGrantTypes("password", "refresh_token")
        .scopes("select")
        .authorities("oauth2")
        .secret(finalSecret);
  }

  @Override
  public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
    security.realm(DEMO_RESOURCE_ID).allowFormAuthenticationForClients();
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
