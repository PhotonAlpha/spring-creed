/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */

package com.ethan.system.dal.repository.dept;

import com.ethan.system.dal.entity.dept.DeptDO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;

@Repository
public interface DeptRepository extends JpaRepository<DeptDO, Long> {
    Long countByUpdateTimeGreaterThan(Instant updateTime);

    Long countByParentId(Long parentId);
}
