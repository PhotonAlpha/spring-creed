package com.creed.security.config;

import com.creed.security.service.AccountServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AnyRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

@Configuration
@EnableWebSecurity
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    //@ConditionalOnMissingBean
    public UserDetailsService userDetailsService() {
        return new AccountServiceImpl();
    }

    //@Bean
    public AuthenticationProvider authenticationProvider() {
        return new CustomAuthenticationProvider(userDetailsService());
    }

    @Bean(name = BeanIds.AUTHENTICATION_MANAGER)
    @Override
    protected AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }

    //@Bean
    public JwtAuthenticationTokenFilter passwordAuthenticationFilter() {
        return new JwtAuthenticationTokenFilter();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            /**
             * {@link org.springframework.security.web.session.SessionManagementFilter}
             * http://blog.didispace.com/tags/Spring-Session/
             * tomcat 通过 JSESSIONID 来识别请求， 因此可以不用免登陆
             */
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)

            .and()
            .requestMatchers()
                .antMatchers("/login")

            .and()
            .formLogin()
            .and().logout().permitAll()

            .and().requestMatchers()
            .antMatchers("/rest1/**")
            .and()
            .authorizeRequests()
            .antMatchers("/rest1/v1/test/hello").permitAll()
            .antMatchers("/rest1/v1/test/**").denyAll()

            .and()
            .requestMatchers()
            .antMatchers("/rest2/**")
            .and()
            .authorizeRequests()
            .antMatchers("/rest2/v1/test/hello").denyAll()
            .antMatchers("/rest2/v1/test/**").denyAll()



            /*
            From the spring-security documentation:
            isAuthenticated()       Returns true if the user is not anonymous
            isFullyAuthenticated()  Returns true if the user is not an anonymous or a remember-me user*/
            .and()
            .requestMatcher(AnyRequestMatcher.INSTANCE)
            .authorizeRequests().anyRequest().authenticated()
        ;


            //    .permitAll()
            //    .antMatchers("/user/**").hasAnyRole("USER") // 需要具有ROLE_USER角色才能访问
            //    .antMatchers("/admin/**").hasAnyRole("ADMIN") // 需要具有ROLE_ADMIN角色才能访问
            //    .anyRequest().authenticated()
            //    .and()
            //        .formLogin()
            //        .loginPage("/authentication/login") // 设置登录页面
            //        .loginProcessingUrl("/authentication/form")
            //        .defaultSuccessUrl("/user/info", true)// 设置默认登录成功后跳转的页面
            //    .and()
            //.logout().logoutUrl("/logout")
            //.permitAll()
            //.and()
            //.httpBasic().disable()

        /**
         * example
         * https://stackoverflow.com/questions/52029258/understanding-requestmatchers-on-spring-security
         */

        //http.requestMatchers()
        //    .antMatchers("/rest1/**")
        //    .and()
        //    .authorizeRequests()
        //    .antMatchers("/rest1/v1/test/hello").permitAll()
        //    .antMatchers("/rest1/v1/test/**").denyAll()
        //    .and()
        //    .requestMatchers()
        //    .antMatchers("/rest2/**")
        //    .and()
        //    .authorizeRequests()
        //    .antMatchers("/rest2/v1/test/hello").denyAll()
        //    .antMatchers("/rest2/v1/test/**").denyAll();
        //
        //
        //http
        //
        //    .requestMatchers()
        //    // /oauth/authorize link org.springframework.security.oauth2.provider.endpoint.AuthorizationEndpoint
        //    // 必须登录过的用户才可以进行 oauth2 的授权码申请
        //    .antMatchers("/home", "/login", "**/*.css", "**/*.ico")
        //    .and()
        //    .authorizeRequests()
        //    .anyRequest().permitAll()
        //    .and()
        //
        //    .requestMatchers().anyRequest().and()
        //    .authorizeRequests().anyRequest().authenticated().and()
        //
        //
        //    .formLogin().permitAll()
        //    .and().logout().permitAll();
        //http.addFilterBefore(passwordAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
    }

    // 密码加密方式
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
    // 重写方法，自定义用户
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        //auth.inMemoryAuthentication().withUser("admin").password(new BCryptPasswordEncoder().encode("admin")).roles("ADMIN","USER");
        //auth.inMemoryAuthentication().withUser("normal").password(new BCryptPasswordEncoder().encode("normal")).roles("USER");
        auth.authenticationProvider(authenticationProvider());
    }
}
