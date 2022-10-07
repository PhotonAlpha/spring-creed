package com.ethan.std.provisioning;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @description: spring-creed
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 * @date: 8/4/2022 3:12 PM
 */
@Repository
public interface OauthRefreshTokenRepository extends CrudRepository<OauthRefreshToken, String> {

    @Query(value = "select new com.ethan.std.provisioning.OauthRefreshToken(r.tokenId, r.token, r.authentication) from OauthRefreshToken r where r.tokenId = ?1")
    Optional<OauthRefreshToken> findAuthentication(String token);

    @Query(value = "select new com.ethan.std.provisioning.OauthRefreshToken(r.tokenId, r.token) from OauthRefreshToken r where r.tokenId = ?1")
    Optional<OauthRefreshToken> findToken(String token);

}
