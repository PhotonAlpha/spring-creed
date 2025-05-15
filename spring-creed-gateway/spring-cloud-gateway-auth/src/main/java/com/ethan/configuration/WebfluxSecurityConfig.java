package com.ethan.configuration;

import com.ethan.configuration.security.DefaultAccessDeniedHandler;
import com.ethan.configuration.security.DefaultAuthenticationEntryPoint;
import com.ethan.configuration.security.DefaultAuthenticationFailureHandler;
import com.ethan.configuration.security.DefaultAuthenticationSuccessHandler;
import com.ethan.configuration.security.DefaultAuthorizationManager;
import com.ethan.configuration.security.DefaultSecurityContextRepository;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.DelegatingReactiveAuthenticationManager;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

import java.util.LinkedList;

@Configuration // 必不可少
@EnableWebFluxSecurity
public class WebfluxSecurityConfig {
    // @Resource
    // private DefaultAuthorizationManager defaultAuthorizationManager;

    // @Resource
    // private UserDetailsServiceImpl userDetailsServiceImpl;

    @Resource
    private DefaultAuthenticationSuccessHandler defaultAuthenticationSuccessHandler;

    @Resource
    private DefaultAuthenticationFailureHandler defaultAuthenticationFailureHandler;

    // @Resource
    // private TokenAuthenticationManager tokenAuthenticationManager;

    @Resource
    private DefaultSecurityContextRepository defaultSecurityContextRepository;

    @Resource
    private DefaultAuthenticationEntryPoint defaultAuthenticationEntryPoint;

    @Resource
    private DefaultAccessDeniedHandler defaultAccessDeniedHandler;
    /**
     * 自定义过滤权限
     */
    @Value("${security.noFilter:}")
    private String noFilter;
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http.authenticationManager(reactiveAuthenticationManager())
                .securityContextRepository(defaultSecurityContextRepository)
                // 请求拦截处理
                .authorizeExchange(exchange -> exchange
                        .pathMatchers(noFilter).permitAll()
                        .pathMatchers(HttpMethod.OPTIONS).permitAll()
                        .anyExchange()
                        .authenticated()
                        // authenticated() 实现就是下面这行代码
                        // .access(defaultAuthorizationManager)
                )
                // 自定义处理
                .formLogin(handling -> handling
                        .disable()
                        // .authenticationSuccessHandler(defaultAuthenticationSuccessHandler)
                        // .authenticationFailureHandler(defaultAuthenticationFailureHandler)
                )
                .exceptionHandling(handling -> handling
                        .authenticationEntryPoint(defaultAuthenticationEntryPoint)
                        .accessDeniedHandler(defaultAccessDeniedHandler)
                )
                .httpBasic(Customizer.withDefaults())
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
        ;
        return http.build();
    }

    @Bean
    public MapReactiveUserDetailsService userDetailsService() {
        UserDetails user = User.withUsername("admin")
                .password("{noop}password")
                .roles("USER")
                .build();
        return new MapReactiveUserDetailsService(user);
    }

    /**
     * 注册用户信息验证管理器，可按需求添加多个按顺序执行
     */
    @Bean
    public ReactiveAuthenticationManager reactiveAuthenticationManager() {
        LinkedList<ReactiveAuthenticationManager> managers = new LinkedList<>();
        managers.add(authentication -> {
            // 其他登陆方式 (比如手机号验证码登陆) 可在此设置不得抛出异常或者 Mono.error
            return Mono.empty();
        });
        // 必须放最后不然会优先使用用户名密码校验但是用户名密码不对时此 AuthenticationManager 会调用 Mono.error 造成后面的 AuthenticationManager 不生效
        managers.add(new UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService()));
        // managers.add(tokenAuthenticationManager);
        // managers.add(new JwtReactiveAuthenticationManager());
        return new DelegatingReactiveAuthenticationManager(managers);
    }


}
