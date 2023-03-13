/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */

package com.ethan.system.dal.repository.oauth2;

import com.ethan.system.dal.entity.oauth2.OAuth2AccessTokenDO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Deprecated
public interface OAuth2AccessTokenRepository extends JpaRepository<OAuth2AccessTokenDO, Long>, JpaSpecificationExecutor<OAuth2AccessTokenDO> {

    List<OAuth2AccessTokenDO> findByRefreshToken(String refreshToken);

    OAuth2AccessTokenDO findByAccessToken(String accessToken);
}
