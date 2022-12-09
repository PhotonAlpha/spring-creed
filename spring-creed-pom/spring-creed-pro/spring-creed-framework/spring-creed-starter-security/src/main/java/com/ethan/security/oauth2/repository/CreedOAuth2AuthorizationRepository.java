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

    @Query("select a from CreedConsumer a where a.state = :token" +
            " or a.authorizationCodeValue = :token" +
            " or a.accessTokenValue = :token" +
            " or a.refreshTokenValue = :token"
    )
    Optional<CreedOAuth2Authorization> findByStateOrAuthorizationCodeValueOrAccessTokenValueOrRefreshTokenValue(@Param("token") String token);

}
