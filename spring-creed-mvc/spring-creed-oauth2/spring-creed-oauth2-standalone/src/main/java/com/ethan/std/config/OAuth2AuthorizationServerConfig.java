package com.ethan.std.config;

import com.ethan.std.provider.CustomizeAuthenticationKeyGenerator;
import com.ethan.std.provider.CustomizeClientDetailsService;
import com.ethan.std.provider.CustomizeTokenServices;
import com.ethan.std.provider.CustomizeTokenStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.approval.ApprovalStore;
import org.springframework.security.oauth2.provider.approval.JdbcApprovalStore;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.JdbcAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;

import javax.sql.DataSource;

/**
 * @description: spring-creed
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 * @date: 8/1/2022 2:35 PM
 */
@Configuration
@EnableAuthorizationServer
public class OAuth2AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserDetailsService service;
    @Autowired
    private DataSource dataSource;

    @Bean
    public TokenStore tokenStore() {
        CustomizeTokenStore tokenStore = new CustomizeTokenStore();
        // tokenStore.setAuthenticationKeyGenerator(new CustomizeAuthenticationKeyGenerator());
        return tokenStore;
        // return new JdbcTokenStore(dataSource);
    }
    @Bean
    public AuthorizationCodeServices authorizationCodeServices() {
        return new JdbcAuthorizationCodeServices(dataSource);
    }
    @Bean
    public ApprovalStore approvalStore() {
        return new JdbcApprovalStore(dataSource);
    }

    @Bean
    public ClientDetailsService clientDetailsService() {
        return new CustomizeClientDetailsService();
    }
    @Bean
    public AuthorizationServerTokenServices tokenServices() {
        CustomizeTokenServices tokenServices = new CustomizeTokenServices();
        tokenServices.setTokenStore(tokenStore());
        tokenServices.setAlwaysCreateToken(true);// always create new token
        tokenServices.setSupportRefreshToken(true);
        tokenServices.setReuseRefreshToken(false);
        tokenServices.setClientDetailsService(clientDetailsService());
        // addUserDetailsService(tokenServices, this.userDetailsService);
        return tokenServices;

    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.authenticationManager(authenticationManager)
                .tokenStore(tokenStore())
                // .tokenEnhancer(tokenEnhancer())
                .tokenServices(tokenServices())
                .authorizationCodeServices(authorizationCodeServices())
                .approvalStore(approvalStore())
        ;
    }
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        // clients.inMemory()
        //         .withClient("client_1").secret("{noop}112233") // Client 账号、密码。
        //         .authorizedGrantTypes("password", "refresh_token") // 密码模式
        //         .scopes("read_userinfo", "read_contacts") // 可授权的 Scope
        //         .accessTokenValiditySeconds(3600) //3600s = 2 hrs
        //         .refreshTokenValiditySeconds(864000)//864000s = 10 days
        // //                .and().withClient() // 可以继续配置新的 Client
        //         .and()
        //         .withClient("client_2").secret("{noop}112233") // Client 账号、密码。
        //         .authorizedGrantTypes("authorization_code", "refresh_token") // 授权码模式
        //         .redirectUris("http://localhost:8090/callback") // 配置回调地址，选填。
        //         .scopes("read_userinfo", "read_contacts") // 可授权的 Scope
        // //                .and().withClient() // 可以继续配置新的 Client
        // ;

        // http://localhost:8080/oauth/authorize?client_id=clientapp&redirect_uri=http://localhost:8090/callback&response_type=code&scope=read_userinfo
        // http://localhost:8080/oauth/authorize?client_id=clientapp&redirect_uri=http://localhost:8090/callback&response_type=token&scope=read_userinfo




        clients
                .withClientDetails(clientDetailsService());
    }
}
