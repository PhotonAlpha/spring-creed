package com.ethan.auth.config;

import com.ethan.auth.handler.UnAuthExceptionHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
  private static final String[] AUTH_WHITELIST = {
      // -- swagger ui
      "/swagger-resources/**",
      "/swagger-ui.html",
      "/v2/api-docs",
      "/h2-console/**"
  };

  @Bean
  public UnAuthExceptionHandler unAuthExceptionHandler() {
    return new UnAuthExceptionHandler();
  }

  @Bean
  public PasswordEncoder passwordEncoder(){
    return PasswordEncoderFactories.createDelegatingPasswordEncoder();
  }

  @Bean(name = BeanIds.AUTHENTICATION_MANAGER)
  @Override
  protected AuthenticationManager authenticationManager() throws Exception {
    return super.authenticationManager();
  }

  @Bean
  public UnAuthExceptionHandler exceptionHandler() {
    return new UnAuthExceptionHandler();
  }

  /**
   * 设置csrf Token 保护策略
   * @return CsrfTokenRepository
   * Token被用户端放在Cookie中（不设置HttpOnly），
   * 同源页面每次发请求都在请求头或者参数中加入Cookie中读取的Token来完成验证。
   * CSRF只能通过浏览器自己带上Cookie，不能操作Cookie来获取到Token并加到http请求的参数中。
   * 所以CSRF本质原因是“重要操作的所有参数都是可以被攻击者猜测到的”，
   * Token加密后通过Cookie储存，只有同源页面可以读取，把Token作为重要操作的参数，
   * CSRF无法获取Token放在参数中，也无法仿造出正确的Token，就被防止掉了.
   */
  @Bean
  public CsrfTokenRepository tokenRepository() {
    return CookieCsrfTokenRepository.withHttpOnlyFalse();
    //return new HttpSessionCsrfTokenRepository();
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
        .headers().cacheControl()
        .and().frameOptions().sameOrigin()

        .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)

        .and().authorizeRequests()
        .antMatchers("/oauth/index", "/oauth/login", "/login").permitAll()
        .antMatchers("/**.js", "/**.css").permitAll()
        .antMatchers("/static/**").permitAll()
        .antMatchers(AUTH_WHITELIST).permitAll()
        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
        .anyRequest().authenticated()

        .and().formLogin()
        .loginPage("/oauth/index") // 登陆 URL 地址
        .loginProcessingUrl("/oauth/login")
        .failureUrl("/login?error")
        .defaultSuccessUrl("/user/info")
        //.failureHandler()
        .permitAll()
        .and().logout().permitAll()

        .and().exceptionHandling()
        //.accessDeniedHandler(customAuthExceptionHandler)
        //.authenticationEntryPoint(customAuthExceptionHandler)
        // 设置默认值是为了不覆盖form中的 LoginUrlAuthenticationEntryPoint
        .defaultAuthenticationEntryPointFor(exceptionHandler(), new NegatedRequestMatcher(new AntPathRequestMatcher("/oauth/**")))
        .defaultAccessDeniedHandlerFor(exceptionHandler(), new NegatedRequestMatcher(new AntPathRequestMatcher("/oauth/**")))

        .and()
        // TODO: put CSRF protection back into this endpoint
        /**
         * 配置csrf端口保护
         * {@link org.springframework.security.web.csrf.CsrfFilter}
         */
        .csrf()
        .csrfTokenRepository(tokenRepository());
  }


  /**
   * give a default implementation for user login
   * @return
   */
  @Bean
  @ConditionalOnMissingBean(UserDetailsService.class)
  public UserDetailsService userDetailsService() {
    return (username) ->
      new User(username, "{noop}123456",
          AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_USER"));
  }
}
