/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */

package com.ethan.security.oauth2.provider;

import com.ethan.security.oauth2.repository.CreedOAuth2AuthorizedClientRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientId;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.util.Assert;

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
            return null;
        }
        // return (T) this.authorizedClients.get(new OAuth2AuthorizedClientId(clientRegistrationId, principalName));
        return null;
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
