/**
 * describe: copy right by @author
 *
 * @author xxx
 * @date 2020/04/08
 */
package com.ethan.sso.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity(debug = true)
public class OAuth2SecurityConfig extends WebSecurityConfigurerAdapter {
  @Bean
  @Override
  public AuthenticationManager authenticationManagerBean() throws Exception {
    return super.authenticationManagerBean();
  }
  @Bean
  public PasswordEncoder passwordEncoder(){
    return PasswordEncoderFactories.createDelegatingPasswordEncoder();
  }


  @Bean
  @ConditionalOnMissingBean(UserDetailsService.class)
  public UserDetailsService userDetailsService() {
    return (username) -> new User(username, "{noop}123456",
        AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_USER"));
  }

  /* // inMemoryAuthentication setup
  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.inMemoryAuthentication()
        .withUser("admin")
        .password(PasswordEncoderFactories.createDelegatingPasswordEncoder().encode("admin"))
        .roles("test")
    ;
  }*/

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.csrf().disable()
        .formLogin().loginPage("/login").permitAll()
        .and()
        .logout()
        .and()
        .requestMatchers()
        .antMatchers("/", "/login", "/oauth/authorize", "/oauth/confirm_access", "/exit")
        .and()
        .authorizeRequests()
        .antMatchers("/webjars/**").permitAll()
        .anyRequest().authenticated();
  }
}
