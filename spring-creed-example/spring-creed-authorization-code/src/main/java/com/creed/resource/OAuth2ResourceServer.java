package com.creed.resource;

import com.creed.handler.CustomAuthExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;

@Configuration
@EnableResourceServer
public class OAuth2ResourceServer extends ResourceServerConfigurerAdapter {
  private static final String RESOURCE_ID = "creed";

  @Bean
  public CustomAuthExceptionHandler exceptionHandler() {
    return new CustomAuthExceptionHandler();
  }

  @Override
  public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
    resources.resourceId(RESOURCE_ID).stateless(true)
      .accessDeniedHandler(exceptionHandler())
      .authenticationEntryPoint(exceptionHandler());
    // 如果关闭 stateless，则 accessToken 使用时的 session id 会被记录，后续请求不携带 accessToken 也可以正常响应
//            resources.resourceId(RESOURCE_ID).stateless(false);
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
