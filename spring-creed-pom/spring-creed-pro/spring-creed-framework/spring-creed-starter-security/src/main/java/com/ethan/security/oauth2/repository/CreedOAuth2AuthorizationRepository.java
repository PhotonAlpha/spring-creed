package com.ethan.security.oauth2.repository;


import com.ethan.security.oauth2.entity.CreedOAuth2Authorization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CreedOAuth2AuthorizationRepository extends JpaRepository<CreedOAuth2Authorization, String> {

    Optional<CreedOAuth2Authorization> findByState(String s);

    Optional<CreedOAuth2Authorization> findByAuthorizationCodeValue(String authorizationCode);
    Optional<CreedOAuth2Authorization> findByAccessTokenValue(String accessToken);

    Optional<CreedOAuth2Authorization> findByRefreshTokenValue(String refreshToken);

    Optional<CreedOAuth2Authorization> findByOidcIdTokenValue(String idToken);
    Optional<CreedOAuth2Authorization> findByUserCodeValue(String userCode);
    Optional<CreedOAuth2Authorization> findByDeviceCodeValue(String deviceCode);

    @Query("select a from CreedOAuth2Authorization a where a.state = :token" +
            " or a.authorizationCodeValue = :token" +
            " or a.accessTokenValue = :token" +
            " or a.refreshTokenValue = :token" +
            " or a.oidcIdTokenValue = :token" +
            " or a.userCodeValue = :token" +
            " or a.deviceCodeValue = :token"
    )
    Optional<CreedOAuth2Authorization> findByTokenValueOrCodeValue(@Param("token") String token);

    Optional<CreedOAuth2Authorization> findByRegisteredClientIdAndPrincipalName(String registeredClientId, String principalName);
}
