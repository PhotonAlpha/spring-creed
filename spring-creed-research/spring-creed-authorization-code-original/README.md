org.springframework.security.authentication.dao.DaoAuthenticationProvider
会把@EnableAuthorizationServer 与 spring security

client模式是不存在“用户”的概念的，那么这里的身份认证是在认证什么呢？debug可以发现UserDetailsService的实现被适配成了ClientDetailsUserDetailsService，这个设计是将client客户端的信息（client_id,client_secret）适配成用户的信息(username,password)，这样我们的认证流程就不需要修改了。

# spring security 加载 userDetailsService 从 authenticationManager -> AuthenticationConfiguration [authBuilder.build()]
 -> InitializeUserDetailsManagerConfigurer [configure(AuthenticationManagerBuilder auth)] -> DaoAuthenticationProvider [setUserDetailsService]
 
`@EnableAuthorizationServer` 则会默认加载 ClientDetailsUserDetailsService, 由 void configure(ClientDetailsServiceConfigurer clients) 配置.
 
参考： http://www.iocoder.cn/Spring-Security/laoxu/OAuth2-2/
源码分析： http://www.iocoder.cn/Spring-Security/laoxu/OAuth2-3/





http://localhost:8080/oauth/authorize?client_id=client_auth&redirect_uri=http://localhost:8080/product/1&response_type=code&scope=select