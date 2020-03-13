package com.creed;

import com.creed.handler.CustomAuthExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.AnyRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  @Autowired
  private CustomAuthExceptionHandler customAuthExceptionHandler;

  public AuthenticationProvider authenticationProvider() {
    return new CustomAuthenticationProvider(userDetailsService());
  }

  @Bean
  PasswordEncoder passwordEncoder(){
    return PasswordEncoderFactories.createDelegatingPasswordEncoder();
  }



  @Bean(name = BeanIds.AUTHENTICATION_MANAGER)
  @Override
  protected AuthenticationManager authenticationManager() throws Exception {
    return super.authenticationManager();
  }

/*  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.inMemoryAuthentication()
        .passwordEncoder(NoOpPasswordEncoder.getInstance())
        .withUser("admin").password("admin").roles("ADMIN")
        .and().withUser("normal").password("normal").roles("NORMAL");
    //auth.authenticationProvider(authenticationProvider());
  }*/

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    /*http

        .requestMatchers().antMatchers("/oauth/index", "/oauth/login", "/login")
        .and().authorizeRequests().anyRequest().permitAll()

        .and().formLogin()
        .loginPage("/oauth/index") // 登陆 URL 地址
        .loginProcessingUrl("/oauth/login")
        .failureUrl("/login?error")
        .defaultSuccessUrl("/user/info")
        .permitAll()
        .and()
        .logout()
        //.logoutUrl("logout")
        .permitAll()
        .and().exceptionHandling().accessDeniedPage("/login?authorization_error=true")

        //.and().requestMatcher(AnyRequestMatcher.INSTANCE).authorizeRequests().anyRequest().authenticated()
        .and().requestMatchers().antMatchers("/user/**", "/api/**")
        .and().authorizeRequests().anyRequest().authenticated()

        .and().csrf().requireCsrfProtectionMatcher(new AntPathRequestMatcher("/oauth/authorize")).disable();;
*/

    // @formatter:off
    http.
        sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)

        //.and().exceptionHandling().authenticationEntryPoint(customAuthExceptionHandler)
        //.accessDeniedHandler(customAuthExceptionHandler)

        /*.and().requestMatchers().antMatchers("/oauth/index", "/oauth/login", "/login")
        .and().authorizeRequests().anyRequest().permitAll()

        .and().requestMatcher(AnyRequestMatcher.INSTANCE)
        .authorizeRequests().anyRequest().authenticated()

        //.and().requestMatchers().antMatchers("/static/**").and().authorizeRequests().anyRequest().permitAll()
        .and().requestMatcher(PathRequest.toStaticResources().atCommonLocations()).authorizeRequests().anyRequest().permitAll()*/

        .and().authorizeRequests()
        .antMatchers("/oauth/index", "/oauth/login", "/login").permitAll()
        .antMatchers("/**.js", "/**.css").permitAll()
        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
        .anyRequest().authenticated()


        .and()
        .formLogin()
        .loginPage("/oauth/index") // 登陆 URL 地址
        .loginProcessingUrl("/oauth/login")
        .failureUrl("/login?error")
        .defaultSuccessUrl("/user/info")
        .permitAll()
        .and().logout().permitAll()

        .and().exceptionHandling()
        .accessDeniedPage("/login?authorization_error=true")

        .and()
        // TODO: put CSRF protection back into this endpoint
        .csrf()
        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
        .requireCsrfProtectionMatcher(new AntPathRequestMatcher("/oauth/authorize"))
        .disable()
    ;


    // @formatter:on

    /*// @formatter:off
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
    // @formatter:on*/
  }

  @Override
  public void configure(WebSecurity web) throws Exception {
    web
        .ignoring()
        .antMatchers("/resources/**", "/static/**", "/css/**", "/js/**", "/img/**", "/icon/**");
  }

}
