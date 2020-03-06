package com.creed.config;

import com.creed.service.MyUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.DefaultLoginPageConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableResourceServer
public class MyResourceServerConfig extends ResourceServerConfigurerAdapter {
  private static final String DEMO_RESOURCE_ID = "order";
  @Override
  public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
    resources.resourceId(DEMO_RESOURCE_ID).stateless(true);
  }

  @Override
  public void configure(HttpSecurity http) throws Exception {
    http
        // Since we want the protected resources to be accessible in the UI as well we need
        // session creation to be allowed (it's disabled by default in 2.0.6)
        //.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
        //.and()
        .requestMatchers()
        // 保险起见，防止被主过滤器链路拦截
        .antMatchers("/product/**")
        .antMatchers("/order/**").and()
        .authorizeRequests().anyRequest().authenticated()
    ;
/*    http
        // Since we want the protected resources to be accessible in the UI as well we need
        // session creation to be allowed (it's disabled by default in 2.0.6)
        //.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
        //.and()
        //.requestMatchers().anyRequest()
        //.and()
        //.anonymous() //默认访问是匿名访问
        //.and()
        .authorizeRequests()
//                    .antMatchers("/product/**").access("#oauth2.hasScope('select') and hasRole('ROLE_USER')")
        .antMatchers("/product/**").authenticated()
        .antMatchers("/order/**").authenticated()//配置order访问控制，必须认证过后才可以访问
        .anyRequest().authenticated()
        ;*/
  }
  //@Bean
  public JwtAuthenticationTokenFilter passwordAuthenticationFilter() {
    return new JwtAuthenticationTokenFilter();
  }

}
