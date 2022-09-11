package com.ethan.oauth2.resource.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @description: spring-creed
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 * @date: 7/27/2022 3:28 PM
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    public static final String DEMO_RESOURCE_ID = "order";

    /**
     * 重点就是配置了一个 UserDetailsService，和 ClientDetailsService 一样，
     * 为了方便运行，使用内存中的用户，实际项目中，
     * 一般使用的是数据库保存用户，具体的实现类可以使用 JdbcDaoImpl 或者 JdbcUserDetailsManager。
     * @return
     * @throws Exception
     */
    @Bean(name = BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        AuthenticationManager manager = super.authenticationManagerBean();
        return manager;
    }
    @Bean
    public static PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
            // 使用内存中的 InMemoryUserDetailsManager
            .inMemoryAuthentication()
            // 不使用 PasswordEncoder 密码编码器
            .passwordEncoder(passwordEncoder())
            // 配置 yunai 用户
            .withUser("yunai").password("{noop}1024").roles("USER");
    }

}
