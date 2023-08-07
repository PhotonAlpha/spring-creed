/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */

package com.ethan.reactive.dal.repo.permission;

import com.ethan.reactive.dal.entity.permission.RoleMenuDO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;

@Repository
public interface RoleMenuRepository extends JpaRepository<RoleMenuDO, Long>, JpaSpecificationExecutor<RoleMenuDO> {

    long countByUpdateTimeGreaterThan(ZonedDateTime maxUpdateTime);

    List<RoleMenuDO> findByRoleId(String roleId);

    void deleteByRoleIdAndMenuIdIn(String roleId, Collection<Long> deleteMenuIds);

    void deleteByRoleId(String roleId);

    void deleteByMenuId(Long menuId);
}
