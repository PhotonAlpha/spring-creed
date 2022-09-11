package com.ethan.std.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

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
    @Autowired
    private DataSource dataSource;

    @Override
    @Bean(name = BeanIds.AUTHENTICATION_MANAGER)
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    @Bean(name = BeanIds.USER_DETAILS_SERVICE)
    public UserDetailsService userDetailsServiceBean() throws Exception {
        return super.userDetailsServiceBean();
    }

    /* @Bean // default is PasswordEncoderFactories.createDelegatingPasswordEncoder()
    public static PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    } */

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // auth.
        //         // 使用内存中的 InMemoryUserDetailsManager
        //                 inMemoryAuthentication()
        //         // 不使用 PasswordEncoder 密码编码器
        //         .passwordEncoder(passwordEncoder())
        //         // 配置 yunai 用户
        //         .withUser("yunai").password("{noop}1024").roles("USER");
        auth.userDetailsService()
        auth.jdbcAuthentication()
                .dataSource(dataSource);
    }
       @Override
       protected void configure(HttpSecurity http) throws Exception {
           // http.authorizeRequests()
           //         // 对所有 URL 都进行认证
           //         .anyRequest()
           //         .authenticated();
           http.csrf().disable()
                   .authorizeRequests()
                   .mvcMatchers("/token/demo/revoke").permitAll()
                   .anyRequest().authenticated()
           ;
       }
}
