# OAUTH2 认证服务端与资源服务整合

配置入口`org.springframework.security.oauth2.provider.endpoint.AuthorizationEndpoint`

1. /oauth/token
获取token请求
    `org.springframework.security.oauth2.provider.endpoint.CheckTokenEndpoint`

2. /oauth/authorize
获取authorization code请求
   `org.springframework.security.oauth2.provider.endpoint.AuthorizationEndpoint`

3. /oauth/check_token
检验token请求
    `org.springframework.security.oauth2.provider.endpoint.CheckTokenEndpoint`

# 核心拦截器 org.springframework.security.web.access.intercept.FilterSecurityInterceptor

- org.springframework.security.oauth2.provider.client.JdbcClientDetailsService
- org.springframework.security.oauth2.provider.token.store.JdbcTokenStore
- org.springframework.security.oauth2.provider.endpoint.TokenEndpoint
- org.springframework.security.oauth2.provider.token.DefaultTokenServices
- -------↑token store↑------------
- org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerEndpointsConfiguration
- org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerSecurityConfiguration
- org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer
- org.springframework.security.web.authentication.DelegatingAuthenticationEntryPoint
- org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint

