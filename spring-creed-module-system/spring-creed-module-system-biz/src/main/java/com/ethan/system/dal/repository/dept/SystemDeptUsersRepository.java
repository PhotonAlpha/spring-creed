/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */

package com.ethan.system.dal.repository.dept;

import com.ethan.system.dal.entity.dept.SystemDeptUsers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SystemDeptUsersRepository extends JpaRepository<SystemDeptUsers, Long> {

}
