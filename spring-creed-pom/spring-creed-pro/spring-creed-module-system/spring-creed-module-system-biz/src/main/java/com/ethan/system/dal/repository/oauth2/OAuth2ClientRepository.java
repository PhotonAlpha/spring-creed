/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */

package com.ethan.system.dal.repository.oauth2;

import com.ethan.system.dal.entity.oauth2.OAuth2ClientDO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;

@Repository
@Deprecated
public interface OAuth2ClientRepository extends JpaRepository<OAuth2ClientDO, Long> {

    long countByUpdateTimeGreaterThan(Instant maxUpdateTime);

    OAuth2ClientDO findByClientId(String clientId);
}
