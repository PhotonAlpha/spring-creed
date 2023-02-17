/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */

package com.ethan.web.logger.core.repository;

import com.ethan.web.logger.core.entity.ApiAccessLogDO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ApiAccessLogRepository extends JpaRepository<ApiAccessLogDO, Long>, JpaSpecificationExecutor<ApiAccessLogDO> {
}
