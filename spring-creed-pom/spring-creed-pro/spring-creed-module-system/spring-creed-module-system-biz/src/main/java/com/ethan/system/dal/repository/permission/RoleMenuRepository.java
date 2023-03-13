/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */

package com.ethan.system.dal.repository.permission;

import com.ethan.system.dal.entity.permission.RoleMenuDO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Repository
public interface RoleMenuRepository extends JpaRepository<RoleMenuDO, Long>, JpaSpecificationExecutor<RoleMenuDO> {

    long countByUpdateTimeGreaterThan(LocalDateTime maxUpdateTime);

    List<RoleMenuDO> findByRoleId(Long roleId);

    void deleteByRoleIdAndMenuIdIn(Long roleId, Collection<Long> deleteMenuIds);

    void deleteByRoleId(Long roleId);

    void deleteByMenuId(Long menuId);
}
