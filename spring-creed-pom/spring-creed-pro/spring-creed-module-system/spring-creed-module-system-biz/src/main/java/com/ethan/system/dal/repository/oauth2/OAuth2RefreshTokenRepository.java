/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */

package com.ethan.system.dal.repository.oauth2;

import com.ethan.system.dal.entity.oauth2.OAuth2RefreshTokenDO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@Deprecated
public interface OAuth2RefreshTokenRepository extends JpaRepository<OAuth2RefreshTokenDO, Long> {

    OAuth2RefreshTokenDO findByRefreshToken(String refreshToken);

    void deleteByRefreshToken(String refreshToken);
}
