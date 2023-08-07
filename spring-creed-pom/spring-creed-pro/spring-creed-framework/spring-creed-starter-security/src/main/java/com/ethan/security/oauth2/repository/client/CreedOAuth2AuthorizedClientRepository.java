/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */

package com.ethan.security.oauth2.repository.client;

import com.ethan.security.oauth2.entity.client.CreedOAuth2AuthorizedClient;
import com.ethan.security.oauth2.entity.client.CreedOAuth2ClientConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CreedOAuth2AuthorizedClientRepository extends JpaRepository<CreedOAuth2AuthorizedClient, Long>, JpaSpecificationExecutor<CreedOAuth2AuthorizedClient> {
    Optional<CreedOAuth2AuthorizedClient> findByClientRegistrationIdAndPrincipalName(String registrationId, String clientId);

    Optional<CreedOAuth2AuthorizedClient> findByAccessTokenValue(String token);

    Optional<CreedOAuth2AuthorizedClient> findByRefreshTokenValue(String refreshToken);
}
