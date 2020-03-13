package com.creed.config;

import com.creed.service.MyUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.AnyRequestMatcher;

@Configuration
@EnableWebSecurity
public class MySecurityConfiguration extends WebSecurityConfigurerAdapter {

  @Bean(name = BeanIds.AUTHENTICATION_MANAGER)
  @Override
  protected AuthenticationManager authenticationManager() throws Exception {
    return super.authenticationManager();
  }

  @Bean
  PasswordEncoder passwordEncoder(){
    return NoOpPasswordEncoder.getInstance();
  }

  @Bean
  public UserDetailsService userDetailsService() {
    return new MyUserDetailsService();
  }
  /*@Bean
  @Override
  protected UserDetailsService userDetailsService(){
    InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
    // 创建两个 qq 用户
    manager.createUser(User.withUsername("250577914").password("123456").authorities("USER").build());
    manager.createUser(User.withUsername("920129126").password("123456").authorities("USER").build());
    return manager;
  }*/
  /*@Bean
  @Override
  protected UserDetailsService userDetailsService(){
    InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
    manager.createUser(User.withUsername("user_1").password("123456").authorities("USER").build());
    manager.createUser(User.withUsername("user_2").password("123456").authorities("USER").build());
    return manager;
  }*/

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    // @formatter:off
    http.
        sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
        .and().requestMatchers()
        // /oauth/authorize link org.springframework.security.oauth2.provider.endpoint.AuthorizationEndpoint
        // 必须登录过的用户才可以进行 oauth2 的授权码申请
        //.antMatchers("/", "/home","/login","/oauth/authorize")
        .antMatchers("/", "/home","/login")

        //.and()
        //.authorizeRequests()
        //.anyRequest().permitAll()

        .and()
        .formLogin()
        //.loginPage("/login")
        .and().logout().permitAll()

        .and().requestMatcher(AnyRequestMatcher.INSTANCE).authorizeRequests().anyRequest().authenticated()

        .and().httpBasic().disable()

        .exceptionHandling()
        .accessDeniedPage("/login?authorization_error=true")

        .and()
        // TODO: put CSRF protection back into this endpoint
        .csrf()
        .requireCsrfProtectionMatcher(new AntPathRequestMatcher("/oauth/authorize"))
        .disable()
     ;
    // @formatter:on
  }

/*
  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    //auth.inMemoryAuthentication().withUser("admin").password(new BCryptPasswordEncoder().encode("admin")).roles("ADMIN","USER");
    //auth.inMemoryAuthentication().withUser("normal").password(new BCryptPasswordEncoder().encode("normal")).roles("USER");
    auth.authenticationProvider(authenticationProvider());
  }

  public AuthenticationProvider authenticationProvider() {
    return new MyAuthenticationProvider(userDetailsService());
  }
*/


  @Override
  public void configure(WebSecurity web) throws Exception {
    super.configure(web);
  }
}
