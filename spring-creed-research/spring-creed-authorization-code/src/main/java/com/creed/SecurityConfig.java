package com.creed;

import com.creed.service.AccountServiceImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
  @Bean
  //@ConditionalOnMissingBean
  public UserDetailsService userDetailsService() {
    return new AccountServiceImpl();
  }

  @Bean
  public AuthenticationProvider authenticationProvider() {
    return new CustomAuthenticationProvider(userDetailsService());
  }

  @Bean
  PasswordEncoder passwordEncoder(){
    return NoOpPasswordEncoder.getInstance();
  }



  @Bean(name = BeanIds.AUTHENTICATION_MANAGER)
  @Override
  protected AuthenticationManager authenticationManager() throws Exception {
    return super.authenticationManager();
  }

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.inMemoryAuthentication()
        .passwordEncoder(NoOpPasswordEncoder.getInstance())
        .withUser("admin").password("admin").roles("ADMIN")
        .and().withUser("normal").password("normal").roles("NORMAL");
    //auth.authenticationProvider(authenticationProvider());
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
        //.requestMatchers()
        // /oauth/authorize link org.springframework.security.oauth2.provider.endpoint.AuthorizationEndpoint
        // 必须登录过的用户才可以进行 oauth2 的授权码申请
        //.antMatchers("/", "/home","/login","/oauth/authorize")
        //.and()
        .authorizeRequests()
        //.antMatchers("/oauth/**").permitAll()
        .antMatchers("/oauth/**").permitAll()
        .antMatchers("/test/echo").permitAll()
        .antMatchers("/test/admin").hasAnyRole("ADMIN")
        .antMatchers("/test/normal").access("hasRole('ROLE_NORMAL')")
        .anyRequest().authenticated()
        .and()
        .formLogin()
        .loginPage("/oauth/index") // 登陆 URL 地址
        .loginProcessingUrl("/oauth/login")
        .failureUrl("/login?error")
        .defaultSuccessUrl("/user/info")
        //.permitAll()
        .and()
        .logout()
        //.logoutUrl("logout")
        .permitAll()
        .and()
        //  .httpBasic().disable()
        .exceptionHandling()
        .accessDeniedPage("/login?authorization_error=true");
        //.and()
        // TODO: put CSRF protection back into this endpoint
      //.csrf()
      //  .requireCsrfProtectionMatcher(new AntPathRequestMatcher("/oauth/authorize"))
      //  .disable();;
  }

}
