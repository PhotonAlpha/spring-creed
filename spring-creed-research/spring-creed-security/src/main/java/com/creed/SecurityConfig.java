package com.creed;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.inMemoryAuthentication()
        .passwordEncoder(NoOpPasswordEncoder.getInstance())
        .withUser("admin").password("admin").roles("ADMIN")
        .and().withUser("normal").password("normal").roles("NORMAL");
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.authorizeRequests()
        .antMatchers("/test/echo").permitAll()
        .antMatchers("/test/admin").hasAnyRole("ADMIN")
        .antMatchers("/test/normal").access("hasRole('ROLE_NORMAL')")
        .anyRequest().authenticated()
    .and()
    .formLogin()
        //.loginPage("/logi") // 登陆 URL 地址
        .permitAll()
    .and()
    .logout()
        .logoutUrl("logout")
        .permitAll();
  }
}
