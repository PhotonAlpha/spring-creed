/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */
package com.ethan.security.config.oauth2;


import com.ethan.security.provider.UnAuthExceptionHandler;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractAuthenticationFilterConfigurer;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.ui.DefaultLoginPageGeneratingFilter;

/**
 * @description: spring-creed-pro
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 * @date: 11/24/2022 6:03 PM
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public UnAuthExceptionHandler exceptionHandler() {
        return new UnAuthExceptionHandler();
    }

    /**
     * 此处配置重点：过滤器{@link AuthorizeHttpRequestsConfigurer}
     *              login页面 {@link HttpSecurity#formLogin()}
     *              {@link FormLoginConfigurer#init(HttpSecurityBuilder)} -> {@link AbstractAuthenticationFilterConfigurer#configure(HttpSecurityBuilder)} )
     *              and {@link FormLoginConfigurer#FormLoginConfigurer()} will use {@link UsernamePasswordAuthenticationFilter}
     *              -> this.authFilter.setAuthenticationManager(http.getSharedObject(AuthenticationManager.class));
     *              -> {@link AbstractAuthenticationProcessingFilter#doFilter(ServletRequest, ServletResponse, FilterChain)}
     *              -> {@link UsernamePasswordAuthenticationFilter#attemptAuthentication(HttpServletRequest, HttpServletResponse)}
     *              will use {@link AuthenticationConfiguration.AuthenticationManagerDelegator} -> {@link ProviderManager#authenticate(Authentication)}
     *              and {@link AuthenticationConfiguration#initializeUserDetailsBeanManagerConfigurer(ApplicationContext)} and register {@link DaoAuthenticationProvider}
     *              -> {@link DaoAuthenticationProvider#authenticate(Authentication)}
     *
     * {@link AuthorizationFilter#doFilter(ServletRequest, ServletResponse, FilterChain)} 会检查是否有autherization
     * {@link DefaultLoginPageGeneratingFilter}
     */
    @Bean
    // @Order(2)
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http)
            throws Exception {
        http
                .authorizeHttpRequests((authorize) -> authorize
                        .anyRequest().authenticated()
                )

                // .csrf().disable()

                // Form login handles the redirect to the login page from the
                // authorization server filter chain
                // .exceptionHandling()
                // .accessDeniedHandler(exceptionHandler())
                // .authenticationEntryPoint(exceptionHandler());
        // .and()

        .formLogin(Customizer.withDefaults());
        return http.build();
    }
    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails userDetails = User.withDefaultPasswordEncoder()
                .username("user")
                .password("password")
                .roles("USER")
                .build();
        return new InMemoryUserDetailsManager(userDetails);
    }

}
