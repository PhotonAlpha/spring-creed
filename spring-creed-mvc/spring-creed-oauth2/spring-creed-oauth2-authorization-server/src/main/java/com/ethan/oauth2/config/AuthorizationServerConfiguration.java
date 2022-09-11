package com.ethan.oauth2.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;

/**
 * @description: spring-creed
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 * @date: 7/27/2022 3:35 PM
 */
@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    RedisConnectionFactory redisConnectionFactory;

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        // 允许表单认证
        // security.allowFormAuthenticationForClients();
        security.checkTokenAccess("isAuthenticated()");
        // security.tokenKeyAccess("permitAll()").checkTokenAccess("permitAll()");
        //security.tokenKeyAccess("hasAuthority('ROLE_TRUSTED_CLIENT')").checkTokenAccess("hasAuthority('ROLE_TRUSTED_CLIENT')");
        security.allowFormAuthenticationForClients();
        // FilterSecurityInterceptor
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory() // <4.1>
                .withClient("clientapp").secret("{noop}112233") // <4.2> Client 账号、密码。
                .authorizedGrantTypes("password") // <4.2> 密码模式
                .scopes("read_userinfo", "read_contacts") // <4.2> 可授权的 Scope
                //                .and().withClient() // <4.3> 可以继续配置新的 Client
                .and().withClient("client_2").secret("{noop}112233")
                .resourceIds(SecurityConfig.DEMO_RESOURCE_ID)
                .authorizedGrantTypes("authorization_code", "refresh_token")
                .redirectUris("http://localhost:8090/callback")
                .scopes("read_userinfo", "read_contacts")
                .authorities("client")
                .and().withClient("client_3").secret("{noop}112233")
                .resourceIds(SecurityConfig.DEMO_RESOURCE_ID)
                .authorizedGrantTypes("implicit", "refresh_token")
                .redirectUris("http://localhost:8090/callback2")
                .scopes("read_userinfo", "read_contacts")
                .authorities("client")
                .and().withClient("client_4").secret("{noop}112233")
                .resourceIds(SecurityConfig.DEMO_RESOURCE_ID)
                .authorizedGrantTypes("client_credentials", "refresh_token")
                .scopes("read_userinfo", "read_contacts")
                .authorities("client")
        // http://localhost:8080/oauth/authorize?client_id=client_2&redirect_uri=http://localhost:8090/callback&response_type=code&scope=read_userinfo
        // http://localhost:8080/oauth/authorize?client_id=client_3&redirect_uri=http://localhost:8090/callback2&response_type=token&scope=read_userinfo
        ;
        // 配置两个客户端, 一个用于 password 认证一个用于 client 认证
        // clients.inMemory().withClient("client_1")
        //         .resourceIds(SecurityConfig.DEMO_RESOURCE_ID)
        //         .authorizedGrantTypes("client_credentials", "refresh_token")
        //         .scopes("select")
        //         .authorities("client")
        //         .secret("123456")
        //         .and().withClient("client_2")
        //         .resourceIds(SecurityConfig.DEMO_RESOURCE_ID)
        //         .authorizedGrantTypes("password", "refresh_token")
        //         .scopes("select")
        //         .authorities("client")
        //         .secret("123456");
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints
                // .tokenStore(new RedisTokenStore(redisConnectionFactory))
                .authenticationManager(authenticationManager);
    }
}
