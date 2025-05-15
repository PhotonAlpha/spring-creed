/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */

package com.ethan.example.jpa.repository.permission;

import com.ethan.example.jpa.dal.permission.SystemGroupUsers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface SystemGroupUsersRepository extends JpaRepository<SystemGroupUsers, Long>, JpaSpecificationExecutor<SystemGroupUsers> {


}
