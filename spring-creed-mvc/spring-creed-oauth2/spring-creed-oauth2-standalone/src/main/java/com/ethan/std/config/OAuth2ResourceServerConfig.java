package com.ethan.std.config;

import com.ethan.std.filter.SignAuthFilter;
import com.ethan.std.provider.UnAuthExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.CsrfTokenRepository;

import static com.ethan.std.config.SecurityConfig.REQUEST_MATCHER;

/**
 * @description: spring-creed
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 * @date: 8/1/2022 2:36 PM
 */
@Configuration
@EnableResourceServer
public class OAuth2ResourceServerConfig extends ResourceServerConfigurerAdapter {

    @Autowired
    private CsrfTokenRepository tokenRepository;
    @Autowired
    private UnAuthExceptionHandler exceptionHandler;
    // @Autowired
    // private SignAuthFilter signAuthFilter;
    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                // 设置 /api/ 开头的 URL 需要保护
                // .antMatchers("/oauth/check_token").permitAll() see {@link OAuth2AuthorizationServerConfig#configure()}
                .anyRequest().authenticated()
                .and().requestMatchers().antMatchers("/api/**")

                .and()
                .csrf()
                .ignoringAntMatchers("/sockjs/**")
                .csrfTokenRepository(tokenRepository)
        ;
        // TODO for testing
        // http.addFilterAt(signAuthFilter, CsrfFilter.class);
    }

    /**
     * 在AuthorizationServer为客户端client配置ResourceID的目的是：限制某个client可以访问的资源服务。
     * ResourceID当然是在Resource Server资源服务器进行验证（你能不能访问我的资源，当然由我自己来验证）。
     * 当资源请求发送到Resource Server的时候会携带access_token，Resource Server会根据access_token找到client_id，
     * 进而找到该client可以访问的resource_ids。如果resource_ids包含ResourceServer自己设置ResourceID，这关就过去了，就可以继续进行其他的权限验证。
     *
     * @param resources configurer for the resource server
     * @throws Exception
     */
    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources.resourceId("order")
                .authenticationEntryPoint(exceptionHandler)
                .accessDeniedHandler(exceptionHandler);
    }


}
