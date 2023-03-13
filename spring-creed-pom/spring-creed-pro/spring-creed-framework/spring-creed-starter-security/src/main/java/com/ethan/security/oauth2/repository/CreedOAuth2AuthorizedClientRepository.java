/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */

package com.ethan.security.oauth2.repository;

import com.ethan.security.oauth2.entity.CreedOAuth2AuthorizedClient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface CreedOAuth2AuthorizedClientRepository extends JpaRepository<CreedOAuth2AuthorizedClient, Long>, JpaSpecificationExecutor<CreedOAuth2AuthorizedClient> {
    Optional<CreedOAuth2AuthorizedClient> findByRefreshTokenValue(String refreshToken);

    Optional<CreedOAuth2AuthorizedClient> findByAccessTokenValue(String accessToken);

    List<CreedOAuth2AuthorizedClient> findByClientRegistrationIdAndPrincipalName(String clientRegistrationId, String principalName);


}
