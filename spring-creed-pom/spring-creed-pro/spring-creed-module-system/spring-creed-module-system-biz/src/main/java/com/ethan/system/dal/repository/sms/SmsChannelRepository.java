/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */

package com.ethan.system.dal.repository.sms;

import com.ethan.system.dal.entity.sms.SmsChannelDO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.Instant;

@Repository
public interface SmsChannelRepository extends JpaRepository<SmsChannelDO, Long>, JpaSpecificationExecutor<SmsChannelDO> {

    int countByUpdateTimeGreaterThan(Instant maxUpdateTime);
}
