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