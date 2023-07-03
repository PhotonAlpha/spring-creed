# Filter Chain and Proxy

spring 默认

# Oauth2 Filter List

- ## DisableEncodeUrlFilter

- ## WebAsyncManagerIntegrationFilter

- ## SecurityContextHolderFilter

- ## AuthorizationServerContextFilter

- ## HeaderWriterFilter

- ## CsrfFilter

- ## LogoutFilter

- ## OAuth2AuthorizationServerMetadataEndpointFilter

- ## OAuth2AuthorizationEndpointFilter

    核心方法 `doFilterInternal(req, resp, chain)`

  1. authenticationConverter.convert(req) 根据请求返回 `Authentication`
    convert list是：

        - org.springframework.security.oauth2.server.authorization.web.authentication.OAuth2AuthorizationCodeRequestAuthenticationConverter

        codeChallenge 用于 PKCE 方式认证
        codeChallengeMethod 可选 PKCE 方式认证

        最终生成 **`OAuth2AuthorizationCodeRequestAuthenticationToken`**，包含信息有：

            - authorizationUri <http://localhost:8081/oauth2/authorize>
            - clientId <messaging-client>
            - principal <new AnonymousAuthenticationToken("anonymous", "anonymousUser", AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS"))>
            - redirectUri <http://127.0.0.1:8080/authorized>
            - state <null>
            - scopes <message.read, message.write>
            - additionalParameters <null> e.g. codeChallenge codeChallengeMethod

        - org.springframework.security.oauth2.server.authorization.web.authentication.OAuth2AuthorizationConsentAuthenticationConverter

`http://localhost:8081/oauth2/authorize?client_id=messaging-client&redirect_uri=http://127.0.0.1:8080/authorized&response_type=code&scope=message.read message.write` code未认证会先进入`OAuth2AuthorizationCodeRequestAuthenticationConverter`，后续callback会进入`OAuth2AuthorizationConsentAuthenticationConverter`去生成token

  2. authenticationManager.authenticate(authentication) 根据1，认证并返回认证过的`Authentication authenticationResult`

  authentication provider list有：

    - org.springframework.security.authentication.AnonymousAuthenticationProvider
    
    - org.springframework.security.oauth2.server.authorization.authentication.JwtClientAssertionAuthenticationProvider
    
    - org.springframework.security.oauth2.server.authorization.authentication.ClientSecretAuthenticationProvider
    
    - org.springframework.security.oauth2.server.authorization.authentication.PublicClientAuthenticationProvider
    
    - **org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationCodeRequestAuthenticationProvider**

    第一次进入这里，

    code认证方式：
    1. 会检查是否是 `AnonymousAuthenticationToken`，或者是否被认证，
    如果没有，则直接返回，不做后续处理。
    2. authenticationResult.isAuthenticated()
        如果认证成功，会调用onAuthenticationSuccess，重定向redirectUri，并且返回state code. 不再进入 filterChain.doFilter(request, response).
        如果未认证，进入下一级filter
    3. 如果配置了需要consent页面，则会进入consent页面
    
    ```java
        if (authenticationResult instanceof OAuth2AuthorizationConsentAuthenticationToken) {
            if (this.logger.isTraceEnabled()) {
            this.logger.trace("Authorization consent is required");
            }
            sendAuthorizationConsent(request, response,
            (OAuth2AuthorizationCodeRequestAuthenticationToken) authentication,
            (OAuth2AuthorizationConsentAuthenticationToken) authenticationResult);
            return;
        }
    ```
    4. 

    
    - **org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationConsentAuthenticationProvider**


    
    - org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationCodeAuthenticationProvider
    
    - org.springframework.security.oauth2.server.authorization.authentication.OAuth2RefreshTokenAuthenticationProvider
    
    - org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientCredentialsAuthenticationProvider
    
    - org.springframework.security.oauth2.server.authorization.authentication.OAuth2TokenIntrospectionAuthenticationProvider
    
    - org.springframework.security.oauth2.server.authorization.authentication.OAuth2TokenRevocationAuthenticationProvider
    
    - org.springframework.security.oauth2.server.authorization.oidc.authentication.OidcUserInfoAuthenticationProvider

- ## OidcProviderConfigurationEndpointFilter

- ## NimbusJwkSetEndpointFilter

- ## OAuth2ClientAuthenticationFilter

- ## BearerTokenAuthenticationFilter （重要）
    添加此Filter需要此配置
    AuthorizationServerConfig#authorizationServerSecurityFilterChain#**`http.oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt)`**;
    
    如果在 AuthorizationServer 和 Spring Security都需要添加此Filter,则两边都需要配置。

- ## RequestCacheAwareFilter

- ## SecurityContextHolderAwareRequestFilter

- ## AnonymousAuthenticationFilter

    重新设置 Authentication anonymous = createAuthentication(request)

- ## LoginTokenAuthenticationFilter （自定义）

    如果认证成功， 设置全局用户信息

- ## ExceptionTranslationFilter

    spring security 默认的异常handler 是 `org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint`, 默认端口为 /login

- ## AuthorizationFilter （重要）

    这个filter决定了认证成功或者失败之后的策略，通过 authorizationManager.check() 是否access deniny当前请求

    步骤为：
    1. AuthorizationDecision decision = this.authorizationManager.check(this::getAuthentication, request);

    RequestMatcherDelegatingAuthorizationManager -> org.springframework.security.authorization.AuthenticatedAuthorizationManager -> this.trustResolver.isAnonymous(authentication) 检查是否是Anonymous,如果是则认为 isGranted=fase

    2. throw new AccessDeniedException("Access Denied");
    3. 异常会进入 `ExceptionTranslationFilter`

- ## OAuth2TokenEndpointFilter

- ## OAuth2TokenIntrospectionEndpointFilter

- ## OAuth2TokenRevocationEndpointFilter

- ## OidcUserInfoEndpointFilter

# 非 Oauth2 Filter List

- ## org.springframework.security.web.session.DisableEncodeUrlFilter

- ## org.springframework.security.web.context.request.async.WebAsyncManagerIntegrationFilter

- ## org.springframework.security.web.context.SecurityContextHolderFilter

- ## org.springframework.security.web.header.HeaderWriterFilter

- ## org.springframework.web.filter.CorsFilter

- ## org.springframework.security.web.authentication.logout.LogoutFilter

- ## org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

- ## org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter

- ## org.springframework.security.web.authentication.www.BasicAuthenticationFilter

- ## org.springframework.security.web.savedrequest.RequestCacheAwareFilter

- ## org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestFilter

- ## org.springframework.security.web.authentication.AnonymousAuthenticationFilter

- ## com.ethan.security.websecurity.filter.LoginTokenAuthenticationFilter

- ## org.springframework.security.web.session.SessionManagementFilter

- ## org.springframework.security.web.access.ExceptionTranslationFilter

- ## org.springframework.security.web.access.intercept.AuthorizationFilter

# 具体案例之code认证

`http://localhost:8081/oauth2/authorize?client_id=messaging-client&redirect_uri=http://127.0.0.1:8080/authorized&response_type=code&scope=message.read message.write` 以此为例：

1. 进入 `OAuth2AuthorizationEndpointFilter`，
    a. Authentication authentication = this.authenticationConverter.convert(request);
    b. Authentication authenticationResult = this.authenticationManager.authenticate(authentication);
    c. authenticationResult.isAuthenticated(),检查是否是 Anonymous认证
    d. Anonymous认证,进入下一个Filter  `AuthorizationFilter#this.authorizationManager.check`然后`throw AccessDeniedException`
    e. 根据 `AuthorizationServerConfig#exceptionHandling#LoginUrlAuthenticationEntryPoint`重定向，然后进入`SecurityConfig.java#formLogin#loginPage`开始登陆认证
    f. 通过 POST`UsernamePasswordAuthenticationFilter`，`this.getAuthenticationManager().authenticate(authRequest)`开始用户认证，如果成功，重新进入 `http://localhost:8081/oauth2/authorize?client_id=messaging-client&redirect_uri=http://127.0.0.1:8080/authorized&response_type=code&scope=message.read message.write`
    g. 重新进入 b. ， Authentication authenticationResult = this.authenticationManager.authenticate(authentication); 调用 `OAuth2AuthorizationCodeRequestAuthenticationProvider#requireAuthorizationConsent(registeredClient, authorizationRequest, currentAuthorizationConsent)`，如果未确认，返回`OAuth2AuthorizationConsentAuthenticationToken`。
    h. 此时c.已经设置了用户认证，进入下一步`if (authenticationResult instanceof OAuth2AuthorizationConsentAuthenticationToken)#sendAuthorizationConsent`，如果已经设置了确认页面，进入确认页面，如果未设置，则返回默认 `displayConsent`
    i. 提交确认请求，`http://localhost:8081/oauth2/authorize?client_id=messaging-client&state=U1Y5WD879Q-2fLyFZXXNT6Bf1sIYqSeylUr_wjeIP_E%3D&scope=message.read&scope=message.write`，插入consent表，重新进入b. c.,返回 `OAuth2AuthorizationCodeRequestAuthenticationToken`.
    j. 重新进入 b. ， Authentication authenticationResult = this.authenticationManager.authenticate(authentication); 调用 `OAuth2AuthorizationCodeRequestAuthenticationProvider#createAuthorizationCodeTokenContext`，返回`OAuth2AuthorizationCodeRequestAuthenticationToken`。
    k. 最后调用`OAuth2AuthorizationEndpointFilter#authenticationSuccessHandler.onAuthenticationSuccess`,返回code

下面根据code生成TOKEN
`
curl --location 'http://localhost:8081/oauth2/token' \
--header 'Content-Type: application/x-www-form-urlencoded' \
--header 'Authorization: Basic bWVzc2FnaW5nLWNsaWVudDpzZWNyZXQ=' \
--data-urlencode 'grant_type=authorization_code' \
--data-urlencode 'code=ZUacHzsIU3mSfThKPr6jm6kVH7hIp794V08EmjxhxXEBcwfG5gRdW8enXfqjBs4nx_eV-zj-F1lA9uNVwLBekQyj6tIGioWgDi5shJJXhAsl_Z7TRp3CtShSxry6P1m9' \
--data-urlencode 'redirect_uri=http://127.0.0.1:8080/authorized'
`

2. 因为Filter优先级的原因，会优先进入 `OAuth2ClientAuthenticationFilter`
    - org.springframework.security.authentication.AnonymousAuthenticationProvider
    - org.springframework.security.oauth2.server.authorization.authentication.JwtClientAssertionAuthenticationProvider
    - org.springframework.security.oauth2.server.authorization.authentication.ClientSecretAuthenticationProvider
        **重要 PKCE验证在这里**
        `this.codeVerifierAuthenticator.authenticateIfAvailable(clientAuthentication, registeredClient);`

        然后 查询并设置 **`OAuth2ClientAuthenticationToken`**

    - org.springframework.security.oauth2.server.authorization.authentication.PublicClientAuthenticationProvider
    - org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationCodeRequestAuthenticationProvider
    - org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationConsentAuthenticationProvider
    - org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationCodeAuthenticationProvider
    - org.springframework.security.oauth2.server.authorization.authentication.OAuth2RefreshTokenAuthenticationProvider
    - org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientCredentialsAuthenticationProvider
    - org.springframework.security.oauth2.server.authorization.authentication.OAuth2TokenIntrospectionAuthenticationProvider
    - org.springframework.security.oauth2.server.authorization.authentication.OAuth2TokenRevocationAuthenticationProvider
    - org.springframework.security.oauth2.server.authorization.oidc.authentication.OidcUserInfoAuthenticationProvider

3. 最后进入`OAuth2TokenEndpointFilter`

    a. `OAuth2AuthorizationCodeAuthenticationProvider`
    b. `this.authenticationSuccessHandler.onAuthenticationSuccess`

# 具体案例之PKCE认证

`GET http://localhost:8081/oauth2/authorize?response_type=code&client_id=messaging-client&state=abc&scope=message.read message.write&redirect_uri=http://127.0.0.1:8080/authorized&code_challenge=JduScgSvQ72QK57pxQRP4sKToCGFdBxOnUbQ7mH20zw&code_challenge_method=S256`

```
curl --location 'http://localhost:8081/oauth2/token' \
--header 'Content-Type: application/x-www-form-urlencoded' \
--data-urlencode 'grant_type= authorization_code' \
--data-urlencode 'code= QrPTGlofdt889VxEWd2wJftY9i0GgFvq4HumWeOlBOJdH8GdYhgMrGXMdWlwhVNfs8_KyV-RB5lhemFuWUM4TSVH5bh0kSowQLVZ0fZM71YG1MBV4Oq_O0dmtNWSABNS' \
--data-urlencode 'redirect_uri= http://127.0.0.1:8080/authorized' \
--data-urlencode 'code_verifier= TSRfNEc_W_YHX6cf1EPRocIiKNZKEwVbhy4QWMbsGKc'
```

区别在于多了 `code_challenge` 和 `code_verifier`. 验证步骤在 `ClientSecretAuthenticationProvider`.

- `code_verifier`: A random, 43-128 character string used to connect the authorization request to the token request. Uses the following characters: [A-Z] / [a-z] / (0-9] / "-" /"."/"_"/"~".
- `code_challenge`: code_verifier的SHA-256加密，Base64的urlSafe=true的字符串

例如 code_verifier:diRBt-ij9bIOPIpNmH5x5JcsYa_JTh.YV.dIBwM4QMr
    codeChallenge:94zdo2nyLyQP6TNkr_gmd1g7MhKQGOfzbG5eqiCmSJc

# OAuth 2.0 Authorization Server Metadata

`http://localhost:8081/.well-known/oauth-authorization-server`

```json
{"issuer":"http://localhost:8081","authorization_endpoint":"http://localhost:8081/oauth2/authorize","token_endpoint":"http://localhost:8081/oauth2/token","token_endpoint_auth_methods_supported":["client_secret_basic","client_secret_post","client_secret_jwt","private_key_jwt"],"jwks_uri":"http://localhost:8081/oauth2/jwks","response_types_supported":["code"],"grant_types_supported":["authorization_code","client_credentials","refresh_token"],"revocation_endpoint":"http://localhost:8081/oauth2/revoke","revocation_endpoint_auth_methods_supported":["client_secret_basic","client_secret_post","client_secret_jwt","private_key_jwt"],"introspection_endpoint":"http://localhost:8081/oauth2/introspect","introspection_endpoint_auth_methods_supported":["client_secret_basic","client_secret_post","client_secret_jwt","private_key_jwt"],"code_challenge_methods_supported":["S256"]}
```

OAuth 2.0 Authorization Server Metadata requests是指通过发送特定的HTTP请求获取OAuth 2.0授权服务器的元数据。这些元数据提供了关于授权服务器的信息，如其支持的授权流程、支持的令牌类型、支持的加密算法等。

在OAuth 2.0中，授权服务器可以通过提供元数据来简化客户端的配置和与授权服务器的交互。元数据是以JSON格式表示的一组属性，可以包含以下信息：

1. "issuer"（发布者）：授权服务器的唯一标识符。
2. "authorization_endpoint"（授权端点）：用于进行用户认证和授权的端点。
3. "token_endpoint"（令牌端点）：用于获取访问令牌的端点。
4. "revocation_endpoint"（撤销端点）：用于撤销访问令牌的端点。
5. "introspection_endpoint"（内省端点）：用于检查访问令牌的有效性和相关属性的端点。
6. "jwks_uri"（JWK Set URI）：包含公钥的JSON Web Key Set（JWKS）的URI，用于验证访问令牌的签名。
7. "response_types_supported"（支持的响应类型）：支持的授权流程的类型，如授权码流程、隐式流程等。
8. "token_endpoint_auth_methods_supported"（支持的令牌端点身份验证方法）：支持的用于验证客户端的身份的方法，如基本身份验证、客户端证书等。
9. "scopes_supported"（支持的范围）：支持的访问令牌授权范围。

客户端可以通过发送HTTP请求到授权服务器的特定端点（通常是以".well-known"开头的URL，如/.well-known/oauth-authorization-server）来获取授权服务器的元数据。元数据请求可以帮助客户端自动配置与授权服务器的交互，并了解其支持的功能和要求。
