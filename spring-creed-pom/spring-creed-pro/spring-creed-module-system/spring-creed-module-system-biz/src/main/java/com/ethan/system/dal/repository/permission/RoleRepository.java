/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */

package com.ethan.system.dal.repository.permission;

import com.ethan.system.dal.entity.permission.RoleDO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<RoleDO, Long>, JpaSpecificationExecutor<RoleDO> {

    long countByUpdateTimeGreaterThan(LocalDateTime maxUpdateTime);

    List<RoleDO> findByStatusIn(Collection<Integer> statuses);

    Optional<RoleDO> findByName(String name);

    Optional<RoleDO> findByCode(String code);
}
