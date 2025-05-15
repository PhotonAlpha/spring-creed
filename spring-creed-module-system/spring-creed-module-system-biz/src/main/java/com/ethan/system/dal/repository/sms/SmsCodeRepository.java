/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */

package com.ethan.system.dal.repository.sms;

import com.ethan.system.dal.entity.sms.SmsCodeDO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface SmsCodeRepository extends JpaRepository<SmsCodeDO, Long>, JpaSpecificationExecutor<SmsCodeDO> {

    // @Query(value = "select u from SmsCodeDO u where u.mobile=?1 and u.code =?2 and u.scene=?3 order by u.id desc")
    SmsCodeDO findTop1ByMobileAndCodeAndSceneOrderByIdDesc(String mobile, String o, Integer o1);
}
