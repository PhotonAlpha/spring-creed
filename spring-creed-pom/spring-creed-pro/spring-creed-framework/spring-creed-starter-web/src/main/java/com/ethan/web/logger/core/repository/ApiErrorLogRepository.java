/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */

package com.ethan.web.logger.core.repository;

import com.ethan.web.logger.core.entity.ApiErrorLogDO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ApiErrorLogRepository extends JpaRepository<ApiErrorLogDO, Long>, JpaSpecificationExecutor<ApiErrorLogDO> {
}
