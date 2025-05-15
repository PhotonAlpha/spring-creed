/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */

package com.ethan.system.dal.repository.permission;

import com.ethan.system.dal.entity.permission.SystemUserRoles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface SystemUserRolesRepository extends JpaRepository<SystemUserRoles, Long>, JpaSpecificationExecutor<SystemUserRoles> {
    void deleteByUsersId(Long userId);


    List<SystemUserRoles> findByRolesIdIn(Collection<Long> ids);
}
