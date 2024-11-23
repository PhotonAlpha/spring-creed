/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */

package com.ethan.system.dal.repository.permission;

import com.ethan.system.dal.entity.permission.SystemAuthorities;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface SystemAuthoritiesRepository extends JpaRepository<SystemAuthorities, Long>, JpaSpecificationExecutor<SystemAuthorities> {


/*     long countByUpdateTimeGreaterThan(ZonedDateTime maxUpdateTime);

    long countByParentId(Long menuId);

    Optional<MenuDO> findByParentIdAndName(Long parentId, String name); */
}
