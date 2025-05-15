/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */

package com.ethan.system.dal.repository.permission;

import com.ethan.system.dal.entity.permission.SystemMenuRoles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface SystemMenuRolesRepository extends JpaRepository<SystemMenuRoles, Long>, JpaSpecificationExecutor<SystemMenuRoles> {
    // void deleteByRolesIdAndMenusIdIn(Long roleId, Collection<Long> deleteMenuIds);
}
