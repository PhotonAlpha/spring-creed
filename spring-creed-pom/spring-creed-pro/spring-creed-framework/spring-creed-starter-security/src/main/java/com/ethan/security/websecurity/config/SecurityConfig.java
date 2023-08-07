/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */
package com.ethan.security.websecurity.config;


import com.ethan.security.core.context.TransmittableThreadLocalSecurityContextHolderStrategy;
import com.ethan.security.provider.UnAuthExceptionHandler;
import com.ethan.security.websecurity.filter.LoginTokenAuthenticationFilter;
import com.ethan.security.websecurity.provider.CreedUserDetailsManager;
import com.ethan.security.websecurity.repository.CreedAuthorityRepository;
import com.ethan.security.websecurity.repository.CreedUserRepository;
import com.ethan.security.websecurity.repository.CreedGroupsAuthoritiesRepository;
import com.ethan.security.websecurity.repository.CreedGroupsMembersRepository;
import com.ethan.security.websecurity.repository.CreedGroupsRepository;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import jakarta.annotation.Resource;
import jakarta.annotation.security.PermitAll;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.config.MethodInvokingFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractAuthenticationFilterConfigurer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.introspection.NimbusOpaqueTokenIntrospector;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.ui.DefaultLoginPageGeneratingFilter;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.PathPatternsRequestCondition;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @description: spring-creed-pro
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 * @date: 11/24/2022 6:03 PM
 */
@Configuration
@EnableWebSecurity
//启用验证权限的注解
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfig {
    @Resource
    private ApplicationContext applicationContext;

    @Resource
    private CreedSecurityProperties securityProperties;

    @Bean
    public UnAuthExceptionHandler exceptionHandler() {
        return new UnAuthExceptionHandler();
    }


    @Bean
    public LoginTokenAuthenticationFilter loginTokenAuthenticationFilter() {
        return new LoginTokenAuthenticationFilter();
    }

    /**
     * 此处配置重点：过滤器{@link AuthorizeHttpRequestsConfigurer}
     * login页面 {@link HttpSecurity#formLogin()}
     * {@link FormLoginConfigurer#init(HttpSecurityBuilder)} -> {@link AbstractAuthenticationFilterConfigurer#configure(HttpSecurityBuilder)} )
     * and {@link FormLoginConfigurer#FormLoginConfigurer()} will use {@link UsernamePasswordAuthenticationFilter}
     * -> this.authFilter.setAuthenticationManager(http.getSharedObject(AuthenticationManager.class));
     * -> {@link AbstractAuthenticationProcessingFilter#doFilter(ServletRequest, ServletResponse, FilterChain)}
     * -> {@link UsernamePasswordAuthenticationFilter#attemptAuthentication(HttpServletRequest, HttpServletResponse)}
     * will use {@link AuthenticationConfiguration.AuthenticationManagerDelegator} -> {@link ProviderManager#authenticate(Authentication)}
     * and {@link AuthenticationConfiguration#initializeUserDetailsBeanManagerConfigurer(ApplicationContext)} and register {@link DaoAuthenticationProvider}
     * -> {@link DaoAuthenticationProvider#authenticate(Authentication)}
     * <p>
     * {@link AuthorizationFilter#doFilter(ServletRequest, ServletResponse, FilterChain)} 会检查是否有autherization
     * {@link DefaultLoginPageGeneratingFilter}
     */
    @Bean
    // @Order(2)
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http)
            throws Exception {
        // 获得 @PermitAll 带来的 URL 列表，免登录
        Multimap<HttpMethod, String> permitAllUrls = getPermitAllUrlsFromAnnotations();

        http
                .cors(Customizer.withDefaults()) //开启跨域
                .csrf(AbstractHttpConfigurer::disable) // CSRF 禁用，因为不使用 Session

                .sessionManagement(mng -> mng.sessionCreationPolicy(SessionCreationPolicy.NEVER))
                // 添加自定义filter
                .addFilterAfter(loginTokenAuthenticationFilter(), AnonymousAuthenticationFilter.class)
                // .oauth2ResourceServer(OAuth2ResourceServerConfigurer::opaqueToken) // {@see myIntrospector()}
                .oauth2ResourceServer(oauth2 ->
                        oauth2
                                //**对于resource app来讲，同一时间只能存在一种token兼容**
                                .opaqueToken(Customizer.withDefaults())
//                                .jwt(Customizer.withDefaults())// for id_token /userinfo endpoint. {@see https://docs.spring.io/spring-authorization-server/docs/current/reference/html/protocol-endpoints.html#oidc-client-registration-endpoint}

                        .authenticationEntryPoint(exceptionHandler())
                )

                // .oauth2ResourceServer(oauth2 ->
                //     oauth2.opaqueToken(opaqueToken ->
                //         opaqueToken.introspector(myIntrospector())
                //     )
                // )


                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers("/css/**", "/js/**", "/fonts/**", "/*.html", "/favicon.ico", "/*.css", "/*.js").permitAll()
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", "/actuator/**", "/webjars/**", "/resources/**", "/static/**").permitAll()
                        .requestMatchers(HttpMethod.GET, permitAllUrls.get(HttpMethod.GET).toArray(new String[0])).permitAll()
                        .requestMatchers(HttpMethod.POST, permitAllUrls.get(HttpMethod.POST).toArray(new String[0])).permitAll()
                        .requestMatchers(HttpMethod.PUT, permitAllUrls.get(HttpMethod.PUT).toArray(new String[0])).permitAll()
                        .requestMatchers(HttpMethod.DELETE, permitAllUrls.get(HttpMethod.DELETE).toArray(new String[0])).permitAll()
                        .requestMatchers(securityProperties.getPermitAllUrls().toArray(new String[0])).permitAll()
                        .anyRequest().authenticated()
                )



                // Form login handles the redirect to the login page from the
                // authorization server filter chain
                .exceptionHandling(eh ->
                        eh.accessDeniedHandler(exceptionHandler())
                                .authenticationEntryPoint(exceptionHandler())
                )
                // .formLogin(Customizer.withDefaults()) 默认配置,此处也是需要的，因为AuthorizationServerConfig重定向之后会来到这里
                .formLogin(form ->
                        form.loginPage("/oauth/index")
                                .loginProcessingUrl("/oauth/login")
                                .failureUrl("/oauth/index?error")
                                .defaultSuccessUrl("/user/info")
                                .permitAll()
                )
                .logout(LogoutConfigurer::permitAll)

                .httpBasic(Customizer.withDefaults());

        // {@see https://docs.spring.io/spring-security/reference/servlet/oauth2/client/authorization-grants.html#_requesting_an_access_token_2}
        // http.oauth2Client()
        return http.build();
    }

    @Bean
    public OpaqueTokenIntrospector defaultIntrospector() {
        return new NimbusOpaqueTokenIntrospector("http://localhost:48080/oauth2/introspect", "messaging-client", "secret");
    }

    private Multimap<HttpMethod, String> getPermitAllUrlsFromAnnotations() {
        Multimap<HttpMethod, String> result = HashMultimap.create();
        // 获得接口对应的 HandlerMethod 集合
        RequestMappingHandlerMapping requestMappingHandlerMapping = (RequestMappingHandlerMapping)
                applicationContext.getBean("requestMappingHandlerMapping");
        Map<RequestMappingInfo, HandlerMethod> handlerMethodMap = requestMappingHandlerMapping.getHandlerMethods();
        // 获得有 @PermitAll 注解的接口
        for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : handlerMethodMap.entrySet()) {
            HandlerMethod handlerMethod = entry.getValue();
            if (!handlerMethod.hasMethodAnnotation(PermitAll.class)) {
                continue;
            }
            if (entry.getKey().getPatternsCondition() == null && entry.getKey().getPathPatternsCondition() == null) {
                continue;
            }

            Set<String> urls1 = Optional.of(entry.getKey())
                    .map(RequestMappingInfo::getPatternsCondition)
                    .map(PatternsRequestCondition::getPatterns)
                    .orElse(Collections.emptySet());
            Set<String> urls2 = Optional.ofNullable(entry.getKey())
                    .map(RequestMappingInfo::getPathPatternsCondition)
                    .map(PathPatternsRequestCondition::getPatternValues)
                    .orElse(Collections.emptySet());
            Set<String> urls = Stream.of(urls1, urls2).flatMap(Set::stream)
                    .collect(Collectors.toSet());

            // 根据请求方法，添加到 result 结果
            entry.getKey().getMethodsCondition().getMethods().forEach(requestMethod -> {
                switch (requestMethod) {
                    case GET -> result.putAll(HttpMethod.GET, urls);
                    case POST -> result.putAll(HttpMethod.POST, urls);
                    case PUT -> result.putAll(HttpMethod.PUT, urls);
                    case DELETE -> result.putAll(HttpMethod.DELETE, urls);
                    default -> {}
                }
            });
        }
        return result;
    }

/*     @Bean
    public UserDetailsService userDetailsService(CreedAuthorityRepository authorityRepository,
                                                 CreedConsumerRepository consumerRepository,
                                                 CreedGroupsAuthoritiesRepository groupsAuthoritiesRepository,
                                                 CreedGroupsMembersRepository groupsMembersRepository,
                                                 CreedGroupsRepository groupsRepository,
                                                 CreedConsumerAuthorityRepository consumerAuthorityRepository,
                                                 AuthenticationManager authenticationManager) {
        CreedUserDetailsManager manager = new CreedUserDetailsManager(authorityRepository,
                consumerRepository,
                groupsAuthoritiesRepository,
                groupsMembersRepository,
                groupsRepository,
                consumerAuthorityRepository);
        manager.setAuthenticationManager(authenticationManager);
        return manager;
    } */

/*     @Bean
    public ProviderManager authManagerBean(AuthenticationProvider provider) {
        return new ProviderManager(provider);
    }

    @Bean
    public PasswordEncoder getPassWordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    } */

    /**
     * Spring Security 6.0 has become ↓↓
     *
     * @param authenticationConfiguration
     * @return
     * @throws Exception
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder getPassWordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(CreedAuthorityRepository authorityRepository,
                                                 CreedUserRepository consumerRepository,
                                                 CreedGroupsAuthoritiesRepository groupsAuthoritiesRepository,
                                                 CreedGroupsMembersRepository groupsMembersRepository,
                                                 CreedGroupsRepository groupsRepository) {
        // UserDetails userDetails = User.withDefaultPasswordEncoder()
        //         .username("user")
        //         .password("password")
        //         .roles("USER")
        //         .build();
        // return new InMemoryUserDetailsManager(userDetails);
        return new CreedUserDetailsManager(authorityRepository,
                consumerRepository,
                groupsAuthoritiesRepository,
                groupsMembersRepository,
                groupsRepository);
    }


    /**
     * 声明调用 {@link SecurityContextHolder#setStrategyName(String)} 方法，
     * 设置使用 {@link TransmittableThreadLocalSecurityContextHolderStrategy} 作为 Security 的上下文策略
     */
    @Bean
    public MethodInvokingFactoryBean securityContextHolderMethodInvokingFactoryBean() {
        MethodInvokingFactoryBean methodInvokingFactoryBean = new MethodInvokingFactoryBean();
        methodInvokingFactoryBean.setTargetClass(SecurityContextHolder.class);
        methodInvokingFactoryBean.setTargetMethod("setStrategyName");
        methodInvokingFactoryBean.setArguments(TransmittableThreadLocalSecurityContextHolderStrategy.class.getName());
        return methodInvokingFactoryBean;
    }
}
