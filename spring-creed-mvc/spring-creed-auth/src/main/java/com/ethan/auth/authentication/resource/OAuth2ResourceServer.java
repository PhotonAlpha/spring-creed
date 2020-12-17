package com.ethan.auth.authentication.resource;

import com.ethan.auth.handler.UnAuthExceptionHandler;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;

/**
 * spring OAuth2 资源服务器配置
 * {@link org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfiguration#configure(HttpSecurity)}
 */
@EnableResourceServer
public class OAuth2ResourceServer extends ResourceServerConfigurerAdapter {
  private final UnAuthExceptionHandler unAuthExceptionHandler;

  public OAuth2ResourceServer(UnAuthExceptionHandler unAuthExceptionHandler) {
    System.out.println("OAuth2ResourceServer init...............");
    this.unAuthExceptionHandler = unAuthExceptionHandler;
  }

  @Override
  public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
    resources.stateless(true)
    //resources.resourceId(Resources.RESOURCE_ID).stateless(true)
      .accessDeniedHandler(unAuthExceptionHandler)
      .authenticationEntryPoint(unAuthExceptionHandler);
    // 如果关闭 stateless，则 accessToken 使用时的 session id 会被记录，后续请求不携带 accessToken 也可以正常响应
    // resources.resourceId(RESOURCE_ID).stateless(false);
  }

  @Override
  public void configure(HttpSecurity http) throws Exception {
    http
        // Since we want the protected resources to be accessible in the UI as well we need
        // session creation to be allowed (it's disabled by default in 2.0.6)
        //.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
        //.and()
        // 对 "/api/**" 开启认证
        .requestMatchers().antMatchers("/user/**", "/api/**")
        .and().authorizeRequests().anyRequest()
        .authenticated()
        //.and()
        //.requestMatchers()
        //.antMatchers("/api/**")
    ;
  }
}
