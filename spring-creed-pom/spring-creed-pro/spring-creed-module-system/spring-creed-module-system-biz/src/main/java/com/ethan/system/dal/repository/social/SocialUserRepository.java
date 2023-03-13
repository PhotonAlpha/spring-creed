/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */

package com.ethan.system.dal.repository.social;

import com.ethan.system.dal.entity.social.SocialUserDO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SocialUserRepository extends JpaRepository<SocialUserDO, Long> {

    SocialUserDO findByTypeAndCodeAndState(Integer type, String code, String state);

    SocialUserDO findByTypeAndOpenid(Integer type, String s);
}
