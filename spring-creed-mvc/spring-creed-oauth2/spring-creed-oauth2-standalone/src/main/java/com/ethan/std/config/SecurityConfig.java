package com.ethan.std.config;

import com.ethan.std.filter.SignAuthFilter;
import com.ethan.std.provider.CustomizeUserDetailsService;
import com.ethan.std.provider.UnAuthExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExceptionHandlingConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfiguration;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.context.NullSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.header.writers.ContentSecurityPolicyHeaderWriter;
import org.springframework.security.web.util.matcher.AndRequestMatcher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.sql.DataSource;

/**
 * @description: spring-creed
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 * @date: 8/1/2022 2:33 PM
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    public static final RequestMatcher REQUEST_MATCHER = new AndRequestMatcher(new NegatedRequestMatcher(new AntPathRequestMatcher("/oauth/**")),
            new NegatedRequestMatcher(new AntPathRequestMatcher("/sessions/**")));
    @Autowired
    private DataSource dataSource;

    private static final String[] AUTH_WHITELIST = {
            // -- swagger ui
            "/swagger-resources/**",
            "/swagger-ui.html",
            "/v2/api-docs",
            "/h2-console/**"
    };

    @Override
    @Bean(name = BeanIds.AUTHENTICATION_MANAGER)
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    @Bean(name = BeanIds.USER_DETAILS_SERVICE)
    public UserDetailsService userDetailsServiceBean() throws Exception {
        return new CustomizeUserDetailsService();
    }

    @Bean // default is PasswordEncoderFactories.createDelegatingPasswordEncoder()
    public static PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public CsrfTokenRepository tokenRepository() {
        return CookieCsrfTokenRepository.withHttpOnlyFalse();
        //return new HttpSessionCsrfTokenRepository();
    }
    
    // @Bean
    // public SignAuthFilter signAuthFilter() {
    //     SignAuthFilter signAuthFilter = new SignAuthFilter();
    //     signAuthFilter.setAccessDeniedHandler(exceptionHandler());
    //     return signAuthFilter;
    // }

    @Bean
    public UnAuthExceptionHandler exceptionHandler() {
        return new UnAuthExceptionHandler();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // auth.
        //         // 使用内存中的 InMemoryUserDetailsManager
        //                 inMemoryAuthentication()
        //         // 不使用 PasswordEncoder 密码编码器
        //         .passwordEncoder(passwordEncoder())
        //         // 配置 yunai 用户
        //         .withUser("yunai").password("{noop}1024").roles("USER");
        auth
            .userDetailsService(userDetailsServiceBean())
        // .and().authenticationProvider();
        // auth.jdbcAuthentication()
        //         .dataSource(dataSource);
        ;
    }
       @Override
       protected void configure(HttpSecurity http) throws Exception {


           // http.authorizeRequests()
           //         // 对所有 URL 都进行认证
           //         .anyRequest()
           //         .authenticated();
           http
                   // .httpBasic()
                   //      .authenticationEntryPoint(exceptionHandler())
                   // .and()
                   .authorizeRequests()
                   // .mvcMatchers("/token/demo/revoke").permitAll()
                   .antMatchers("/auth/grant", "/oauth/index", "/oauth/login", "/login", "/external/**").permitAll()
                   .antMatchers("/**.js", "/**.css").permitAll()
                   .antMatchers("/static/**").permitAll()
                   .antMatchers(AUTH_WHITELIST).permitAll()
                   .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                   .anyRequest().authenticated()

                   /**
                    * <url>https://www.baeldung.com/spring-security-session?fireglass_rsn=true</url>
                    * By default, Spring Security will create a session when it needs one — this is “ifRequired“.
                    * For a more stateless application, the “never” option will ensure that Spring Security itself won't create any session. But if the application creates one, Spring Security will make use of it.
                    * Finally, the strictest session creation option, “stateless“, is a guarantee that the application won't create any session at all.
                    *
                    * This stateless architecture plays well with REST APIs and their Statelessness constraint. They also work well with authentication mechanisms such as Basic and Digest Authentication.
                    *
                    * Before running the Authentication process, Spring Security will run a filter responsible for storing the Security Context between requests. This is the {@link SecurityContextPersistenceFilter}.
                    * The context will be stored according to the strategy HttpSessionSecurityContextRepository by default, which uses the HTTP Session as storage.
                    * For the strict create-session=”stateless” attribute, this strategy will be replaced with another — {@link NullSecurityContextRepository} — and no session will be created or used to keep the context.
                    *
                    *  *Concurrent Session Control*
                    *  When a user that is already authenticated tries to authenticate again, the application can deal with that event in one of a few ways.
                    *  It can either invalidate the active session of the user and authenticate the user again with a new session, or allow both sessions to exist concurrently.
                    *
                    *  {@link HttpSessionEventPublisher }
                    *  @Bean
                    * public HttpSessionEventPublisher httpSessionEventPublisher() {
                    *     return new HttpSessionEventPublisher();
                    * }
                    *
                    * This is essential to make sure that the Spring Security session registry is notified when the session is destroyed.
                    *
                    * We can easily configure the Session timeout value of the embedded server using properties:
                    * server.servlet.session.timeout=15m
                    */
                   .and()
                   .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)

                   .and()
                   // 需要添加 formLogin。 否则只会有 {@link Http403ForbiddenEntryPoint} 而不是 {@link DelegatingAuthenticationEntryPoint},
                   // 这样会导致 oauth/** api一直处于access denied 情况
                   // login源码页面参考 {@link org.springframework.security.web.authentication.ui.DefaultLoginPageGeneratingFilter}
                   .formLogin()
                        .loginPage("/oauth/index") // 登陆 URL 地址
                        .loginProcessingUrl("/oauth/login")
                        .failureUrl("/login?error")
                        .defaultSuccessUrl("/user/info")
                        .permitAll()
                   .and()
                   .logout().permitAll()

                   .and()
                        .csrf()
                        .ignoringAntMatchers("/sockjs/**")
                        .csrfTokenRepository(tokenRepository())

                   .and()
                       .headers()
                       .xssProtection()
                       .and()
                       .contentSecurityPolicy("script-src 'self'")
                   .and()

             /**
              * !!!设置默认值是为了不被form中的 LoginUrlAuthenticationEntryPoint覆盖
              * !!!注意 与spring security & form 结合的时候，
              * 为了只允许 oauth相关的API能够跳转登录URL. 其他的URL应该交由authResourceServer去进行验证与访问的控制。
              * 需要进行以下配置。
              * {@link ResourceServerConfiguration#configure(HttpSecurity)}
              * {@link ExceptionHandlingConfigurer#createDefaultEntryPoint(HttpSecurityBuilder)}
              * 意思是除了/oauth/**以外的API 不在进入form的重定向，而是被自定义的AuthenticationEntryPoint替代
              *
              * 否则会进入LoginUrlAuthenticationEntryPoint 跳转登录页面
              * {@link LoginUrlAuthenticationEntryPoint}
              *
              * 这不是我们所期望的。
              *
              * */
           .and().exceptionHandling()
                   .accessDeniedHandler(exceptionHandler())
                   .authenticationEntryPoint(exceptionHandler())
                 .defaultAuthenticationEntryPointFor(exceptionHandler(), REQUEST_MATCHER)
                 .defaultAccessDeniedHandlerFor(exceptionHandler(), REQUEST_MATCHER)


           ;

            // TODO for testing
           // http.addFilterAt(signAuthFilter(), CsrfFilter.class);
       }
}
