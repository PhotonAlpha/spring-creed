package com.ethan.core.config;

import com.ethan.core.filter.JwtAuthenticationTokenFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {
  private static final String[] AUTH_WHITELIST = {
      // -- swagger ui
      "/swagger-resources/**",
      "/swagger-ui.html",
      "/v2/api-docs",
      "/webjars/**",
      "/api/test/**"
  };
  private final JwtAuthenticationTokenFilter tokenFilter;

  public WebSecurityConfiguration(JwtAuthenticationTokenFilter tokenFilter) {
    this.tokenFilter = tokenFilter;
  }

  @Bean
  public UnAuthenticationEntryPoint unAuthenticationEntryPoint() {
    return new UnAuthenticationEntryPoint();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.cors().and()
        .csrf().disable()
        .exceptionHandling().authenticationEntryPoint(unAuthenticationEntryPoint()).and()
        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
        .authorizeRequests()
        .antMatchers(HttpMethod.GET, "/",
            "/**/*.html",
            "/**/*.{png,jpg,jpeg,svg.ico}",
            "/**/*.css",
            "/**/*.js").permitAll()
        .antMatchers("/api/auth").permitAll()
        .antMatchers("/v1/booklet/**").permitAll()
        .antMatchers("/h2-console/**").permitAll()
        .antMatchers(AUTH_WHITELIST).permitAll()
        .anyRequest().authenticated();
    // providers
    http.addFilterBefore(tokenFilter, UsernamePasswordAuthenticationFilter.class);
    http.headers().cacheControl();
    http.headers().frameOptions().disable();
  }

  @Bean(name = BeanIds.AUTHENTICATION_MANAGER)
  @Override
  protected AuthenticationManager authenticationManager() throws Exception {
    return super.authenticationManager();
  }
  @Bean
  public CustomAuthenticationProvider customAuthenticationProvider() {
    return new CustomAuthenticationProvider().setPasswordEncoder(passwordEncoder());
  }

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.authenticationProvider(customAuthenticationProvider());
  }
}
