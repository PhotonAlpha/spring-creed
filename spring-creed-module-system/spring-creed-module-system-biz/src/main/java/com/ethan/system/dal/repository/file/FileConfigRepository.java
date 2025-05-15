/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */

package com.ethan.system.dal.repository.file;

import com.ethan.system.dal.entity.file.FileConfigDO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;

@Repository
public interface FileConfigRepository extends JpaRepository<FileConfigDO, Long>, JpaSpecificationExecutor<FileConfigDO> {

    long countByUpdateTimeGreaterThan(ZonedDateTime maxUpdateTime);
}
