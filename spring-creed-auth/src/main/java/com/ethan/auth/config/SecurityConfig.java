package com.ethan.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

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
  PasswordEncoder passwordEncoder(){
    return new BCryptPasswordEncoder();
  }

  @Bean(name = BeanIds.AUTHENTICATION_MANAGER)
  @Override
  protected AuthenticationManager authenticationManager() throws Exception {
    return super.authenticationManager();
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.cors().and()
        .csrf().disable()
        //.exceptionHandling().authenticationEntryPoint(unAuthenticationEntryPoint()).and()
        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
        .authorizeRequests()
        .antMatchers(HttpMethod.GET, "/",
            "/**/*.html",
            "/**/*.{png,jpg,jpeg,svg.ico}",
            "/**/*.css",
            "/**/*.js").permitAll()
        .antMatchers(AUTH_WHITELIST).permitAll()
        .anyRequest().authenticated();
    // providers
    //http.addFilterBefore(tokenFilter(), UsernamePasswordAuthenticationFilter.class);
    http.headers().cacheControl();
    http.headers().frameOptions().disable();
  }
}
