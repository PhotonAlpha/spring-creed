/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */

package com.ethan.system.dal.repository.oauth2;

import com.ethan.system.dal.entity.oauth2.OAuth2ApproveDO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Deprecated
public interface OAuth2ApproveRepository extends JpaRepository<OAuth2ApproveDO, Long>, JpaSpecificationExecutor<OAuth2ApproveDO> {

    List<OAuth2ApproveDO> findByUserIdAndUserTypeAndClientId(Long userId, Integer userType, String clientId);
}
