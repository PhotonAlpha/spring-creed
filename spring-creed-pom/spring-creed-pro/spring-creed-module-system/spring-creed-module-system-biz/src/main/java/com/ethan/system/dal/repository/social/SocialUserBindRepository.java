/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */

package com.ethan.system.dal.repository.social;

import com.ethan.system.dal.entity.social.SocialUserBindDO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SocialUserBindRepository extends JpaRepository<SocialUserBindDO, Long>, JpaSpecificationExecutor<SocialUserBindDO> {

    List<SocialUserBindDO> findByUserIdAndUserType(Long userId, Integer userType);

    void deleteByUserTypeAndSocialUserId(Integer userType, Long id);

    void deleteByUserTypeAndUserIdAndSocialType(Integer userType, String userId, Integer type);

    SocialUserBindDO findByUserTypeAndSocialUserId(Integer userType, Long id);
}
