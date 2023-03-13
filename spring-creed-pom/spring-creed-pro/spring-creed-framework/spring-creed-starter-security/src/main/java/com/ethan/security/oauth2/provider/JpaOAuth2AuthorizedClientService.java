/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */

package com.ethan.security.oauth2.provider;

import com.ethan.security.oauth2.dto.CreedOAuth2AuthorizedClientDTO;
import com.ethan.security.oauth2.entity.CreedOAuth2AuthorizedClient;
import com.ethan.security.oauth2.repository.CreedOAuth2AuthorizedClientRepository;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientId;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.util.Assert;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;

/**
 * TODO,  need to provide a service and intergrate with OAuth2ClientConfig
 * see {@link OAuth2TokenServiceImpl}
 */
public class JpaOAuth2AuthorizedClientService implements OAuth2AuthorizedClientService {
    private final CreedOAuth2AuthorizedClientRepository authorizedClientRepository;

    private final ClientRegistrationRepository clientRegistrationRepository;

    /**
     * //TODO
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
    public <T extends OAuth2AuthorizedClient> T loadAuthorizedClient(String clientRegistrationId, String principalName) {
        Assert.hasText(clientRegistrationId, "clientRegistrationId cannot be empty");
        Assert.hasText(principalName, "principalName cannot be empty");
        ClientRegistration registration = this.clientRegistrationRepository.findByRegistrationId(clientRegistrationId);
        if (registration == null) {
            throw new DataRetrievalFailureException(
                    "The ClientRegistration with id '" + clientRegistrationId + "' exists in the data source, "
                            + "however, it was not found in the ClientRegistrationRepository.");
        }
        Optional<CreedOAuth2AuthorizedClient> clientOptional = authorizedClientRepository.findByClientRegistrationIdAndPrincipalName(clientRegistrationId, principalName)
                .stream().findFirst();
        if (clientOptional.isEmpty()) {
            return null;
        }
        CreedOAuth2AuthorizedClient authorizedClient = clientOptional.get();

        OAuth2AccessToken.TokenType tokenType = null;
        if (OAuth2AccessToken.TokenType.BEARER.getValue().equalsIgnoreCase(authorizedClient.getAccessTokenType())) {
            tokenType = OAuth2AccessToken.TokenType.BEARER;
        }
        String tokenValue = authorizedClient.getAccessTokenValue();
        Instant issuedAt = authorizedClient.getAccessTokenIssuedAt();
        Instant expiresAt = authorizedClient.getAccessTokenExpiresAt();
        Set<String> scopes = authorizedClient.getAccessTokenScopes();

        OAuth2AccessToken accessToken = new OAuth2AccessToken(tokenType, tokenValue, issuedAt, expiresAt, scopes);
        String refreshTokenValue = authorizedClient.getRefreshTokenValue();
        Instant refreshTokenExpiresAt = authorizedClient.getRefreshTokenExpiresAt();
        Instant refreshTokenIssuedAt = authorizedClient.getRefreshTokenIssuedAt();
        OAuth2RefreshToken refreshToken = new OAuth2RefreshToken(refreshTokenValue, refreshTokenIssuedAt, refreshTokenExpiresAt);
        String principalName2 = authorizedClient.getPrincipalName();

        CreedOAuth2AuthorizedClientDTO client = new CreedOAuth2AuthorizedClientDTO(registration, principalName2, accessToken, refreshToken);
        return (T) client;
    }

    @Override
    public void saveAuthorizedClient(OAuth2AuthorizedClient authorizedClient, Authentication principal) {
        Assert.notNull(authorizedClient, "authorizedClient cannot be null");
        Assert.notNull(principal, "principal cannot be null");
        // this.authorizedClients.put(new OAuth2AuthorizedClientId(
        //         authorizedClient.getClientRegistration().getRegistrationId(), principal.getName()), authorizedClient);
    }

    @Override
    public void removeAuthorizedClient(String clientRegistrationId, String principalName) {
        Assert.hasText(clientRegistrationId, "clientRegistrationId cannot be empty");
        Assert.hasText(principalName, "principalName cannot be empty");
        ClientRegistration registration = this.clientRegistrationRepository.findByRegistrationId(clientRegistrationId);
        if (registration != null) {
            // this.authorizedClients.remove(new OAuth2AuthorizedClientId(clientRegistrationId, principalName));
        }
    }
}
