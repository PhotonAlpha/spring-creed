# 探索spring-security-oauth2-authorization-server OAUTH2/OIDC

## Spring OAuth2 Filter注册流程

### Ⅰ.AuthorizationServerSettings 注册了以下api endpoint

- authorizationEndpoint("/oauth2/authorize") `AUTHORIZATION_ENDPOINT` oauth2模式获取access token

  当grant_type:authorization_code时，用于获取code，在通过oauth2/token 获取token.

  ```txt
  http://localhost:48080/oauth2/authorize?response_type=code&client_id=jwt-client&state=qazxswedcvfr&scope=message.read%20message.write&redirect_uri=http://127.0.0.1:8080/authorized&code_challenge=rrkfk3AwBUxd20J2RjBGvdI13L_P36On7t6JOMHPYg0&code_challenge_method=S256
  ```

  

- deviceAuthorizationEndpoint("/oauth2/device_authorization") `DEVICE_AUTHORIZATION_ENDPOINT`

  同上，区别是这里是通过设备获取code

  

- deviceVerificationEndpoint("/oauth2/device_verification") `DEVICE_VERIFICATION_ENDPOINT`

- tokenEndpoint("/oauth2/token") `TOKEN_ENDPOINT`

- jwkSetEndpoint("/oauth2/jwks") `JWK_SET_ENDPOINT` 

- tokenRevocationEndpoint("/oauth2/revoke") `TOKEN_REVOCATION_ENDPOINT`  用于移除token

- tokenIntrospectionEndpoint("/oauth2/introspect") `TOKEN_INTROSPECTION_ENDPOINT` 

- oidcClientRegistrationEndpoint("/connect/register") `OIDC_CLIENT_REGISTRATION_ENDPOINT` 

- oidcUserInfoEndpoint("/userinfo") `OIDC_USER_INFO_ENDPOINT` 

- oidcLogoutEndpoint("/connect/logout") `OIDC_LOGOUT_ENDPOINT`  用于移除OIDC token



### Ⅱ. Endpoint注册流程

首先OAuth2AuthorizationServerConfigurer#init(HttpSecurity) 中 this.configurers.values()会注册endpoint

```java
private Map<Class<? extends AbstractOAuth2Configurer>, AbstractOAuth2Configurer> createConfigurers() {
		Map<Class<? extends AbstractOAuth2Configurer>, AbstractOAuth2Configurer> configurers = new LinkedHashMap<>();
		configurers.put(OAuth2ClientAuthenticationConfigurer.class,
				new OAuth2ClientAuthenticationConfigurer(this::postProcess)); 
		configurers.put(OAuth2AuthorizationServerMetadataEndpointConfigurer.class,
				new OAuth2AuthorizationServerMetadataEndpointConfigurer(this::postProcess)); 
		configurers.put(OAuth2AuthorizationEndpointConfigurer.class,
				new OAuth2AuthorizationEndpointConfigurer(this::postProcess));
		configurers.put(OAuth2TokenEndpointConfigurer.class, new OAuth2TokenEndpointConfigurer(this::postProcess));
		configurers.put(OAuth2TokenIntrospectionEndpointConfigurer.class,
				new OAuth2TokenIntrospectionEndpointConfigurer(this::postProcess));
		configurers.put(OAuth2TokenRevocationEndpointConfigurer.class,
				new OAuth2TokenRevocationEndpointConfigurer(this::postProcess));
		configurers.put(OAuth2DeviceAuthorizationEndpointConfigurer.class,
				new OAuth2DeviceAuthorizationEndpointConfigurer(this::postProcess));
		configurers.put(OAuth2DeviceVerificationEndpointConfigurer.class,
				new OAuth2DeviceVerificationEndpointConfigurer(this::postProcess));
		return configurers;
	}
```

> [!TIP]
>
> 上述所有`EndpointConfigurer#configure(HttpSecurity httpSecurity)`中，都会配置 addFilterAfter(XXXFilter, <Authenticate/Authorization>Filter.class)
>
> After `AuthorizationFilter`意味着是**后置的验证器**，在**认证成功之后**才会执行
>
> After `AbstractPreAuthenticatedProcessingFilter` 意味着是**前置的验证器**，在**认证之前**执行
>
> - **OAuth2ClientAuthenticationConfigurer:** After `AbstractPreAuthenticatedProcessingFilter`
> - **OAuth2AuthorizationServerMetadataEndpointConfigurer:** After `AbstractPreAuthenticatedProcessingFilter`
> - **OAuth2AuthorizationEndpointConfigurer:**After `AbstractPreAuthenticatedProcessingFilter`
> - **OAuth2TokenEndpointConfigurer** After `AuthorizationFilter`
> - **OAuth2TokenIntrospectionEndpointConfigurer:**After `AuthorizationFilter`
> - **OAuth2TokenRevocationEndpointConfigurer:**After `AuthorizationFilter`
> - **OAuth2DeviceAuthorizationEndpointConfigurer:**After `AuthorizationFilter`
> - **OAuth2DeviceVerificationEndpointConfigurer:** After `AbstractPreAuthenticatedProcessingFilter`
>
> ```java
> @Bean
> @Order(Ordered.HIGHEST_PRECEDENCE)
> public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http, AuthorizationServerSettings authorizationServerSettings)
>         throws Exception {
>   	OAuth2AuthorizationServerConfigurer authorizationServerConfigurer = OAuth2AuthorizationServerConfigurer.authorizationServer();//创建authorizationServer configuration
>     http.securityMatcher(authorizationServerConfigurer.getEndpointsMatcher()) //想Spring security注册相应的filter
>         .with(authorizationServerConfigurer, Customizer.withDefaults())
>         .authorizeHttpRequests((authorize) ->
>                 authorize.anyRequest().authenticated()
>         );
> }
> 
> ```
>
> 1. 上述配置`#authorizeHttpRequests()`会添加 `org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer`
> 2. `AuthorizeHttpRequestsConfigurer#configure(H http)` 会添加 `org.springframework.security.web.access.intercept.AuthorizationFilter`

> [!NOTE]
>
> 关于`request.getParameterMap()`知识点， 后面的所有请求都是围绕下属获取参数的。
>
> - `GET`请求，一般是传入 `Params` 参数
> - `POST`请求，一般是使用 `x-www-form-urlencoded`作为body传入参数

> [!IMPORTANT]
>
> Filter执行流程为
>
> 1. 先注册 **authenticationConverters**
> 2. 在执行 **authenticationProviders**
>
> 因此在上述端口，都是按照注册的这两个转换器进行处理的



#### ⅰ. `OAuth2ClientAuthenticationConfigurer` OAuth2Client身份验证配置器

- 向*requestMatcher*注册以下endpoint

  - *TOKEN_ENDPOINT*
  - *TOKEN_INTROSPECTION_ENDPOINT*
  - *TOKEN_REVOCATION_ENDPOINT*
  - *DEVICE_AUTHORIZATION_ENDPOINT*

- 注册**Filter**过滤器

  - **`OAuth2ClientAuthenticationFilter`** , 提供以下endpoint

    - `POST /oauth2/token`  
    - `POST /oauth2/introspect`
    - `POST /oauth2/revoke`
    - `POST /oauth2/device_authorization` 

    > [!IMPORTANT]
    >
    > 此过滤器主要做下以下几件事
    >
    > 1. AuthenticationConverter#convert(HttpServletRequest request) 返回 Authentication, 简单的转换request请求
    >
    > 2. AuthenticationProvider#authenticate(Authentication authentication) 返回 Authentication， 验证请求的authentication，转换为新的authentication
    >
    > 3. authenticationSuccessHandler#onAuthenticationSuccess(request, response, authenticationResult)
    >
    >    securityContext.setAuthentication(authentication);设置全局的authentication
    >
    > 4. AuthorizationFilter 中验证securityContext

  

- 向`authenticationConverters`注册，并返回`Authentication`

  1. **JwtClientAssertionAuthenticationConverter**

     请求params【`POST x-www-form-urlencoded`】：

       - **client_assertion_type (REQUIRED):** 必须是 `urn:ietf:params:oauth:client-assertion-type:jwt-bearer`
  
       - **client_assertion (REQUIRED):**  此处是 jwt token
  
       - **client_id (OPTIONAL as per specification but REQUIRED by this implementation):**必须是client ID

  
  2. **ClientSecretBasicAuthenticationConverter**
  
     请求params【`POST x-www-form-urlencoded`】：
  
       - **Authorization:** base64格式的用户名密码
  

  3. **ClientSecretPostAuthenticationConverter**
  
     请求params【`POST x-www-form-urlencoded`】：
  
       - **client_id (REQUIRED):**
  
       - **client_secret (REQUIRED):**
  
  
  4. **PublicClientAuthenticationConverter**
  
     请求params【`POST x-www-form-urlencoded`】：
  
       - **code_verifier (REQUIRED):**
  
       - **client_id (REQUIRED):**
  
  
  5. **X509ClientCertificateAuthenticationConverter**
  
     请求params【`POST x-www-form-urlencoded`】：
  
    - **client_id (REQUIRED):**
    - **(X509Certificate[]) request.getAttribute("jakarta.servlet.request.X509Certificate")**
  
- 向`authenticationProviders`注册，并返回`OAuth2ClientAuthenticationToken`

  > [!IMPORTANT]
  >
  > `OAuth2ClientAuthenticationConfigurer` 不同于 `xxxEndpointConfigurer`， 它主要的作用就是拦截上述的api，验证`authenticate`
  >
  > `httpSecurity.addFilterAfter(postProcess(clientAuthenticationFilter), AbstractPreAuthenticatedProcessingFilter.class);`的作用是在调用下面的endpoint之前，securityContext.setAuthentication(authentication);

  

  1. **JwtClientAssertionAuthenticationProvider**

     used for OAuth 2.0 Client Authentication, which authenticates the Jwt `client_assertion` parameter.

     

     > [!NOTE]
     >
     > 在**PRIVATE_KEY_JWT**与**CLIENT_SECRET_JWT**模式下，启用验证。

     默认使用**NimbusJwt**。nimbus-jose-jwt是基于Apache2.0开源协议的JWT开源库，支持所有的签名(JWS)和加密(JWE)算法。

     

     

  2. **X509ClientCertificateAuthenticationProvider**

     used for OAuth 2.0 Client Authentication, which authenticates the client X509Certificate received when the `tls_client_auth` or `self_signed_tls_client_auth` authentication method is used.

     

     > [!NOTE]
     >
     > 在**TLS_CLIENT_AUTH**与**SELF_SIGNED_TLS_CLIENT_AUTH**模式下，启用验证。

     

     验证`X509Certificate[] clientCertificateChain = (X509Certificate[]) clientAuthentication.getCredentials();`与JWK的clientCertificate是否相同。

     

  3. **ClientSecretAuthenticationProvider**

     used for OAuth 2.0 Client Authentication, which authenticates the `client_secret` parameter.

     验证`client_secret`是否匹配，如果存在`code_verifier`，比较并验证.

     

     > [!NOTE]
     >
     > 在**CLIENT_SECRET_BASIC**与**CLIENT_SECRET_POST**模式下，启用验证。

     

  4. **PublicClientAuthenticationProvider**

     used for OAuth 2.0 Public Client Authentication, which authenticates the `code_verifier` parameter.

     在PCKCE认证模式下，检查是否与`code_challenge`哈希值相同，默认貌似只支持`S256(SHA-256)`比较.

     > [!NOTE]
     >
     > 在**NONE**模式下，启用验证。

  


---

> [!IMPORTANT]
>
> 以下所有的**Filter**都是在**`AuthorizationFilter`**之后，[⌘ + 点击查看源码](#Ⅱ. Endpoint注册流程)
>
> > builder.addFilterAfter(postProcess(deviceAuthorizationEndpointFilter), AuthorizationFilter.class);
>
> `AuthorizationFilter#authorizationManager#check` 会执行检查是否已经认证成功。
>
> 只有成功之后，才会执行下面的Filter代码，否则会抛出**未授权**的异常。



#### ⅱ. `OAuth2AuthorizationServerMetadataEndpointConfigurer` OAuth 2.0 授权服务器元数据端点的配置器

- 向*requestMatcher*注册以下endpoint 

  - /.well-known/oauth-authorization-server/**

- 注册**Filter**过滤器

  - **`OAuth2AuthorizationServerMetadataEndpointFilter`**，提供查询支持的OAuth2.0的端口

  

#### ⅲ. `OAuth2AuthorizationEndpointConfigurer` OAuth 2.0 授权端点的配置器。

- 向*requestMatcher*注册以下endpoint，支持**GET POST**

  - **AUTHORIZATION_ENDPOINT**

- 注册**Filter**过滤器

  - **`OAuth2AuthorizationEndpointFilter`**, 提供 `/oauth2/authorize`endpoint

- 向`authenticationConverters`注册，并返回`Authentication`

  1. **OAuth2AuthorizationCodeRequestAuthenticationConverter**

     请求params【`GET params`/ `POST OPENID模式`】：

       - **response_type (REQUIRED):** 必须是 `code`

       - **client_id (REQUIRED):**

       - redirect_uri (OPTIONAL):

       - scope (OPTIONAL):

       - state (**RECOMMENDED**):

       - code_challenge (**REQUIRED** for public clients) - (**PKCE**模式):

       - code_challenge_method (OPTIONAL for public clients) - (**PKCE**模式):

         

  2. **OAuth2AuthorizationConsentAuthenticationConverter**

     请求params【`POST x-www-form-urlencoded`】：

       - **client_id (REQUIRED):** 

       - **state (REQUIRED):**

       - scope (OPTIONAL):

         

- 向`authenticationProviders`注册，并返回`OAuth2AuthorizationCodeRequestAuthenticationToken`

  1. **OAuth2AuthorizationCodeRequestAuthenticationProvider**

      the OAuth 2.0 Authorization Request used in the Authorization Code Grant.

      OAuth2AuthorizationCodeRequestAuthenticationToken 验证器

  2. **OAuth2AuthorizationConsentAuthenticationProvider** 
  
       the OAuth 2.0 Authorization Consent used in the Authorization Code Grant.

      OAuth2DeviceAuthorizationConsentAuthenticationToken 验证器

     

     > [!NOTE]
     >
     > 用于生成授权码code

     默认使用OAuth2AuthorizationCodeRequestAuthenticationValidator验证scopes和redirectUri

     在PCKCE认证模式下，code_challenge (REQUIRED for public clients) - RFC 7636 (PKCE)

     如果`authorizationConsentRequired`,返回`OAuth2AuthorizationConsentAuthenticationToken`

     否则生成`OAuth2AuthorizationCode`,返回`OAuth2AuthorizationCodeRequestAuthenticationToken`

     
  
  2. **OAuth2AuthorizationConsentAuthenticationProvider** 并返回 `OAuth2AuthorizationCodeRequestAuthenticationToken`

     the OAuth 2.0 Authorization Consent used in the Authorization Code Grant.
  
     参考上述，当启用授权确认页面时，会使用此provider。用于确认并验证**scopes**。
  
     > [!NOTE]
     >
     > 'openid' scope is auto-approved as it does not require consent
  
     

#### ⅳ. **OAuth2TokenEndpointConfigurer**

- 向*requestMatcher*注册以下endpoint，支持**POST**

  - TOKEN_ENDPOINT

    

- 注册**Filter**过滤器

  - **`OAuth2TokenEndpointFilter`**, 提供 `/oauth2/token`endpoint

    

- 向`authenticationConverters`注册，并返回`Authentication`

  1. **OAuth2AuthorizationCodeAuthenticationConverter**

     请求params【`POST x-www-form-urlencoded`】：

       - **grant_type (REQUIRED):** 必须是`authorization_code`
       - **code (REQUIRED):**
       - **redirect_uri (REQUIRED):**

     

  2. **OAuth2RefreshTokenAuthenticationConverter**

     请求params【`POST x-www-form-urlencoded`】：

       - **grant_type (REQUIRED):**  必须是`refresh_token`

       - **refresh_token (REQUIRED):**

       - scope (OPTIONAL)

         

  3. **OAuth2ClientCredentialsAuthenticationConverter**

     请求params【`POST x-www-form-urlencoded`】：

       - **grant_type (REQUIRED):**  必须是`client_credentials`

       - scope (OPTIONAL)

         

         

  4. **OAuth2DeviceCodeAuthenticationConverter**

     请求params【`POST x-www-form-urlencoded`】：

       - **grant_type (REQUIRED):** 必须是`urn:ietf:params:oauth:grant-type:device_code`

       - **device_code (REQUIRED):**

         

  5. **OAuth2TokenExchangeAuthenticationConverter**

     请求params【`POST x-www-form-urlencoded`】：

       - **grant_type (REQUIRED):** 必须是`urn:ietf:params:oauth:grant-type:token-exchange`

       - resource (OPTIONAL)

       - audience (OPTIONAL)

       - scope (OPTIONAL)

       - requested_token_type (OPTIONAL)

       - **subject_token (REQUIRED)**

       - **subject_token_type (REQUIRED)**

       - actor_token (OPTIONAL, REQUIRED if **actor_token_type** is provided)

       - actor_token_type (OPTIONAL, REQUIRED if **actor_token** is provided)

         

- 向`authenticationProviders`注册，并返回`OAuth2AccessTokenAuthenticationToken`

  1. **OAuth2AuthorizationCodeAuthenticationProvider** `AuthorizationGrantType=authorization_code`

     the OAuth 2.0 Authorization Code Grant.

     > [!NOTE]
     >
     > 注册JwtGenerator 与 OAuth2AccessTokenGenerator Token generator
     >
     > ```java
     > JwtGenerator jwtGenerator = getJwtGenerator(httpSecurity);
     > OAuth2AccessTokenGenerator accessTokenGenerator = new OAuth2AccessTokenGenerator();
     > accessTokenGenerator.setAccessTokenCustomizer(getAccessTokenCustomizer(httpSecurity));
     > OAuth2RefreshTokenGenerator refreshTokenGenerator = new OAuth2RefreshTokenGenerator();
     > if (jwtGenerator != null) {
     >     tokenGenerator = new DelegatingOAuth2TokenGenerator(jwtGenerator, accessTokenGenerator,
     >           refreshTokenGenerator);
     > } else {
     >     tokenGenerator = new DelegatingOAuth2TokenGenerator(accessTokenGenerator, refreshTokenGenerator);
     > }
     > 
     > ```
     >
     > **JwtGenerator**:
     >
     > ​	**OAuth2TokenType**必须是**`ACCESS_TOKEN`**或者**`ID_TOKEN`**,并且`TokenSettings.AccessTokenFormat`必须是**`SELF_CONTAINED`**
     >
     > **OAuth2AccessTokenGenerator:**
     >
     > ​	**OAuth2TokenType**必须是**`ACCESS_TOKEN`并且`TokenSettings.AccessTokenFormat`必须是**`REFERENCE`**
     >
     > ​	

     生成Token的步骤：

       a. AuthorizationGrantType contains **REFRESH_TOKEN**, 生成 REFRESH_TOKEN。通常情况下 AuthorizationCode 模式下是有 REFRESH_TOKEN的授权类型的。

       b. Scopes contains **OPENID**, 生成**OidcIdToken**

       c. OAuth2AuthenticationProviderUtils#invalidate 移除AuthorizationCode

       d. 返回`OAuth2AccessTokenAuthenticationToken`

     

  2. **OAuth2RefreshTokenAuthenticationProvider** `AuthorizationGrantType=refresh_token`

      the OAuth 2.0 Refresh Token Grant.

       a. 查询Refresh Token,生成新的access token

       b. token.reuse-refresh-tokens=false， 生成新的refresh Token

       c. Scopes contains **OPENID**, 生成**OidcIdToken**

       d. 返回`OAuth2AccessTokenAuthenticationToken`

     

  3. **OAuth2ClientCredentialsAuthenticationProvider** `AuthorizationGrantType=client_credentials`

      the OAuth 2.0 Client Credentials Grant.

     a. 验证scopes

     b. 返回`OAuth2AccessTokenAuthenticationToken`

  4. **OAuth2DeviceCodeAuthenticationProvider** `AuthorizationGrantType=urn:ietf:params:oauth:grant-type:device_code`

      the Device Access Token Request used in the OAuth 2.0 Device Authorization Grant.

     a. 验证 `deviceCode`，Invalidate the device code given that a different client is attempting

     b. 验证 `userCode`

     > // In https://www.rfc-editor.org/rfc/rfc8628.html#section-3.5,
     > // the following error codes are defined:
     > 
     > // authorization_pending
     > // The authorization request is still pending as the end user hasn't
     > // yet completed the user-interaction steps (Section 3.3). The
     > // client SHOULD repeat the access token request to the token
     > // endpoint (a process known as polling). Before each new request,
     > // the client MUST wait at least the number of seconds specified by
     > // the "interval" parameter of the device authorization response (see
     > // Section 3.2), or 5 seconds if none was provided, and respect any
     > // increase in the polling interval required by the "slow_down"
     > // error.
     
     c. 根据返回的response，如果是`authorization_pending`，则需要重复再次发送请求。

     > // slow_down
     > // A variant of "authorization_pending", the authorization request is
     > // still pending and polling should continue, but the interval MUST
     > // be increased by 5 seconds for this and all subsequent requests.
     > // NOTE: This error is not handled in the framework.
     
     d. 返回`OAuth2AccessTokenAuthenticationToken`
     
  5. **OAuth2TokenExchangeAuthenticationProvider** `AuthorizationGrantType=urn:ietf:params:oauth:grant-type:token-exchange`

     Attempts to extract an Access Token Request from HttpServletRequest for the OAuth 2.0 Token Exchange Grant and then converts it to an OAuth2TokenExchangeAuthenticationToken used for authenticating the authorization grant.

     

     > [!IMPORTANT]
     >
     > ## 了解 OAuth/OIDC 中的令牌交换
     >
     > 在 [RFC 8693](https://tools.ietf.org/html/rfc8693) 中引入的令牌交换是一种 OAuth 扩展，允许受信任的客户端将现有令牌交换为具有不同属性或范围的新令牌。此机制特别适用于服务、应用程序或最终用户通过预授权的令牌获得正常的 OAuth 访问令牌，而无需完成完整的 OAuth 流程。
     >
     > ### 与授权码流程的比较
     >
     > 首先，让我们来看看 OAuth 授权码流程，它是获取访问令牌的最常见流程。
     >
     > ![authorization_code](./imgs/authorization_code_flow.png)
     >
     > 这是令牌交换流程：
     >
     > ![authorization_code](./imgs/exchange_code_flow.png)
     >
     > #### 重定向
     >
     > 主要区别在于，在授权码流程中，客户端应用程序将用户重定向到授权服务器以获取访问令牌。在令牌交换中，客户端应用程序可以与授权服务器交换令牌而无需涉及用户重定向。
     >
     > 这是因为在授权码流程中，客户端应用程序不被“信任”，需要知道用户的凭证才能获得访问令牌。在令牌交换中，客户端应用程序被信任已经从用户那里获得了令牌，授权服务器将验证该令牌并签发新令牌。
     >
     > ### 令牌发行者和令牌交换服务
     >
     > 在令牌交换流程中，“授权服务器”现在有两个参与者：
     >
     > 1. 令牌发行者：向客户端应用程序颁发主题令牌。
     > 2. 令牌交换服务：验证主题令牌并向客户端应用程序颁发新令牌。
     >
     > 令牌交换服务与授权码流程中的“授权服务器”相同，令牌发行者可以是第三方身份提供者，或从“授权服务器”拆分为专用服务。
     >
     > ## 何时使用令牌交换？
     >
     > 令牌交换流程可以在无用户交互的情况下进行，这在以下场景中很有用：
     >
     > - **模拟：** 允许服务（例如，后台微服务）或管理员用户在不暴露完整用户凭据的情况下代表用户执行操作。
     > - **自动化：** 允许受信任的服务自动执行操作，无需人工干预，或执行自动化测试。
     > - **与其他 IdP 的集成：** 在不同的身份系统之间翻译令牌，以保持无缝的用户体验并有效管理权限，一个常见场景是将 SSO 令牌翻译为下游服务的令牌。
     > - **迁移：** 将令牌从一个授权服务器迁移到另一个，例如，从遗留系统迁移到现代 OAuth/OIDC 兼容系统。
     >
     > ## 令牌交换过程
     >
     > 交换新访问令牌是最常见的用例，我们将以此为例。不仅限于访问令牌，令牌交换还可以用于颁发其他类型的令牌，如刷新令牌、ID 令牌等。
     >
     > ### 客户端应用程序
     >
     > 要执行令牌交换，必须向授权服务器注册客户端应用程序。
     >
     > 并且客户端应用程序在启动令牌交换流程之前必须拥有一个 `subject_token`，此令牌通常由授权服务器或受信任的第三方身份提供者授予。拥有此令牌后，客户端应用程序现在可以“信任”地交换令牌，而无需用户的凭证和交互。
     >
     > ### 令牌交换请求
     >
     > 客户端向授权服务器的令牌端点发送请求以交换现有令牌。这包括 `subject_token`（正在交换的令牌）以及可选的目标受众、范围和令牌类型。
     >
     > 1. `grant_type`: 必须。此参数的值必须是 `urn:ietf:params:oauth:grant-type:token-exchange`，表示正在执行令牌交换。
     > 2. `subject_token`: 必须。用户的 PAT。
     > 3. `subject_token_type`: 必须。`subject_token` 参数中提供的安全令牌的类型。此参数的一个常见值是 `urn:ietf:params:oauth:token-type:access_token`，但可以根据正在交换的令牌而有所不同。
     > 4. `resource`: 可选。资源指示器，用于指定访问令牌的目标资源。
     > 5. `audience`: 可选。访问令牌的受众，指明令牌的预期接收者，如果未指定 `audience`，授权服务器可能会使用 `resource` 的值。
     > 6. `scope`: 可选。请求的范围。
     >
     > 此外，请求需要包含客户端信息，可以编码为基本认证头或作为表单数据发送。
     >
     > 以下是一个请求示例：
     >
     > ```shell
     > POST /oidc/token HTTP/1.1
     > Host: https://authorization-server.com
     > Content-Type: application/x-www-form-urlencoded
     > Authorization: Basic <base64-encoded-clientId:clientSecret>
     > 
     > grant_type=urn:ietf:params:oauth:grant-type:token-exchange
     > &subject_token=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
     > &subject_token_type=urn:ietf:params:oauth:token-type:access_token
     > &resource=https://resource-server.com/api/resource
     > &scope=read write
     > ```
     >
     > ## [参考](https://blog.logto.io/zh-CN/token-exchange#何时使用令牌交换)
     
     

#### ⅴ. OAuth2TokenIntrospectionEndpointConfigurer

- 向*requestMatcher*注册以下endpoint，支持**POST**

  - TOKEN_INTROSPECTION_ENDPOINT

    

- 注册**Filter**过滤器
  - **`OAuth2TokenIntrospectionEndpointFilter`**, 提供 `/oauth2/introspect`endpoint

    

- 向`authenticationConverters`注册，并返回`Authentication`

  1. **OAuth2TokenIntrospectionAuthenticationConverter**

     请求params【`POST x-www-form-urlencoded`】：

     - **token (REQUIRED):** 需要验证的令牌
     - token_type_hint (OPTIONAL)

     

- 向`authenticationProviders`注册，并返回`OAuth2TokenIntrospectionAuthenticationToken`

  1. **OAuth2TokenIntrospectionAuthenticationProvider** 

     OAuth 2.0 Token Introspection.

     简单的总结来说，这个规范是为OAuth2扩展了一个API接口（Introspection Endpoint），让第三方Client可以查询上面提到的那些信息（比如，access_token是否还有效，谁颁发的，颁发给谁的，scope又哪些等等的元数据信息）。

     

     request parameters:

     - **TOKEN** (REQUIRED): 必选，可以是access_token或者refresh_token的内容。
     - **TOKEN_TYPE_HINT** (OPTIONAL): 表示token的类型，值为”access_token“或者"refresh_token"。
     - **Authorization: Basic czZCaGRSa3F0MzpnWDFmQmF0M2JW**：访问受保护资源的授权凭证。


#### ⅵ. OAuth2TokenRevocationEndpointConfigurer   //token移除

- 向*requestMatcher*注册以下endpoint，支持**POST**

  - TOKEN_REVOCATION_ENDPOINT

    

- 注册**Filter**过滤器
  - **`OAuth2TokenRevocationEndpointFilter`**, 提供 ` /oauth2/revoke`endpoint

    

- 向`authenticationConverters`注册，并返回`Authentication`

  1. **OAuth2TokenRevocationAuthenticationConverter**

     请求params【`POST x-www-form-urlencoded`】：

     - **token (REQUIRED):** 需要验证的令牌

     - token_type_hint (OPTIONAL)

       

- 向`authenticationProviders`注册，并返回`OAuth2TokenRevocationAuthenticationToken`

  1. **OAuth2TokenRevocationAuthenticationProvider** `AuthorizationGrantType=urn:ietf:params:oauth:grant-type:token-exchange`

     OAuth 2.0 Token Revocation.

     简单来说，这个协议规定了一个Authorization server提供一个怎样的API来供Client撤销access_token或者refresh_token。

     

     request parameters:

     - **TOKEN** (REQUIRED): 必选，可以是access_token或者refresh_token的内容。

     - **TOKEN_TYPE_HINT** (OPTIONAL): 表示token的类型，值为”access_token“或者"refresh_token"。

     - **Authorization: Basic czZCaGRSa3F0MzpnWDFmQmF0M2JW**：访问受保护资源的授权凭证。


#### ⅶ. OAuth2DeviceAuthorizationEndpointConfigurer

- 向*requestMatcher*注册以下endpoint，支持**POST**

  - DEVICE_AUTHORIZATION_ENDPOINT 

    

- 注册**Filter**过滤器
  - **`OAuth2DeviceAuthorizationEndpointFilter`**, 提供 `/oauth2/device_authorization`endpoint

    

- 向`authenticationConverters`注册，并返回`Authentication`

  1. **OAuth2DeviceAuthorizationRequestAuthenticationConverter**

     请求params【`POST x-www-form-urlencoded`】：

     - scope (OPTIONAL)

       

- 向`authenticationProviders`注册，并返回`OAuth2DeviceAuthorizationRequestAuthenticationToken`

  1. **OAuth2DeviceAuthorizationRequestAuthenticationProvider** `AuthorizationGrantType=urn:ietf:params:oauth:grant-type:device_code`

     the Device Authorization Request used in the OAuth 2.0 Device Authorization Grant.

     生成`deviceCode` 和 `userCode`,并且返回 `OAuth2DeviceAuthorizationRequestAuthenticationToken`


#### ⅷ. OAuth2DeviceVerificationEndpointConfigurer 

- 向*requestMatcher*注册以下endpoint，支持**GET POST**

  - DEVICE_VERIFICATION_ENDPOINT

    

- 注册**Filter**过滤器
  - **`OAuth2DeviceVerificationEndpointFilter`**, 提供 `/oauth2/device_verification`endpoint

    

- 向`authenticationConverters`注册，并返回`Authentication`

  1. **OAuth2DeviceVerificationAuthenticationConverter**

     请求params【`GET params`/`POST x-www-form-urlencoded`】：

     - **user_code (REQUIRED)**

       

  2. **OAuth2DeviceAuthorizationConsentAuthenticationConverter**

     请求params【`POST x-www-form-urlencoded`】：

     - **STATE (REQUIRED)**

     - **client_id (REQUIRED)**

     - **user_code (REQUIRED)**

     - **scope (OPTIONAL)**

       

- 向`authenticationProviders`注册，并返回`OAuth2DeviceAuthorizationRequestAuthenticationToken`

  1. **OAuth2DeviceVerificationAuthenticationProvider**

     the Device *Verification Request (submission of the user code)* used in the OAuth 2.0 Device Authorization Grant.

     - 检查 `AuthorizationConsent`, 未确认则返回`OAuth2DeviceAuthorizationConsentAuthenticationToken`
     - 已经确认则生成token，并返回`OAuth2DeviceVerificationAuthenticationToken`

  2. **OAuth2DeviceAuthorizationConsentAuthenticationProvider**

       the Device *Authorization Consent* used in the OAuth 2.0 Device Authorization Grant.
       
      - 检查 如果**Authorization consent denied (or revoked)**，移除`authorizationConsentService#remove`并标记`authorizationService metadata invalidated`
      - 如果已经通过，则生成token，并返回`OAuth2DeviceVerificationAuthenticationToken`


## 目前Spring oAuth2支持以下几种授权模式：

> 授权模式参考： **org.springframework.security.oauth2.core.AuthorizationGrantType**
>
> 授权方法：**org.springframework.security.oauth2.core.ClientAuthenticationMethod**
>
> Token格式：OAuth2TokenFormat 
>
> - **SELF_CONTAINED**(JWT token)   , 源码路径：`org.springframework.security.oauth2.server.authorization.token.JwtGenerator`
>
> - **REFERENCE**(随机字符串), 源码路径：`org.springframework.security.oauth2.server.authorization.token.OAuth2AccessTokenGenerator`
>
>   ```java
>   new Base64StringKeyGenerator(Base64.getUrlEncoder().withoutPadding(), 96)
>   ```

### ①. AUTHORIZATION_CODE

   **::code模式::**

   ```shell
   #1. 前端发起请求
   http://localhost:48080/oauth2/authorize?client_id=jwt-client&redirect_uri=http://127.0.0.1:8080/authorized&response_type=code&state=qazxswedcvfr&scope=message.read message.write
   
   #2. 如果需要确认为true["settings.client.require-authorization-consent": true], 进入确认页面，提交如下确认请求。
   curl 'http://localhost:48080/oauth2/authorize' \
     -H 'Content-Type: application/x-www-form-urlencoded' \
     -H 'Origin: http://localhost:48080' \
     -H 'Referer: http://localhost:48080/oauth2/authorize?client_id=jwt-client&redirect_uri=http://127.0.0.1:8080/authorized&response_type=code&state=qazxswedcvfr&scope=message.read%20message.write&continue' \
     --data-raw 'client_id=jwt-client&state=mA-OaGwUpXwZy8RMJm4q-W6U3NXqwZaeuq-zOEh66IM%3D&scope=message.read&scope=message.write'
     
   #3. 返回code
   http://127.0.0.1:8080/authorized?code=ERDH3lro0-aJjmWv0HHzygQ7PBsQl9gS7jsxs30wKvk1XWxYTkQqt1YB8T6a4qnN-opPBOuRcp6V2tgTkP-wwM1jlixxgG1rHezyrc1CIibx94oa2XfBYTZYP-7IJnv3&state=qazxswedcvfr
   
   #4. 通过code申请token
   curl -X POST 'http://localhost:48080/oauth2/token' \
   -H 'Content-Type: application/x-www-form-urlencoded' \
   --data-urlencode 'grant_type=authorization_code' \
   --data-urlencode 'code=ERDH3lro0-aJjmWv0HHzygQ7PBsQl9gS7jsxs30wKvk1XWxYTkQqt1YB8T6a4qnN-opPBOuRcp6V2tgTkP-wwM1jlixxgG1rHezyrc1CIibx94oa2XfBYTZYP-7IJnv3' \
   --data-urlencode 'redirect_uri=http://127.0.0.1:8080/authorized' \
   --data-urlencode 'client_secret=password' \
   --data-urlencode 'client_id=jwt-client'
   ```

   **[::PCKCE模式::](#GenerateCodeVerifierAndCodeChallenge)**

   > [!TIP]
   >
   > [关于授权码拦截攻击的思考](#秘钥凭证 （Proof Key）在OAuth 客户端 Code Exchange 阶段的应用)
   >
   > 不管哪一种授权方式，第三方应用申请令牌之前，都必须先到系统备案，说明自己的身份，然后会拿到两个身份识别码：
   >
   > - 客户端 ID（client ID）
   > - 客户端密钥（client secret）
   >
   > 在获取到code之后，到了A网站的服务端，就要通过`code` + `clientid` + **`clientSecret`**去获取`accesstoken`

   

   > [!IMPORTANT]
   >
   > The `requireProofKey` setting is important to prevent the [PKCE Downgrade Attack](https://datatracker.ietf.org/doc/html/draft-ietf-oauth-security-topics-25#name-pkce-downgrade-attack).
   >
   > ```json
   > {
   >   "@class": "java.util.Collections$UnmodifiableMap",
   >   "settings.client.require-proof-key": true, // 作用就是强制用户需要传入code_challenge，防止降级攻击。如果是false，可能会绕过 codeVerifierValid，从而获取token
   >   "settings.client.require-authorization-consent": true //需要用户进入确认页面确认 授权范围
   > }
   > ```
   >
   > code_challenge是**base64**格式
   > https://docs.spring.io/spring-authorization-server/reference/guides/how-to-pkce.html
   > https://datatracker.ietf.org/doc/html/rfc7636#section-4
   >
   > > ```tex
   > > 4.2.  Client Creates the Code Challenge
   > > 
   > >    The client then creates a code challenge derived from the code
   > >    verifier by using one of the following transformations on the code
   > >    verifier:
   > > 
   > >    plain
   > >       code_challenge = code_verifier
   > >    S256
   > >       code_challenge = BASE64URL-ENCODE(SHA256(ASCII(code_verifier)))
   > > ```

   

   ```shell
   #1. 生成随机字符串，使用sha256加密，生成 code_challenge
   const characters = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-._~';
   let randomString = '';
   const length = Math.floor(Math.random()) + 43
   for (let i = 0; i < length; i++) {
       randomString += characters.charAt(Math.floor(Math.random() * characters.length));
   }
   function base64UrlEncode(str) {
       return str
           .replace(/\+/g, '-')
           .replace(/\//g, '_')
           .replace(/=+$/, '');
   }
   function base64UrlDecode(str) {
       str = str
           .replace(/-/g, '+')
           .replace(/_/g, '/');
       while (str.length % 4) {
           str += '=';
       }
       return atob(str);
   }
   console.log('randomString',randomString)
   const msgUint8 = new TextEncoder().encode(randomString);
   const hashBuffer = await window.crypto.subtle.digest("SHA-256", msgUint8); 
   base64UrlEncode(btoa(String.fromCharCode.apply(null, new Uint8Array(hashBuffer))))
   
   example:
   raw: msrz_wwpglVtouDbPUfPDiplSGDjvMhr9c9CzBpjJ2g
   code_challenge_sha256: rrkfk3AwBUxd20J2RjBGvdI13L_P36On7t6JOMHPYg0
       
   #2. 前端发起请求
   http://localhost:48080/oauth2/authorize?response_type=code&client_id=jwt-client&state=qazxswedcvfr&scope=message.read message.write openid&redirect_uri=http://127.0.0.1:8080/authorized&code_challenge=rrkfk3AwBUxd20J2RjBGvdI13L_P36On7t6JOMHPYg0&code_challenge_method=S256
   
   #3. 如果需要确认为true["settings.client.require-authorization-consent": true], 进入确认页面，提交如下确认请求。
   curl 'http://localhost:48080/oauth2/authorize' \
     -H 'Content-Type: application/x-www-form-urlencoded' \
     -H 'Origin: http://localhost:48080' \
     -H 'Referer: http://localhost:48080/oauth2/authorize?response_type=code&client_id=jwt-client&state=qazxswedcvfr&scope=message.read%20message.write&redirect_uri=http://127.0.0.1:8080/authorized&code_challenge=rrkfk3AwBUxd20J2RjBGvdI13L_P36On7t6JOMHPYg0&code_challenge_method=S256&continue' \
     --data-raw 'client_id=jwt-client&state=_wqz8__sNqO6XFhXz7qnnhi19KTDkJYLJlLYBPyVVlE%3D&scope=message.read&scope=message.write'
     
   #4. 返回code
   http://127.0.0.1:8080/authorized?code=9LRDdJpkL6bwiaqisUjLUNZLg6UUrPvoydww9REnWOy2XI3ELb57sy-yAn4ok07kncsEICZ7qqj4_1L6FpugxJxQCcCRJxJbSAqfhhgKvY-5A8x770MYFyVbxhWy2SxJ&state=qazxswedcvfr
   
   #5. 通过code申请token
   curl -X POST 'http://localhost:48080/oauth2/token' \
   -H 'Content-Type: application/x-www-form-urlencoded' \
   --data-urlencode 'grant_type=authorization_code' \
   --data-urlencode 'code=9LRDdJpkL6bwiaqisUjLUNZLg6UUrPvoydww9REnWOy2XI3ELb57sy-yAn4ok07kncsEICZ7qqj4_1L6FpugxJxQCcCRJxJbSAqfhhgKvY-5A8x770MYFyVbxhWy2SxJ' \
   --data-urlencode 'redirect_uri=http://127.0.0.1:8080/authorized' \
   --data-urlencode 'code_verifier=msrz_wwpglVtouDbPUfPDiplSGDjvMhr9c9CzBpjJ2g' \
   --data-urlencode 'client_id=jwt-client' \
   --data-urlencode 'client_secret=password' \
   --data-urlencode 'code_challenge_method=S256'
   ```

   ```json
   //6. 返回token
   {
       "access_token": "eyJraWQiOiJyb290LWNyZWVkLW1hbGwiLCJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJldGhhbiIsImF1ZCI6Imp3dC1jbGllbnQiLCJuYmYiOjE3MzU3OTk0NTUsInNjb3BlIjpbIm9wZW5pZCIsIm1lc3NhZ2UucmVhZCIsIm1lc3NhZ2Uud3JpdGUiXSwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo0ODA4MCIsImV4cCI6MTczNTg4NTg1NSwiaWF0IjoxNzM1Nzk5NDU1LCJ1dWlkIjoiNmZlNjA3OWItMzg3Zi00NWY0LThlYTQtYmVlYmE4Mzc3ZTM4IiwianRpIjoiNTQxNTYwZWMtNzhjMy00MDM1LTg4ZmUtZDkwYWQ5YmEyOGY2In0.aT_YJlXmVhGhOMHn6Rih3VE3EzoTz8X1H7eFuozES9Ey-HWA-g7wCPvxaTg14tEvjvJ_xR_9fi-PB44_kcoWNqIHjNTYCbJ6CfVLrTamowbHCSsw-LVlYbY6FLMZPHv5PkzycscirdvvXwomcsX26Sn7Bu1xJ9GBXqk33XStok3ITQG7CNYQTt7JdFEYmZXzuyb-yoklpIPrFjCxpfHxF74MDZEBSdbGBJdfsYuzu1LZn4TbAs6-_9Ru_634fij80aQxL3TiJzqThzyayktflqBUr7bRgiP6J8kAfeNRjPtO35KnuHo_ChTXxFdoDAC2o1R2Cle7IAgNduzSnJwIOQ",
       "refresh_token": "8AxUZx2r-XH812zWn5cqOU6SW-N5z1Lr6yyX9EU911lP5-U23U9Ba08ERgoaDsx_NqGkGnSRM5S3e3YkANU9HEB3ug3Po-DUtAmUm5aN_uuVT7qkAyDfUQsbfwGMrcMo",
       "scope": "openid message.read message.write",
       "id_token": "eyJraWQiOiJyb290LWNyZWVkLW1hbGwiLCJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJldGhhbiIsImF1ZCI6Imp3dC1jbGllbnQiLCJhenAiOiJqd3QtY2xpZW50IiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo0ODA4MCIsImV4cCI6MTczNTgwMTI1NSwiaWF0IjoxNzM1Nzk5NDU1LCJqdGkiOiJkYjViNTJlNi1iM2NiLTQwMTctOGY0NC1iMWExNGNkOWZlMmMifQ.r6Tl3veZsijw5TMnDWb5rAeNYwQQYV10TfVP5llPsNG08R3doKVMW5YPaEgFNiG_EErdieODAZcqE1PikYz2O8apsRWA9AylNtH3Oqb5mdr3g1avdkldCiFYSRYzMSWzDumXvGHXuaQppay3_lElCgJyukQN-Uj_uOXnUkF7Fexfagsr3AjOpaYopwMlNFIjgw2KlaPm3Kk0EtIDibn1KR1jb1IEWJXh8OurAxgou1eLuHuYcPP73XvNvFCX0S-clBnpfooQYnLamCA2saMqagdqb2-EcPAe2B_cYpLe3M9H4SWPMsGuW1VrQXuE90Z36TbWrSfiOpKWdA2VJAU2qg",
       "token_type": "Bearer",
       "expires_in": 86399
   }
   ```

   

### ②. REFRESH_TOKEN

```shell
# refresh_token 发起请求，获取刷新的token
curl -X POST 'http://localhost:48080/oauth2/token' \
-H 'Content-Type: application/x-www-form-urlencoded' \
--data-urlencode 'grant_type=refresh_token' \
--data-urlencode 'refresh_token=ulN8mfCuiGBLL3rLWOa940v5JPIbXZ-zM5vMR0GX0eUdE3hvBCUDss6FdRyUuetu3bJxfNWC47wlT7W8pBHCfexU6gAJSTGvDJ5SwlvXaTBFNQQN5QtWCZErgcOhf_wU' \
--data-urlencode 'client_id=jwt-client' \
--data-urlencode 'client_secret=password'
```

```json
{
    "access_token": "eyJraWQiOiJjNTI3N2NmZC02YTQwLTQyMWYtYWViNi0yOGI2NWRiOGQ4MTAiLCJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJldGhhbiIsImF1ZCI6Imp3dC1jbGllbnQiLCJuYmYiOjE3MjkyNDg2MjYsInNjb3BlIjpbIm1lc3NhZ2UucmVhZCIsIm1lc3NhZ2Uud3JpdGUiXSwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo0ODA4MCIsImV4cCI6MTcyOTMzNTAyNiwiaWF0IjoxNzI5MjQ4NjI2LCJ1dWlkIjoiYTUyYzg5N2MtNjAyOS00MjRhLWJjNzgtMDEwMGQyOTIwZDk2IiwianRpIjoiNDJlN2U5ZmEtYTJiMS00ZWNiLWFlOTYtMGQyNjcxNTVmMzgwIn0.fDgm2_L3qyHJCIMUc_Hzdvz6wxWUOO_ujnaONIiB42vPZ6oUd64ZixFAOdcoNFvqxei8A-lFk2r97CKomlYqwaXS7qE-x2ugYqw4szE9kyG0phFS012cGbvhIPFcMVtSIS29BIrsEhTOSfyrXBtH9tjLrpXJcIHz22Z863iRFFWvCPZnINUnnQGftsFIrQhF3EOqnKphCrzwOFJ-PB36QbHhIETbvJ9V06PP3GqaxXuwurcNvbLuXb5RYeMk4ZC2qOFYXLaBJyfHbxv0wZzqm9S_asmffbmDwB3mNf6FoHi5jlJolr_6Ws83eKRizdhb7ewjbQFOuUhFYZB61MVIww",
    "refresh_token": "ulN8mfCuiGBLL3rLWOa940v5JPIbXZ-zM5vMR0GX0eUdE3hvBCUDss6FdRyUuetu3bJxfNWC47wlT7W8pBHCfexU6gAJSTGvDJ5SwlvXaTBFNQQN5QtWCZErgcOhf_wU",
    "scope": "message.read message.write",
    "token_type": "Bearer",
    "expires_in": 86399
}
```



### ③. CLIENT_CREDENTIALS 客户端认证

随机token生成方式

```shell
curl -X POST 'http://localhost:48080/oauth2/token' \
-H 'Content-Type: application/x-www-form-urlencoded' \
--data-urlencode 'grant_type=client_credentials' --data-urlencode 'client_secret=password' --data-urlencode 'client_id=random-client'
```

```json
{
    "access_token": "TS2V838SKPQYpq8W0CTO-z9W6FwXQXa8xOqU-jop_4ywh8yB08ejS0lX0XB6mO3Iu5p2gxqYbDC6goSdLXGrkmG8fEMhutKgswXHcaNIhx9ESkctUiSRWVsMAERGj3cO",
    "token_type": "Bearer",
    "expires_in": 86399
}
```

JWT token生成方式

```shell
curl -X POST 'http://localhost:48080/oauth2/token' \
-H 'Content-Type: application/x-www-form-urlencoded' \
--data-urlencode 'grant_type=client_credentials' \
--data-urlencode 'client_secret=password' \
--data-urlencode 'client_id=jwt-client'
```

```json
{
    "access_token": "eyJraWQiOiIwM2NkMjJhOS0xNTUwLTQ1ZWUtOWI4Ni02Y2U2ZTYxOTZhMGIiLCJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJqd3QtY2xpZW50IiwiYXVkIjoiand0LWNsaWVudCIsIm5iZiI6MTcyOTE0NjY3MiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo0ODA4MCIsImV4cCI6MTcyOTIzMzA3MiwiaWF0IjoxNzI5MTQ2NjcyLCJ1dWlkIjoiNDJiZDIwMWMtNDQ2NS00NTI2LTg1MDctYjA3ODMwMzZjMTc4IiwianRpIjoiN2FlNzViNmEtOGQ3OS00MWMzLWE3MWItM2JiOTVlZGQ1YTc1In0.OfwEDqxakpLIq7-yY7eS4e3p0BJyEHoxdAai1gOe3XfsAI55L6G2-YKGqK8C-5vNCYpTNvoHGDpe-ONEHNH800lLNeEc68iM11UCyfrzc34Lp7AqpHjptJs2XeqjqxdDhP778VZwVimmsZ2regiFNXV-Qqi42HW8y1FkQgUwVIR5WDQ7M9KQ5Glu475_A5_-mAndA9NzkhKcCmdclKz4zqA_oo3wQ-Qa8dyGn6lPV3sCs8HCDW6P4q8kYtlePydLEzNh-3Rf3FIg7JnUWmJKJr47Lbtay9oLTzq3eCUWQX3iCA-9olbvJnD2p2hT0uOQXzH5vnKrapJuuRvqrK4DoA",
    "token_type": "Bearer",
    "expires_in": 86399
}
```



### ④. ~~PASSWORD~~(Deprecated 弃用)

> The latest OAuth 2.0 Security Best Current Practice disallows the use of the Resource Owner Password Credentials grant. 

### ⑤. JWT_BEARER(暂不支持)

目前1.4.0 暂时不支持。[官方文档](https://docs.spring.io/spring-authorization-server/reference/core-model-components.html)

> `authorizationGrantTypes`: The [authorization grant type(s)](https://datatracker.ietf.org/doc/html/rfc6749#section-1.3) that the client can use. The supported values are `authorization_code`, `client_credentials`, `refresh_token`, `urn:ietf:params:oauth:grant-type:device_code`, and `urn:ietf:params:oauth:grant-type:token-exchange`.

但是可以自己实现：[官方文档](https://docs.spring.io/spring-authorization-server/reference/guides/how-to-ext-grant-type.html)

```java
public class CustomCodeGrantAuthenticationConverter implements AuthenticationConverter {

	@Nullable
	@Override
	public Authentication convert(HttpServletRequest request) {
		// grant_type (REQUIRED)
		String grantType = request.getParameter(OAuth2ParameterNames.GRANT_TYPE);
		if (!"urn:ietf:params:oauth:grant-type:custom_code".equals(grantType)) { 
			return null;
		}

		Authentication clientPrincipal = SecurityContextHolder.getContext().getAuthentication();

		MultiValueMap<String, String> parameters = getParameters(request);

		// code (REQUIRED)
		String code = parameters.getFirst(OAuth2ParameterNames.CODE); 
		if (!StringUtils.hasText(code) ||
				parameters.get(OAuth2ParameterNames.CODE).size() != 1) {
			throw new OAuth2AuthenticationException(OAuth2ErrorCodes.INVALID_REQUEST);
		}

		Map<String, Object> additionalParameters = new HashMap<>();
		parameters.forEach((key, value) -> {
			if (!key.equals(OAuth2ParameterNames.GRANT_TYPE) &&
					!key.equals(OAuth2ParameterNames.CLIENT_ID) &&
					!key.equals(OAuth2ParameterNames.CODE)) {
				additionalParameters.put(key, value.get(0));
			}
		});

		return new CustomCodeGrantAuthenticationToken(code, clientPrincipal, additionalParameters); 
	}

	private static MultiValueMap<String, String> getParameters(HttpServletRequest request) {
		Map<String, String[]> parameterMap = request.getParameterMap();
		MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>(parameterMap.size());
		parameterMap.forEach((key, values) -> {
			if (values.length > 0) {
				for (String value : values) {
					parameters.add(key, value);
				}
			}
		});
		return parameters;
	}

}
```

Client 访问时调用org.springframework.security.oauth2.client.JwtBearerOAuth2AuthorizedClientProvider ，支持该访问模式

### ⑥. DEVICE_CODE

> Spring Authorization Server cannot provide this capability out-of-the-box as it would not adhere to the secure by default principle, since the token endpoint would be "open" allowing a client to obtain an access token simply by providing the `client_id` parameter only.
>
> Spring Authorization Server 无法提供开箱即用的功能，因为它不遵守默认安全原则，因为令牌端点将“开放”，允许客户端仅通过提供 client_id 参数即可获取访问令牌。
>
> https://github.com/spring-projects/spring-authorization-server/issues/1522
>
> https://github.com/spring-projects/spring-authorization-server/issues/1116#issuecomment-1468628303
>
> 但是下面vendors (at least Okta, Auth0, Azure)是支持的，因为目前只能实现自定义的支持实现
>
> - https://auth0.com/docs/get-started/authentication-and-authorization-flow/call-your-api-using-the-device-authorization-flow#receive-tokens
> - https://developer.okta.com/docs/guides/device-authorization-grant/main/#request-access-id-and-refresh-tokens
> - https://learn.microsoft.com/en-us/azure/active-directory/develop/v2-oauth2-device-code#successful-authentication-response

因此代码执行顺序为

认证需要给 client 配置 ClientAuthenticationMethod.NONE

1. OAuth2ClientAuthenticationFilter

   - 注册 endpoint`oauth2/device_authorization`

   - DeviceClientAuthenticationConverter

   - DeviceClientAuthenticationProvider 

   - authenticationSuccessHandler#onAuthenticationSuccess

     ```java
     private void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
     			Authentication authentication) {
     		SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
     		securityContext.setAuthentication(authentication);
     		SecurityContextHolder.setContext(securityContext);
     		if (this.logger.isDebugEnabled()) {
     			this.logger.debug(LogMessage.format("Set SecurityContextHolder authentication to %s",
     					authentication.getClass().getSimpleName()));
     		}
     	}
     ```

2. AuthorizationFilter

3. OAuth2DeviceAuthorizationEndpointFilter

   1. 免登录设备授权

      ```shell
      curl -X POST 'http://localhost:48080/oauth2/device_authorization' \
          -H 'Content-Type: application/x-www-form-urlencoded' \
          --data-urlencode 'scope=message.read message.write' \
          --data-urlencode 'client_id=jwt-client'
          
      # 返回 device code
      {
          "user_code": "WGTD-MZJD",
          "device_code": "1GHjjvHqq7lKlLm65KIDS_V-LGZxcGt3ra6tSlRe3FQVPwrAZKFvTip4HsoYOemDaXhQ2aCr4oP0CPB_EH7-CeiZ0POXAX3ssi3f45ocwTXtRxAMfKMYUQ_kpBzXohgt",
          "verification_uri_complete": "http://localhost:48080/oauth2/device_verification?user_code=WGTD-MZJD",
          "verification_uri": "http://localhost:48080/oauth2/device_verification",
          "expires_in": 3600
      }    
      ```

   2. 用户可以利用手机扫描，浏览器自动触发登录和验证user_code的流程

      或者 浏览器访问http://localhost:48080/oauth2/device_verification?user_code=WGTD-MZJD 返回success

   3. 获取token

      ```shell
      curl -X POST 'http://localhost:48080/oauth2/token' \
          -H 'Content-Type: application/x-www-form-urlencoded' \
          -H 'Cookie: JSESSIONID=6F06D745B9FAEE81A852E81BC4E0F1AD' \
          --data-urlencode 'grant_type=urn:ietf:params:oauth:grant-type:device_code' \
          --data-urlencode 'device_code=waicX19Rv5svw7haCr3tVXezIZu0z45-bVSvXFh2yRdEKWQk2blZmkSC_9MozcecK_H0WBkt_cMIAtf3AyrYqPaQNn4VxNB_vobcIM2zf1cIVngWWmOzUZeNhqqKswYn' \
          --data-urlencode 'client_id=jwt-client'
      
      # 返回access token
      {
          "access_token": "eyJraWQiOiJyb290LWNyZWVkLW1hbGwiLCJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJldGhhbiIsImF1ZCI6Imp3dC1jbGllbnQiLCJuYmYiOjE3MzUyNzAxMjksInNjb3BlIjpbIm1lc3NhZ2UucmVhZCIsIm1lc3NhZ2Uud3JpdGUiXSwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo0ODA4MCIsImV4cCI6MTczNTM1NjUyOSwiaWF0IjoxNzM1MjcwMTI5LCJ1dWlkIjoiNDRiZmU5NzktNDAzYi00ZmI3LWJjOTItNzFjZDk5NjNmM2I2IiwianRpIjoiMmU2ZjljZDktNjY1YS00M2IxLTg2ODEtYTNlMzA4MDEyZjJiIn0.WebQVRBoBVq8HfY2EIBXUbbC4XjuFewsWrqDwhTr03kFuFaJmJ99UPZQcF3HdSJ3g9msnNCrAt81A0qNzIS_HZf07FfwzKq5PRDdcSQvQJuodl2A-6qLatbxR0GVUTTaMhJN_3GxFlt-u1sJFdsakI3dULlv2zpY5_QZ_6kidZAmwdmiKIbsTMY6-bT1VfvLAUnzc0Z7s8DCxPZtrFLP_hhmjaCnchSmOYYZMdoSM01cJWu3k5qY9ou1fmX2SNRRH4HZERYF8PcSePTbtWegNmGKmIRj1ETa543QCwm4WI_l5Sclsf5Sf4FQumdIhXCR1U-ihKUxzl2axP0duW4OHg",
          "refresh_token": "hnAmz9K8o4OWuRsO1NLmtjpQc1bC2CylZwb2oQFnPJ2b3_G1Z7EDgz0Z1Mn9_1DyHzATlqwV9V8g1DGqPVbnLPJQMPzaUst5cp1qnRRHn5O2bV3az_oGftIq5biAA8r6",
          "scope": "message.read message.write",
          "token_type": "Bearer",
          "expires_in": 86399
      }
      ```


### ⑦. TOKEN_EXCHANGE

在 [RFC 8693](https://tools.ietf.org/html/rfc8693) 中引入的令牌交换是一种 OAuth 扩展，允许受信任的客户端将现有令牌交换为具有不同属性或范围的新令牌。此机制特别适用于服务、应用程序或最终用户通过预授权的令牌获得正常的 OAuth 访问令牌，而无需完成完整的 OAuth 流程。

#### 与授权码流程的比较

首先，让我们来看看 OAuth 授权码流程，它是获取访问令牌的最常见流程。

![authorization_code_flow.png](./imgs/authorization_code_flow.png)

这是令牌交换流程：

![exchange_code_flow](./imgs/exchange_code_flow.png)

主要区别在于，在授权码流程中，客户端应用程序将用户重定向到授权服务器以获取访问令牌。在令牌交换中，客户端应用程序可以与授权服务器交换令牌而无需涉及用户重定向。

这是因为在授权码流程中，客户端应用程序**不被“信任”**，需要知道用户的凭证才能获得访问令牌。**在令牌交换中，客户端应用程序被信任**已经从用户那里获得了令牌，**授权服务器将验证该令牌并签发新令牌。**

#### 令牌发行者和令牌交换服务

在令牌交换流程中，“授权服务器”现在有两个参与者：

1. 令牌发行者(subject_token)：向客户端应用程序颁发主题令牌。
2. 令牌交换服务：验证主题令牌并向客户端应用程序颁发新令牌。

令牌交换服务与授权码流程中的“授权服务器”相同，令牌发行者可以是第三方身份提供者，或从“授权服务器”拆分为专用服务。

#### 何时使用令牌交换？

令牌交换流程可以在无用户交互的情况下进行，这在以下场景中很有用：

- **模拟：** 允许服务（例如，后台微服务）或管理员用户在不暴露完整用户凭据的情况下代表用户执行操作。
- **自动化：** 允许受信任的服务自动执行操作，无需人工干预，或执行自动化测试。
- **与其他 IdP 的集成：** 在不同的身份系统之间翻译令牌，以保持无缝的用户体验并有效管理权限，一个常见场景是将 SSO 令牌翻译为下游服务的令牌。
- **迁移：** 将令牌从一个授权服务器迁移到另一个，例如，从遗留系统迁移到现代 OAuth/OIDC 兼容系统。

#### 令牌交换请求

客户端向授权服务器的令牌端点发送请求以交换现有令牌。这包括 `subject_token`（正在交换的令牌）以及可选的目标受众、范围和令牌类型。

1. `grant_type`: 必须。此参数的值必须是 `urn:ietf:params:oauth:grant-type:token-exchange`，表示正在执行令牌交换。
2. `subject_token`: 必须。用户的 PAT。
3. `subject_token_type`: 必须。`subject_token` 参数中提供的安全令牌的类型。此参数的一个常见值是 `urn:ietf:params:oauth:token-type:access_token`，但可以根据正在交换的令牌而有所不同。
4. `resource`: 可选。资源指示器，用于指定访问令牌的目标资源。
5. `audience`: 可选。访问令牌的受众，指明令牌的预期接收者，如果未指定 `audience`，授权服务器可能会使用 `resource` 的值。
6. `scope`: 可选。请求的范围。

```shell
curl -X POST 'http://localhost:48080/oauth2/token' \
    -H 'Authorization: Basic and0LWNsaWVudDpwYXNzd29yZA==' \
    -H 'Content-Type: application/x-www-form-urlencoded' \
    -H 'Cookie: JSESSIONID=6F06D745B9FAEE81A852E81BC4E0F1AD' \
    --data-urlencode 'grant_type=urn:ietf:params:oauth:grant-type:token-exchange' \
    --data-urlencode 'audience=http://localhost:48080/admin-api/system/role/page' \
    --data-urlencode 'resource=http://localhost:48080/admin-api/system/role/page' \
    --data-urlencode 'scope=profile' \
    --data-urlencode 'requested_token_type=urn:ietf:params:oauth:token-type:jwt' \
    --data-urlencode 'subject_token=eyJraWQiOiJyb290LWNyZWVkLW1hbGwiLCJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJldGhhbiIsImF1ZCI6Imp3dC1jbGllbnQiLCJuYmYiOjE3MzUyNzAxMjksInNjb3BlIjpbIm1lc3NhZ2UucmVhZCIsIm1lc3NhZ2Uud3JpdGUiXSwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo0ODA4MCIsImV4cCI6MTczNTM1NjUyOSwiaWF0IjoxNzM1MjcwMTI5LCJ1dWlkIjoiNDRiZmU5NzktNDAzYi00ZmI3LWJjOTItNzFjZDk5NjNmM2I2IiwianRpIjoiMmU2ZjljZDktNjY1YS00M2IxLTg2ODEtYTNlMzA4MDEyZjJiIn0.WebQVRBoBVq8HfY2EIBXUbbC4XjuFewsWrqDwhTr03kFuFaJmJ99UPZQcF3HdSJ3g9msnNCrAt81A0qNzIS_HZf07FfwzKq5PRDdcSQvQJuodl2A-6qLatbxR0GVUTTaMhJN_3GxFlt-u1sJFdsakI3dULlv2zpY5_QZ_6kidZAmwdmiKIbsTMY6-bT1VfvLAUnzc0Z7s8DCxPZtrFLP_hhmjaCnchSmOYYZMdoSM01cJWu3k5qY9ou1fmX2SNRRH4HZERYF8PcSePTbtWegNmGKmIRj1ETa543QCwm4WI_l5Sclsf5Sf4FQumdIhXCR1U-ihKUxzl2axP0duW4OHg' \
    --data-urlencode 'subject_token_type=urn:ietf:params:oauth:token-type:jwt' \
    --data-urlencode 'actor_token_type=urn:ietf:params:oauth:token-type:jwt' \
    --data-urlencode 'actor_token=eyJraWQiOiJyb290LWNyZWVkLW1hbGwiLCJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJldGhhbiIsImF1ZCI6Imp3dC1jbGllbnQiLCJuYmYiOjE3MzUyNzAxMjksInNjb3BlIjpbIm1lc3NhZ2UucmVhZCIsIm1lc3NhZ2Uud3JpdGUiXSwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo0ODA4MCIsImV4cCI6MTczNTM1NjUyOSwiaWF0IjoxNzM1MjcwMTI5LCJ1dWlkIjoiNDRiZmU5NzktNDAzYi00ZmI3LWJjOTItNzFjZDk5NjNmM2I2IiwianRpIjoiMmU2ZjljZDktNjY1YS00M2IxLTg2ODEtYTNlMzA4MDEyZjJiIn0.WebQVRBoBVq8HfY2EIBXUbbC4XjuFewsWrqDwhTr03kFuFaJmJ99UPZQcF3HdSJ3g9msnNCrAt81A0qNzIS_HZf07FfwzKq5PRDdcSQvQJuodl2A-6qLatbxR0GVUTTaMhJN_3GxFlt-u1sJFdsakI3dULlv2zpY5_QZ_6kidZAmwdmiKIbsTMY6-bT1VfvLAUnzc0Z7s8DCxPZtrFLP_hhmjaCnchSmOYYZMdoSM01cJWu3k5qY9ou1fmX2SNRRH4HZERYF8PcSePTbtWegNmGKmIRj1ETa543QCwm4WI_l5Sclsf5Sf4FQumdIhXCR1U-ihKUxzl2axP0duW4OHg'
    
# 返回response
{
    "access_token": "eyJraWQiOiJyb290LWNyZWVkLW1hbGwiLCJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJldGhhbiIsImF1ZCI6Imp3dC1jbGllbnQiLCJuYmYiOjE3MzUyODI2NDMsImFjdCI6eyJpc3MiOiJodHRwOi8vbG9jYWxob3N0OjQ4MDgwIiwic3ViIjoiZXRoYW4ifSwic2NvcGUiOlsicHJvZmlsZSJdLCJpc3MiOiJodHRwOi8vbG9jYWxob3N0OjQ4MDgwIiwiZXhwIjoxNzM1MzY5MDQzLCJpYXQiOjE3MzUyODI2NDMsInV1aWQiOiI3ZWY2ZWVkNy0zZmQ4LTRhZWYtYWE2Ny05ZWRkMGFmNjQyNGMiLCJqdGkiOiI4NTUzMjYzMC1mZGI1LTRlZmItOGVjYi0yYjY4MTg0NDQ1OTQifQ.UIWViNCisF8TSd34D3kolBsOFm5YzndIx_EwV3cKNOMSKeQAkT6c6arDlMpkwg12rnuimVTpewQecfrRlB5SPRV4_uUMa2MGq62jpBnA68FSpfp4DpJzaqIzHnRAyUUZwlFdb8RYPqRFnOqkSef1NVXoCaR-LTAoT5dNE6ieiuSVDOkYLdmfV28PAB9VqdwG6RpMKxyIaq7CwJ4-Z4kAl7NsCr1zynN1OvPK3gkqOhOnL5DpaY4DWDop5kiBccC5fICMhPSOfGnbpHXYZUkaBM-YJOLRrB9yWH4ks0NqNjF3OVSJDxpMwVRyabJ8CeU6Bx2CAXa17KuYrq-2DVl4Iw",
    "issued_token_type": "urn:ietf:params:oauth:token-type:jwt",
    "scope": "profile",
    "token_type": "Bearer",
    "expires_in": 86399
}
```



Spring boot支持生成的Token类型有：

- random-token

  这种类型会生成随机字符串，不是JWT类型的token。

- jwt-token

  这种类型默认使用**NimbusJwt**。nimbus-jose-jwt是基于Apache2.0开源协议的JWT开源库，支持所有的签名(JWS)和加密(JWE)算法。



## 秘钥凭证 （Proof Key）在OAuth 客户端 Code Exchange 阶段的应用

基于授权码（`Authorization code grant`）技术的 OAuth 2.0 客户端容易受到授权码被劫持（interception attack）的风险。本篇文章基于 [RFC7636](https://datatracker.ietf.org/doc/html/rfc7636) 主要介绍了OAuth 2.0 客户端在 `code exchange` （Authz code exchange access token）阶段如何使用秘钥凭证来减缓这种攻击。

### 授权码拦截攻击

在 OAuth 2.0 协议 [RFC6749](https://datatracker.ietf.org/doc/html/rfc6749) 中，授权码是通过认证/授权服务器返回到重定向的 URI的，并且这个返回的路径是不受 TLS 保护的，这个重定向 URI 可以指向某个 URL 或者是客户端系统内的某个应用程序。

一旦攻击者获取到这个 `Authz code`， 就能够利用它去交换得到一个 `Access token`。有了这个 token，攻击者就能冒充合法的应用程序访问到服务器（`service provider`）中的资源了。

下图展示了攻击者如何进行授权码拦截的过程：

![authorization_code_intercept_attack](./imgs/authorization_code_intercept_attack.png)

**第一步**：运行在终端设备上的本地应用（legitimate OAuth 2.0 App）通过操作系统或者浏览器发送 OAuth 2.0 的授权请求（`Authz request`），一般 `redirect URI` 都是自定义的。

**第二、三步**：授权请求经过操作系统或者浏览器发往了授权服务器（`Authz server`）, 然后授权服务器返回了授权码（`Authz code`）。因为 OAuth 要求使用 TLS 作为传输的协议，所以这些请求都是受 TLS 保护的，不会收到中间人攻击。

**第四步**：通过步骤一提供的 URI 重定向的方式将 `Authz code` 返回给了这个 `redirect URI` 指向的地址。除了合法的 App 之外，恶意程序（malicious App）也有可能将自己注册为自定义的 redirect URI ，这样恶意程序就有可能拿到这个 Authz code。

### 授权码拦截的一些先决条件

1. Hack 需要将自己的恶意 App 安装到用户的终端设备， 还需要将自定义的 Redirect URI 注册到其他的 App， 也就是操作系统必须允许这种自定义的 URI scheme 能够跨 App 的注册。
2. Hack 需要能够拿到 OAuth 2.0 中的 `client_id` 和 `client_secret`。这里的 client_id 是基于设备的，也就是不同的设备类型（Mac/Windows/IOS/Android）使用不同的 client_id，client_secret。因为这种信息是在客户端保存的，这种在客户端保存的秘钥也不能被认为是绝对安全的。
3. Hack 能够监测到授权服务器的 response。

### PKCE （Proof Key for Code Exchange）流程

为了降低授权码被拦截的风险，这个协议引入了 `code verifier`（授权码验证器）的概念，它实际就是一个动态创建的、加密的随机秘钥。并且每个 `Authz request` 都会创建唯一的一个 code verifier, 并将其转换后的值 `code challenge` 发送到 `Authz server` 以获取 `Authz code`（见下图的步骤 A、B）。

然后，将这个获得的 Authz code 以及之前的 code verifier 一起发送到 Authz server，server 会将 code verifier 进行 `hash` 运算以及 `base64 URL` 编码，将得到的结果跟之前收到的 code challenge （之前 Authz request 携带过来的）进行比较。如果值相等则表示这个 Authz code 是有效的，会返回 access token，否则被认为是非法的，会返回无效授权的错误（见下图的步骤 C、D）。

PKCE 协议能够有效的降低授权码被劫持的风险，因为攻击者不知道这个一次性密钥，`Authz request` 和 `access token request` 都是通过 `TLS` 发送的并且无法被拦截。

![authorization_code_intercept_attack_2](./imgs/authorization_code_intercept_attack_2.png)

### PKCE （Proof Key for Code Exchange）协议详解

- Client 创建一个 Code Verifier

  Client 会为每个 Authz request 创建一个 `coder verifier`，它是由一串随机字符串组成的。这个 code verifier 应该有足够的高的熵（`entropy`）或者说足够高的随机性，这能够否防止 code verifier 被 hack 轻易的破解。

- Client 创建对应的 Code Challenge

  Client 通过之前的 code verifier 创建对应的 `code challenge`, 创建规则如下：

  ```
  [Plain]
  code_challenge = code_verifier
  
  [S256]
  code_challenge = BASE64URL-ENCODE(SHA256(code_verifier))
  ```

  如果 client 端能够支持 `S256`，就必须使用这种转换规则，并且这种规则在服务器端也是强制要求要能够支持的。而 client 端使用 `plain` 转换规则的前提是当且仅当 client 端由于某些技术原因不能支持 S256。

- Client 通过 Authz request 发送 Code Challenge

  Client 将 `code challenge` 作为 Authz request 的参数发送给 server。

  ```
  [code_challenge]
  必需填写项
  
  [code_challenge_method]
  可选项，默认是使用 plain，code verifier 转换的方法可以是 S256 或者 plain。
  ```

  

- Server 返回 Authz Code

  当 server 在 Authz response 中返回 Authz code 的时候，server 必须要将 code_challenge 跟这个 Authz code 进行关联，以便在下一个 access token request 中能够验证 code verifier 的有效性。

- Client 发送 Authz Code

  Client 发送 Access token request 给 server, 除了 OAuth 2.0 需要的一些必要参数外，还需要额外的参数：`code_verifier`。

- Server 在返回 Access Token 之前验证 coder verifier

  Server 会基于 code_verifier 做转换，然后将转换出的结果跟之前关联的 code_challenge 作比较。具体的转换规则依赖于 `code_challenge_method` 属性。

  ```
  BASE64URL-ENCODE(SHA256(code_verifier)) == code_challenge
  ```

  如果这个属性是 S256，code_verifier 需要做 SHA-256 的 hash, 然后是 base64url 的编码。如果这个属性是 plain，code_verifier 直接跟 code_challenge 比较。

  ```
  code_verifier == code_challenge
  ```

  如果比较的值相等，Authz server 会返回 Access token 给 client 端， 如果不相等，则返回一个 error message 表示这是一个无效的授权（ invalid grant）。

### 关于安全性的考虑

#### code_verifier 的熵

这个 PKCE 安全模型需要依赖于 code verifier 是否容易被 hack 学习或者猜到，坚持这一原则也是至关重要的。因此这个 code verifier 的创建必须是加密的并且具有足够高的随机性。

#### 防止窃听

客户端在尝试 `S256` 的规则后不得降级为 `plain` ，对于支持 PKCE 的服务器也必须要支持 S256，不支持 PKCE 的服务器将简单地忽略未知的 code_verifier 。 因此在出现 S256 时的错误只能表示 server 出现故障或者攻击者正在尝试降级攻击，而并不意味着 server 不支持 S256。

#### code_challenge 的 Salt

为了降低实现的复杂度，在产生 code challenge 过程中不需要使用 salting，因为 code verifier 已经包含了足够的随机性来避免被攻击。

## curl 命令参数解释

curl 是常用的命令行工具，用来请求 Web 服务器。它的名字就是客户端（client）的 URL 工具的意思。

它的功能非常强大，命令行参数多达几十种。如果熟练的话，完全可以取代 Postman 这一类的图形界面工具。[官方文档](https://curl.se/docs/manpage.html)

### -b

`-b`参数用来向服务器发送 Cookie。

```bash
$ curl -b 'foo=bar' https://google.com
```

上面命令会生成一个标头`Cookie: foo=bar`，向服务器发送一个名为`foo`、值为`bar`的 Cookie。

```bash
$ curl -b 'foo1=bar;foo2=bar2' https://google.com
```

上面命令发送两个 Cookie。

---

### **--basic**

(HTTP) Use HTTP Basic authentication with the remote host. This method is the default and this option is usually pointless, unless you use it to override a previously set option that sets a different authentication method (such as [--ntlm](https://curl.se/docs/manpage.html#--ntlm), [--digest](https://curl.se/docs/manpage.html#--digest), or [--negotiate](https://curl.se/docs/manpage.html#--negotiate)).

Used together with [-u, --user](https://curl.se/docs/manpage.html#-u).

Providing --basic multiple times has no extra effect.

Example:

```
curl -u name:password --basic https://example.com
```

---

### **-H**

`-H`参数添加 HTTP 请求的标头。

```bash
$ curl -d '{"login": "emma", "pass": "123"}' -H 'Content-Type: application/json' https://google.com/login
```

上面命令添加 HTTP 请求的标头是`Content-Type: application/json`，然后用`-d`参数发送 JSON 数据。

---

### **-d, --data**

`-d`参数用于发送 **POST** 请求的数据体。

```bash
$ curl -d'login=emma＆password=123'-X POST https://google.com/login
# 或者
$ curl -d 'login=emma' -d 'password=123' -X POST  https://google.com/login
```

使用`-d`参数以后，HTTP 请求会自动加上标头`Content-Type : application/x-www-form-urlencoded`。并且会自动将请求转为 **POST** 方法，因此可以省略`-X POST`。

`-d`参数可以读取本地文本文件的数据，向服务器发送。

```bash
$ curl -d '@data.txt' https://google.com/login
```

上面命令读取`data.txt`文件的内容，作为数据体向服务器发送。

`--data-raw` is almost the same but does not have a special interpretation of the `@` character. To post data purely binary, you should instead use the `--data-binary` option. 

To URL-encode the value of a form field you may use `--data-urlencode`.

---

### **-i**, **--show-headers**

`-i`参数打印出服务器回应的 HTTP 标头。

```bash
$ curl -i https://www.example.com
```

上面命令收到服务器回应后，先输出服务器回应的标头，然后空一行，再**输出网页的源码**。

---

### **-I**, **--head**

`-I`参数向服务器发出 HEAD 请求，然会将服务器返回的 HTTP 标头打印出来。

```bash
$ curl -I https://www.example.com

$ curl --head https://www.example.com
```

上面命令输出服务器对 HEAD 请求的回应。

---

### **-k**

`-k`参数指定跳过 SSL 检测。

```bash
$ curl -k https://www.example.com
```

上面命令不会检查服务器的 SSL 证书是否正确。

---

### **-o**

`-o`参数将服务器的回应保存成文件，等同于`wget`命令。

```bash
$ curl -o example.html https://www.example.com
```

上面命令将`www.example.com`保存成`example.html`。

---

### **-O**

`-O`参数将服务器回应保存成文件，并将 URL 的最后部分当作文件名。

```bash
$ curl -O https://www.example.com/foo/bar.html
```

上面命令将服务器回应保存成文件，文件名为`bar.html`。

---

### -L, --location

如果服务器报告请求的页面已移动到其他位置（用location:header和3xx 响应代码），此选项将使curl在新位置上重新执行请求。









---



[SHA256 Online]: https://emn178.github.io/online-tools/sha256.html

## GenerateCodeVerifierAndCodeChallenge 

[GoBack](#目前Spring oAuth2支持以下几种授权模式：)

```javascript
const characters = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-._~';
let randomString = '';
const length = Math.floor(Math.random()) + 43
for (let i = 0; i < length; i++) {
    randomString += characters.charAt(Math.floor(Math.random() * characters.length));
}
console.log('randomString', randomString)
function base64UrlEncode(str) {
    return str
        .replace(/\+/g, '-')
        .replace(/\//g, '_')
        .replace(/=+$/, '');
}
const msgUint8 = new TextEncoder().encode(randomString);
const hashBuffer = await window.crypto.subtle.digest("SHA-256", msgUint8); 
const encodedStr =base64UrlEncode(btoa(String.fromCharCode.apply(null, new Uint8Array(hashBuffer))))
console.log('encodedStr', encodedStr)


function base64UrlDecode(str) {
    str = str
        .replace(/-/g, '+')
        .replace(/_/g, '/');
    while (str.length % 4) {
        str += '=';
    }
    return atob(str);
}  
```



[GenerateCodeVerifierAndCodeChallenge]: #GenerateCodeVerifierAndCodeChallenge