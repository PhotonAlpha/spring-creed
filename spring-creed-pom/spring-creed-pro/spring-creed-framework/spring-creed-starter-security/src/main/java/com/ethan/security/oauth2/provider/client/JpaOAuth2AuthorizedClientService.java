/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */

package com.ethan.security.oauth2.provider.client;

import com.ethan.security.oauth2.entity.client.CreedOAuth2AuthorizedClient;
import com.ethan.security.oauth2.repository.client.CreedOAuth2AuthorizedClientRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientId;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

/**
 * TODO,  need to provide a service and intergrate with OAuth2ClientConfig
 * see {@link OAuth2TokenServiceImpl}
 */
public class JpaOAuth2AuthorizedClientService implements OAuth2AuthorizedClientService {
    private final CreedOAuth2AuthorizedClientRepository authorizedClientRepository;

    private final ClientRegistrationRepository clientRegistrationRepository;

    /**
     *
     * Constructs an {@code JpaOAuth2AuthorizedClientService} using the provided
     * parameters.
     * @param clientRegistrationRepository the repository of client registrations
     * @param authorizedClients the initial {@code Map} of authorized client(s) keyed by
     * {@link OAuth2AuthorizedClientId}
     * @since 5.2
     */
    public JpaOAuth2AuthorizedClientService(ClientRegistrationRepository clientRegistrationRepository,
                                            CreedOAuth2AuthorizedClientRepository authorizedClientRepository) {
        Assert.notNull(clientRegistrationRepository, "clientRegistrationRepository cannot be null");
        Assert.notNull(authorizedClientRepository, "authorizedClientRepository cannot be empty");
        this.clientRegistrationRepository = clientRegistrationRepository;
        this.authorizedClientRepository = authorizedClientRepository;
    }


    @Override
    public OAuth2AuthorizedClient loadAuthorizedClient(String clientRegistrationId, String principalName) {
        Assert.hasText(clientRegistrationId, "clientRegistrationId cannot be empty");
        Assert.hasText(principalName, "principalName cannot be empty");
        ClientRegistration registration = this.clientRegistrationRepository.findByRegistrationId(clientRegistrationId);
        if (registration == null) {
            throw new DataRetrievalFailureException(
                    "The ClientRegistration with id '" + clientRegistrationId + "' exists in the data source, "
                            + "however, it was not found in the ClientRegistrationRepository.");
        }
        Optional<CreedOAuth2AuthorizedClient> clientOptional = authorizedClientRepository.findByClientRegistrationIdAndPrincipalName(clientRegistrationId, principalName);
        if (clientOptional.isEmpty()) {
            return null;
        }
        CreedOAuth2AuthorizedClient authorizedClient = clientOptional.get();

        OAuth2AccessToken.TokenType tokenType = OAuth2AccessToken.TokenType.BEARER;
        if (OAuth2AccessToken.TokenType.BEARER.getValue().equalsIgnoreCase(authorizedClient.getAccessTokenType())) {
            tokenType = OAuth2AccessToken.TokenType.BEARER;
        }
        String tokenValue = authorizedClient.getAccessTokenValue();
        Instant issuedAt = authorizedClient.getAccessTokenIssuedAt();
        Instant expiresAt = authorizedClient.getAccessTokenExpiresAt();
        Set<String> scopes = authorizedClient.getAccessTokenScopes();

        OAuth2AccessToken accessToken = new OAuth2AccessToken(tokenType, tokenValue, issuedAt, expiresAt, scopes);

        OAuth2RefreshToken refreshToken = null;
        String refreshTokenValue = authorizedClient.getRefreshTokenValue();
        if (StringUtils.isNoneBlank(refreshTokenValue)) {
            Instant refreshTokenExpiresAt = authorizedClient.getRefreshTokenExpiresAt();
            Instant refreshTokenIssuedAt = authorizedClient.getRefreshTokenIssuedAt();
            refreshToken = new OAuth2RefreshToken(refreshTokenValue, refreshTokenIssuedAt, refreshTokenExpiresAt);
        }
        return new OAuth2AuthorizedClient(registration, principalName, accessToken, refreshToken);
    }

    @Override
    public void saveAuthorizedClient(OAuth2AuthorizedClient authorizedClient, Authentication principal) {
        Assert.notNull(authorizedClient, "authorizedClient cannot be null");
        Assert.notNull(principal, "principal cannot be null");
        boolean existsAuthorizedClient = null != this.loadAuthorizedClient(
                authorizedClient.getClientRegistration().getRegistrationId(), principal.getName());

        if (existsAuthorizedClient) {
            updateAuthorizedClient(authorizedClient, principal);
        } else {
            try {
                insertAuthorizedClient(authorizedClient, principal);
            }
            catch (DuplicateKeyException ex) {
                updateAuthorizedClient(authorizedClient, principal);
            }
        }
        // this.authorizedClients.put(new OAuth2AuthorizedClientId(
        //         authorizedClient.getClientRegistration().getRegistrationId(), principal.getName()), authorizedClient);
    }

    private void insertAuthorizedClient(OAuth2AuthorizedClient authorizedClient, Authentication principal) {
        CreedOAuth2AuthorizedClient client = new CreedOAuth2AuthorizedClient();

        OAuth2AccessToken accessToken = authorizedClient.getAccessToken();
        OAuth2RefreshToken refreshToken = authorizedClient.getRefreshToken();

        ClientRegistration clientRegistration = authorizedClient.getClientRegistration();
        client.setClientRegistrationId(clientRegistration.getRegistrationId());
        client.setPrincipalName(principal.getName());

        setTokenValues(accessToken,
                client::setAccessTokenType,
                client::setAccessTokenValue,
                client::setAccessTokenIssuedAt,
                client::setAccessTokenExpiresAt,
                client::setAccessTokenScopes
        );
        setRefreshTokenValues(refreshToken,
                client::setRefreshTokenValue,
                client::setRefreshTokenIssuedAt);
        authorizedClientRepository.save(client);
    }

    private void updateAuthorizedClient(OAuth2AuthorizedClient authorizedClient, Authentication principal) {
        ClientRegistration clientRegistration = authorizedClient.getClientRegistration();
        OAuth2AccessToken accessToken = authorizedClient.getAccessToken();
        OAuth2RefreshToken refreshToken = authorizedClient.getRefreshToken();

        String registrationId = clientRegistration.getRegistrationId();
        String principalName = principal.getName();

        Optional<CreedOAuth2AuthorizedClient> clientOptional = authorizedClientRepository.findByClientRegistrationIdAndPrincipalName(registrationId, principalName);
        clientOptional.ifPresent(client -> {
            setTokenValues(accessToken,
                    client::setAccessTokenType,
                    client::setAccessTokenValue,
                    client::setAccessTokenIssuedAt,
                    client::setAccessTokenExpiresAt,
                    client::setAccessTokenScopes
            );
            setRefreshTokenValues(refreshToken,
                    client::setRefreshTokenValue,
                    client::setRefreshTokenIssuedAt);
            authorizedClientRepository.save(client);
        });

    }

    private void setTokenValues(OAuth2AccessToken accessToken,
                           Consumer<String> tokenTypeConsumer,
                           Consumer<String> tokenValueConsumer,
                           Consumer<Instant> issuedAtConsumer,
                           Consumer<Instant> expiresAtConsumer,
                           Consumer<Set<String>> accessTokenScopesConsumer) {
        if (Objects.nonNull(accessToken)) {
            tokenTypeConsumer.accept(accessToken.getTokenType().getValue());
            tokenValueConsumer.accept(accessToken.getTokenValue());
            issuedAtConsumer.accept(accessToken.getIssuedAt());
            expiresAtConsumer.accept(accessToken.getExpiresAt());
            if (!CollectionUtils.isEmpty(accessToken.getScopes())) {
                accessTokenScopesConsumer.accept(accessToken.getScopes());
            }
        }
    }
    private void setRefreshTokenValues(OAuth2RefreshToken refreshToken,
                           Consumer<String> tokenValueConsumer,
                           Consumer<Instant> issuedAtConsumer) {
        if (Objects.nonNull(refreshToken)) {
            tokenValueConsumer.accept(refreshToken.getTokenValue());
            issuedAtConsumer.accept(refreshToken.getIssuedAt());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeAuthorizedClient(String clientRegistrationId, String principalName) {
        Assert.hasText(clientRegistrationId, "clientRegistrationId cannot be empty");
        Assert.hasText(principalName, "principalName cannot be empty");

        Optional<CreedOAuth2AuthorizedClient> clientOptional = authorizedClientRepository.findByClientRegistrationIdAndPrincipalName(clientRegistrationId, principalName);
        clientOptional.ifPresent(authorizedClientRepository::delete);
    }
}
