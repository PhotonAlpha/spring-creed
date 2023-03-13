/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */

package com.ethan.system.dal.repository.file;

import com.ethan.system.dal.entity.dept.PostDO;
import com.ethan.system.dal.entity.file.FileContentDO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface FileContentRepository extends JpaRepository<FileContentDO, Long>, JpaSpecificationExecutor<FileContentDO> {

}
