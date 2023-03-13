/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */

package com.ethan.system.dal.repository.permission;

import com.ethan.system.dal.entity.permission.UserRoleDO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRoleDO, Long> {

    long countByUpdateTimeGreaterThan(LocalDateTime maxUpdateTime);

    List<UserRoleDO> findByUserId(Long userId);

    List<UserRoleDO> findByRoleIdIn(Collection<Long> roleIds);

    void deleteByUserIdAndRoleIdIn(Long userId, Collection<Long> deleteMenuIds);

    void deleteByRoleId(Long roleId);

    void deleteByUserId(Long userId);
}
