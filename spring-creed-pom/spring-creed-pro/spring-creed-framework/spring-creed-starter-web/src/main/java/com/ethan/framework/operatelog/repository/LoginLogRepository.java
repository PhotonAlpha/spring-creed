/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */

package com.ethan.framework.operatelog.repository;

import com.ethan.framework.operatelog.entity.LoginLogDO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface LoginLogRepository extends JpaRepository<LoginLogDO, Long>, JpaSpecificationExecutor<LoginLogDO> {
}
