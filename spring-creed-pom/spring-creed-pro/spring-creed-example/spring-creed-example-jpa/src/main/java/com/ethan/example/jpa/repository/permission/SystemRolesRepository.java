/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */

package com.ethan.example.jpa.repository.permission;

import com.ethan.example.jpa.dal.permission.SystemRoles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface SystemRolesRepository extends JpaRepository<SystemRoles, Long>, JpaSpecificationExecutor<SystemRoles> {


/*     long countByUpdateTimeGreaterThan(ZonedDateTime maxUpdateTime);

    long countByParentId(Long menuId);

    Optional<MenuDO> findByParentIdAndName(Long parentId, String name); */
}
