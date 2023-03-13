/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */

package com.ethan.system.dal.repository.user;

import com.ethan.system.dal.entity.user.AdminUserDO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface AdminUserRepository extends JpaRepository<AdminUserDO, Long>, JpaSpecificationExecutor<AdminUserDO> {

    AdminUserDO findByUsername(String username);

    AdminUserDO findByMobile(String mobile);

    List<AdminUserDO> findByDeptIdIn(Collection<Long> deptIds);

    List<AdminUserDO> findByNickname(String nickname);

    AdminUserDO findByEmail(String email);

    List<AdminUserDO> findByStatus(Integer status);
}
