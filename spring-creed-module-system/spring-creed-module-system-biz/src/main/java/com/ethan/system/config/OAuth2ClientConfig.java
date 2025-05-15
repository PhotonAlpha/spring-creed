/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */

package com.ethan.system.config;

import com.ethan.system.dal.registration.client.JpaClientRegistrationRepository;
import com.ethan.system.dal.registration.client.JpaOAuth2AuthorizedClientService;
import com.ethan.system.dal.repository.oauth2.client.CreedOAuth2AuthorizedClientRepository;
import com.ethan.system.dal.repository.oauth2.client.CreedOAuth2ClientConfigurationRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.security.oauth2.client.JwtBearerOAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.http.OAuth2ErrorResponseErrorHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.AuthenticatedPrincipalOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.core.http.converter.OAuth2AccessTokenResponseHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

@Configuration
public class OAuth2ClientConfig {
    @Bean
    public RestTemplate clientRestTemplate() {
        RestTemplate restTemplate = new RestTemplate(Arrays.asList(
                new FormHttpMessageConverter(),
                new OAuth2AccessTokenResponseHttpMessageConverter()
        ));
        restTemplate.setErrorHandler(new OAuth2ErrorResponseErrorHandler());
        return restTemplate;
    }

    @Bean
    public OAuth2AuthorizedClientManager authorizedClientManager(
            ClientRegistrationRepository clientRegistrationRepository,
            OAuth2AuthorizedClientRepository authorizedClientRepository) {
        // see {@link OAuth2ClientConfiguration.OAuth2AuthorizedClientManagerRegistrar#postProcessBeanDefinitionRegistry}
        JwtBearerOAuth2AuthorizedClientProvider jwtBearerAuthorizedClientProvider = new JwtBearerOAuth2AuthorizedClientProvider();
        OAuth2AuthorizedClientProvider authorizedClientProvider =
                OAuth2AuthorizedClientProviderBuilder.builder()
                        .authorizationCode()
                        .refreshToken()
                        .clientCredentials()
                        .provider(jwtBearerAuthorizedClientProvider)
                        .build();

        DefaultOAuth2AuthorizedClientManager authorizedClientManager =
                new DefaultOAuth2AuthorizedClientManager(
                        clientRegistrationRepository, authorizedClientRepository);
        authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider);
        return authorizedClientManager;
    }

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository(CreedOAuth2ClientConfigurationRepository repo) {
        return new JpaClientRegistrationRepository(repo);
    }

    @Bean
    public OAuth2AuthorizedClientService auth2AuthorizedClientService(ClientRegistrationRepository clientRegistrationRepository, CreedOAuth2AuthorizedClientRepository authorizedClientRepository) {
        return new JpaOAuth2AuthorizedClientService(clientRegistrationRepository, authorizedClientRepository);
    }

    @Bean
    public OAuth2AuthorizedClientRepository auth2AuthorizedClientRepository(OAuth2AuthorizedClientService auth2AuthorizedClientService) {
        return new AuthenticatedPrincipalOAuth2AuthorizedClientRepository(auth2AuthorizedClientService);
    }
}
