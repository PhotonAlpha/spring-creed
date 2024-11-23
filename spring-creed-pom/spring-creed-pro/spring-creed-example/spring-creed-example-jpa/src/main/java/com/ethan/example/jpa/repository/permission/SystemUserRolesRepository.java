/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */

package com.ethan.example.jpa.repository.permission;

import com.ethan.example.jpa.dal.permission.SystemUserRoles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface SystemUserRolesRepository extends JpaRepository<SystemUserRoles, Long>, JpaSpecificationExecutor<SystemUserRoles> {

}
