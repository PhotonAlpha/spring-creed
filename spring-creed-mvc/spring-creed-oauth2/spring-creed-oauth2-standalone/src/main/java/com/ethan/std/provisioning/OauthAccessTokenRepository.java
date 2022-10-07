package com.ethan.std.provisioning;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * @description: spring-creed
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 * @date: 8/4/2022 3:10 PM
 */
@Repository
public interface OauthAccessTokenRepository extends PagingAndSortingRepository<OauthAccessToken, String> {

    @Query(value = "select new com.ethan.std.provisioning.OauthAccessToken(o.tokenId, o.authenticationId, o.authentication) from OauthAccessToken o where o.tokenId = ?1")
    Optional<OauthAccessToken> findAuthentication(String token);

    @Query(value = "select new com.ethan.std.provisioning.OauthAccessToken(o.tokenId, o.token) from OauthAccessToken o where o.tokenId = ?1")
    Optional<OauthAccessToken> findToken(String token);


    // @Query(value = "select new com.ethan.std.provisioning.OauthAccessToken(o.tokenId, o.token) from OauthAccessToken o where o.authenticationId = ?1 order by o.createTime desc")
    Optional<OauthAccessToken> findFirstByAuthenticationIdOrderByCreateTimeDesc(String authenticationId);

    @Query(value = "select new com.ethan.std.provisioning.OauthAccessToken(o.tokenId, o.token) from OauthAccessToken o where o.userName = ?1 and o.clientId = ?2")
    Optional<List<OauthAccessToken>> findTokenByClientIdAndUserName(String username, String clientId);

    @Query(value = "select new com.ethan.std.provisioning.OauthAccessToken(o.tokenId, o.token) from OauthAccessToken o where o.clientId = ?1")
    Optional<List<OauthAccessToken>> findTokenByClientId(String clientId);

    @Transactional
    @Modifying
    @Query(value = "delete from OauthAccessToken o where o.refreshToken = ?1")
    void deleteByRefreshToken(String refreshToken);

    @Transactional
    @Modifying
    @Query(value = "delete from OauthAccessToken o where o.tokenId = ?1")
    void deleteByTokenId(String tokenId);
}
