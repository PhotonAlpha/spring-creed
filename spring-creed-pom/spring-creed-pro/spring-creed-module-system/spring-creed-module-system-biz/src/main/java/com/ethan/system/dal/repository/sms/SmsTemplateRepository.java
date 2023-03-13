/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */

package com.ethan.system.dal.repository.sms;

import com.ethan.system.dal.entity.sms.SmsTemplateDO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.Instant;

@Repository
public interface SmsTemplateRepository extends JpaRepository<SmsTemplateDO, Long>, JpaSpecificationExecutor<SmsTemplateDO> {
    long countByUpdateTimeGreaterThan(Instant maxUpdateTime);

    SmsTemplateDO findByCode(String code);

    Long countByChannelId(Long channelId);
}
