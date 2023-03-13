package com.ethan.security.oauth2.config;

import com.ethan.security.oauth2.provider.JpaOAuth2AuthorizationConsentService;
import com.ethan.security.oauth2.provider.JpaOAuth2AuthorizationService;
import com.ethan.security.oauth2.provider.JpaRegisteredClientRepository;
import com.ethan.security.oauth2.repository.CreedOAuth2AuthorizationConsentRepository;
import com.ethan.security.oauth2.repository.CreedOAuth2AuthorizationRepository;
import com.ethan.security.oauth2.repository.CreedOAuth2RegisteredClientRepository;
import com.ethan.security.provider.UnAuthExceptionHandler;
import com.ethan.security.websecurity.filter.LoginTokenAuthenticationFilter;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AnonymousAuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.authentication.ClientSecretAuthenticationProvider;
import org.springframework.security.oauth2.server.authorization.authentication.JwtClientAssertionAuthenticationProvider;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationCodeAuthenticationProvider;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationCodeRequestAuthenticationContext;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationCodeRequestAuthenticationProvider;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationCodeRequestAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationCodeRequestAuthenticationValidator;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationConsentAuthenticationProvider;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientCredentialsAuthenticationProvider;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2RefreshTokenAuthenticationProvider;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2TokenIntrospectionAuthenticationProvider;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2TokenRevocationAuthenticationProvider;
import org.springframework.security.oauth2.server.authorization.authentication.PublicClientAuthenticationProvider;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2TokenEndpointConfigurer;
import org.springframework.security.oauth2.server.authorization.oidc.authentication.OidcUserInfoAuthenticationProvider;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.web.OAuth2AuthorizationEndpointFilter;
import org.springframework.security.oauth2.server.authorization.web.OAuth2ClientAuthenticationFilter;
import org.springframework.security.oauth2.server.authorization.web.authentication.ClientSecretBasicAuthenticationConverter;
import org.springframework.security.oauth2.server.authorization.web.authentication.ClientSecretPostAuthenticationConverter;
import org.springframework.security.oauth2.server.authorization.web.authentication.JwtClientAssertionAuthenticationConverter;
import org.springframework.security.oauth2.server.authorization.web.authentication.OAuth2AuthorizationCodeRequestAuthenticationConverter;
import org.springframework.security.oauth2.server.authorization.web.authentication.OAuth2AuthorizationConsentAuthenticationConverter;
import org.springframework.security.oauth2.server.authorization.web.authentication.PublicClientAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;

@Configuration
// @Import(OAuth2AuthorizationServerConfiguration.class)
@EnableWebSecurity
public class AuthorizationServerConfig {
    @Resource
    private LoginTokenAuthenticationFilter loginTokenAuthenticationFilter;
    @Resource
    private UnAuthExceptionHandler exceptionHandler;

    /**
     *    {@link HttpSecurity#performBuild()}会绑定 requestMatcher 和 List<Filter> sortedFilters, 生成 {@link FilterChainProxy.filterChains}
     *
     *
     * 下面来看看这边的配置
     * {@link OAuth2AuthorizationServerConfiguration#}
     * {@link OAuth2AuthorizationServerConfigurer#init(HttpSecurity)}
     *      中 this.configurers.values()会注册endpoint
     *
     *      ↓然后下面代码会进行注册↓
     *      		List<RequestMatcher> requestMatchers = new ArrayList<>();
     * 		this.configurers.values().forEach(configurer -> {
     * 			configurer.init(httpSecurity);
     * 			requestMatchers.add(configurer.getRequestMatcher());
     *                });
     * 		requestMatchers.add(new AntPathRequestMatcher(
     * 				authorizationServerSettings.getJwkSetEndpoint(), HttpMethod.GET.name()));
     * 		this.endpointsMatcher = new OrRequestMatcher(requestMatchers);
     *
     *  1. client credentials {@link OAuth2TokenEndpointConfigurer#createDefaultAuthenticationProviders(HttpSecurity)}
     *      OAuth2TokenGenerator<? extends OAuth2Token> tokenGenerator = OAuth2ConfigurerUtils.getTokenGenerator(httpSecurity); 会尝试从JWK中获取Bean {@link org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2ConfigurerUtils#getJwtEncoder(HttpSecurity)} -> {@link NimbusJwtEncoder}
     *
     *  2. authorization_code
     *     a. 生成code
     *          // http://localhost:8081/oauth2/authorize?client_id=messaging-client&redirect_uri=http://127.0.0.1:8080/authorized&response_type=code&scope=message.read message.write
     *       {@link OAuth2AuthorizationEndpointFilter} 初始化的时候注入
     *       		this.authenticationConverter = new DelegatingAuthenticationConverter(
     * 				Arrays.asList(
     * 						new OAuth2AuthorizationCodeRequestAuthenticationConverter(),
     * 						new OAuth2AuthorizationConsentAuthenticationConverter()));
     *       i. 首先转换 Authentication authorizationGrantAuthentication = this.authenticationConverter.convert(request);
     *          - {@link OAuth2AuthorizationCodeRequestAuthenticationConverter} -> {@link OAuth2AuthorizationCodeRequestAuthenticationToken}
     *          - {@link OAuth2AuthorizationConsentAuthenticationConverter}
     *       ii. this.authenticationManager.authenticate(authorizationGrantAuthentication) which is {@link ProviderManager}, 每个provider都会匹配Token类型
     *          - {@link AnonymousAuthenticationProvider}
     *          - {@link JwtClientAssertionAuthenticationProvider}
     *          - {@link ClientSecretAuthenticationProvider}
     *          - {@link PublicClientAuthenticationProvider}
     *          - {@link OAuth2AuthorizationCodeRequestAuthenticationProvider} ->
     *              1. {@link OAuth2AuthorizationCodeRequestAuthenticationValidator#accept(OAuth2AuthorizationCodeRequestAuthenticationContext)}
     *              2. // code_challenge (REQUIRED for public clients) - RFC 7636 (PKCE)
     *              3. isPrincipalAuthenticated(principal) ==> The request is valid - ensure the resource owner is authenticated
     *                 因为此时 authorizationCodeRequestAuthentication.getPrincipal() 还是 annoynous,所以会退出
     *                 Authentication principal = SecurityContextHolder.getContext().getAuthentication();
 *  		                if (principal == null) {
 *			                principal = ANONYMOUS_AUTHENTICATION;
     *                }
     *
     *              4. requireAuthorizationConsent(registeredClient, authorizationRequest, currentAuthorizationConsent) 会检查是否有OAuth2AuthorizationConsentAuthenticationToken，
     *                 如果没有会返回 OAuth2AuthorizationConsentAuthenticationToken 并且{@link OAuth2AuthorizationEndpointFilter#sendAuthorizationConsent())}跳转到consent页面进行确认
     *
     *                 - Consent按钮的作用
     *                      会调用 /oauth2/authorize === client_id=messaging-client&state=27beO1YP7pANBTHjkts92Xyh_ZQvRF-TC2BrKHDeaes%3D&scope=message.read&scope=message.write
     *                      i.通过 {@link OAuth2AuthorizationConsentAuthenticationConverter} 生成 OAuth2AuthorizationConsentAuthenticationToken
     *                      ii. 通过匹配OAuth2AuthorizationConsentAuthenticationToken 进入 {@link OAuth2AuthorizationConsentAuthenticationProvider}
     *                          其中 this.authorizationConsentService.save(authorizationConsent) 会存入consent
     *                          然后通过 {@link OAuth2AuthorizationConsentAuthenticationProvider} 存入数据，生成code
     *                          最后通过 {@link OAuth2AuthorizationEndpointFilter#setAuthenticationSuccessHandler(AuthenticationSuccessHandler)}转发url
     *
     *          - {@link OAuth2AuthorizationConsentAuthenticationProvider}
     *          - {@link OAuth2AuthorizationCodeAuthenticationProvider}
     *          - {@link OAuth2RefreshTokenAuthenticationProvider}
     *          - {@link OAuth2ClientCredentialsAuthenticationProvider}
     *          - {@link OAuth2TokenIntrospectionAuthenticationProvider}
     *          - {@link OAuth2TokenRevocationAuthenticationProvider}
     *          - {@link OidcUserInfoAuthenticationProvider}
     *          - {@link JwtAuthenticationProvider}
     *
     *      iii. {@link OAuth2AuthorizationEndpointFilter#sendAuthorizationResponse}
     *
     *           并没有设置 securityContext.setAuthentication(authentication); 未登录则会转发到login
     *     b. 请求access_token
     *         // http://localhost:8081/oauth2/token
     *
     *         // x-www-form-urlencoded
     *         // grant_type:authorization_code
     *         // code:kVUvIpnNM50oxve4a7Gs3jZgkTEP291-utCt4NULxyy82aMRjgGs6rPZ3XEizrs18LYxprNbCw7XVxZCmzAOCojmTm3ZVqDLsbBOv7g-1fPqOVCq0KQVDZZ79vJqklMv
     *         // redirect_uri:http://127.0.0.1:8080/authorized
     *
     *       {@link OAuth2ClientAuthenticationFilter}
     *       i. 首先转换 Authentication authorizationGrantAuthentication = this.authenticationConverter.convert(request);， list注册在 {@link #registeredClientRepository}
     *          - {@link JwtClientAssertionAuthenticationConverter}
     *          - {@link ClientSecretBasicAuthenticationConverter}
     *          - {@link ClientSecretPostAuthenticationConverter}
     *          - {@link PublicClientAuthenticationConverter}
     *         获取 OAuth2ClientAuthenticationToken
     *
     *       ii. this.authenticationManager.authenticate(authorizationGrantAuthentication) which is {@link ProviderManager}
     *          - {@link AnonymousAuthenticationProvider}
     *          - {@link JwtClientAssertionAuthenticationProvider}
     *          - {@link ClientSecretAuthenticationProvider}
     *          - {@link PublicClientAuthenticationProvider}
     *          - {@link OAuth2AuthorizationCodeRequestAuthenticationProvider}
     *          - {@link OAuth2AuthorizationConsentAuthenticationProvider}
     *          - {@link OAuth2AuthorizationCodeAuthenticationProvider}
     *              // 此处可以 检查 获取token -> authorization_code_value -> "metadata.token.invalidated":true(检查code是否被使用)
     *
     *
     *          - {@link OAuth2RefreshTokenAuthenticationProvider}
     *          - {@link OAuth2ClientCredentialsAuthenticationProvider}
     *          - {@link OAuth2TokenIntrospectionAuthenticationProvider}
     *          - {@link OAuth2TokenRevocationAuthenticationProvider}
     *          - {@link OidcUserInfoAuthenticationProvider}
     *          - {@link JwtAuthenticationProvider}
     *      iii. this.authenticationSuccessHandler.onAuthenticationSuccess(request, response, authenticationResult); 会设置全局login authentication
     *             其中 AuthenticationSuccessHandler authenticationSuccessHandler = (request, response, authentication) -> onAuthenticationSuccess(request, response, authentication); = this::onAuthenticationSuccess
     *
     *
     *
     *  bearer token 验证
     * {@link  OAuth2ClientAuthenticationFilter}
     * {@link  BearerTokenAuthenticationFilter}
     */
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http)
            throws Exception {
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);
        http.getConfigurer(OAuth2AuthorizationServerConfigurer.class)
                .oidc(Customizer.withDefaults());// Enable OpenID Connect 1.0
        http
                // Redirect to the login page when not authenticated from the
                // authorization endpoint
                .exceptionHandling((exceptions) -> exceptions
                        .authenticationEntryPoint(
                                new LoginUrlAuthenticationEntryPoint("/login"))
                        // .authenticationEntryPoint(exceptionHandler)
                        // .accessDeniedHandler(exceptionHandler)
                        // .exceptionHandling()
                        // .accessDeniedHandler(exceptionHandler())
                        // .authenticationEntryPoint(exceptionHandler())
                )

                // Accept access tokens for User Info and/or Client Registration
                .addFilterAfter(loginTokenAuthenticationFilter, AnonymousAuthenticationFilter.class)
                // .oauth2ResourceServer(OAuth2ResourceServerConfigurer::opaqueToken)
                // .oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt)
                // add api validation by token
                // .securityMatcher(additionalRequestMatcher)

        // .authorizeHttpRequests((authorizeRequests) -> {
        //     authorizeRequests
        //             .requestMatchers("/api/**")
        //             .authenticated()
        //             .anyRequest().authenticated()
        //     // .and()
        //     // .securityMatchers((matchers) -> matchers.requestMatchers("/api/**"))
        //     ;
        // })


        // .oauth2ResourceServer(oauth2 -> {
        //     oauth2.jwt()
        // })
        ;
        return http.build();

        /** 备份添加额外api验证
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);
        OAuth2AuthorizationServerConfigurer serverConfigurer = http.getConfigurer(OAuth2AuthorizationServerConfigurer.class)
                // .authorizationConsentService()
                .oidc(Customizer.withDefaults());// Enable OpenID Connect 1.0

        RequestMatcher endpointsMatcher = serverConfigurer.getEndpointsMatcher();
        RequestMatcher additionalRequestMatcher = new OrRequestMatcher(new AntPathRequestMatcher("/api/**"), endpointsMatcher);

        // DefaultBearerTokenResolver resolver = new DefaultBearerTokenResolver();
        // resolver.setAllowFormEncodedBodyParameter(true);

        http
                // Redirect to the login page when not authenticated from the
                // authorization endpoint
                .exceptionHandling((exceptions) -> exceptions
                        .authenticationEntryPoint(
                                new LoginUrlAuthenticationEntryPoint("/login"))
                )

                // Accept access tokens for User Info and/or Client Registration
                .addFilterAfter(loginTokenAuthenticationFilter, AnonymousAuthenticationFilter.class)
                // .oauth2ResourceServer(OAuth2ResourceServerConfigurer::opaqueToken)
                // .oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt)
                // add api validation by token
                .securityMatcher(additionalRequestMatcher)

                // .authorizeHttpRequests((authorizeRequests) -> {
                //     authorizeRequests
                //             .requestMatchers("/api/**")
                //             .authenticated()
                //             .anyRequest().authenticated()
                //     // .and()
                //     // .securityMatchers((matchers) -> matchers.requestMatchers("/api/**"))
                //     ;
                // })


                // .oauth2ResourceServer(oauth2 -> {
                //     oauth2.jwt()
                // })

        ;


        return http.build(); */
    }

/*     @Bean
    public OAuth2TokenGenerator accessTokenGenerator() {
        return new OAuth2AccessTokenGenerator();
    } */

    private void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                         Authentication authentication) {

        org.springframework.security.core.context.SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);
    }


    /**
     * ClientAuthenticationMethod.CLIENT_SECRET_BASIC ==> {@link ClientSecretBasicAuthenticationConverter}
     * ClientAuthenticationMethod.CLIENT_SECRET_POST ==> {@link ClientSecretPostAuthenticationConverter}
     *  -> {@link OAuth2AuthorizationCodeAuthenticationProvider}
     * @param jdbcTemplate
     * @return
     */
    @Bean
    public RegisteredClientRepository registeredClientRepository(CreedOAuth2RegisteredClientRepository clientRepository) {

        // RegisteredClient.withId("")
        //         .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST);
/*         RegisteredClient registeredClient = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId("messaging-client")
                .clientSecret("{noop}secret")
                // .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                .redirectUri("http://127.0.0.1:8080/login/oauth2/code/messaging-client-oidc")
                .redirectUri("http://127.0.0.1:8080/authorized")
                .scope(OidcScopes.OPENID)
                .scope(OidcScopes.PROFILE)
                .scope("message.read")
                .scope("message.write")
                .clientSettings(ClientSettings.builder().requireAuthorizationConsent(true).build())
                .build();
        // return new InMemoryRegisteredClientRepository(registeredClient);
        JdbcRegisteredClientRepository registeredClientRepository = new JdbcRegisteredClientRepository(jdbcTemplate);
        // registeredClientRepository.save(registeredClient);
        return registeredClientRepository; */
        return new JpaRegisteredClientRepository(clientRepository);
    }
    @Bean
    public JWKSource<SecurityContext> jwkSource() {
        KeyPair keyPair = generateRsaKey();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        RSAKey rsaKey = new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID(UUID.randomUUID().toString())
                .build();
        JWKSet jwkSet = new JWKSet(rsaKey);
        return new ImmutableJWKSet<>(jwkSet);
    }
    private static KeyPair generateRsaKey() {
        KeyPair keyPair;
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            keyPair = keyPairGenerator.generateKeyPair();
        }
        catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
        return keyPair;
    }
    @Bean
    public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
    }

    @Bean
    public OAuth2AuthorizationService authorizationService(CreedOAuth2AuthorizationRepository authorizationRepository, RegisteredClientRepository registeredClientRepository) {
        /* return new JdbcOAuth2AuthorizationService(jdbcTemplate, registeredClientRepository); */
        return new JpaOAuth2AuthorizationService(authorizationRepository, registeredClientRepository);
    }
    @Bean
    public OAuth2AuthorizationConsentService authorizationConsentService(CreedOAuth2AuthorizationConsentRepository authorizationConsentRepository, RegisteredClientRepository registeredClientRepository) {
        /* return new JdbcOAuth2AuthorizationConsentService(jdbcTemplate, registeredClientRepository); */
        return new JpaOAuth2AuthorizationConsentService(authorizationConsentRepository, registeredClientRepository);
    }

    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder().build();
    }

    /* @Bean
    public EmbeddedDatabase embeddedDatabase() {
        // @formatter:off
        return new EmbeddedDatabaseBuilder()
                .generateUniqueName(true)
                .setType(EmbeddedDatabaseType.H2)
                .setScriptEncoding("UTF-8")
                .addScript("org/springframework/security/oauth2/server/authorization/oauth2-authorization-schema.sql")
                .addScript("org/springframework/security/oauth2/server/authorization/oauth2-authorization-consent-schema.sql")
                .addScript("org/springframework/security/oauth2/server/authorization/client/oauth2-registered-client-schema.sql")
                .build();
        // @formatter:on
    } */

}
