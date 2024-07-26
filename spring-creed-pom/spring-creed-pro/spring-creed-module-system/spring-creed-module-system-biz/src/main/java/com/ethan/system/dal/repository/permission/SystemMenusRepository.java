/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */

package com.ethan.system.dal.repository.permission;

import com.ethan.common.utils.date.DateUtils;
import com.ethan.system.controller.admin.permission.vo.menu.MenuListReqVO;
import com.ethan.system.controller.admin.permission.vo.role.RolePageReqVO;
import com.ethan.system.dal.entity.permission.SystemMenus;
import com.ethan.system.dal.entity.permission.SystemRoles;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public interface SystemMenusRepository extends JpaRepository<SystemMenus, Long>, JpaSpecificationExecutor<SystemMenus> {
    long countByParentId(Long menuId);

    Optional<SystemMenus> findByParentIdAndName(Long parentId, String name);

    default List<SystemMenus> findByCondition(MenuListReqVO reqVO) {
        return findAll(
                (Specification<SystemMenus>) (root, query, cb) -> {
                    List<Predicate> predicateList = new ArrayList<>();
                    if (StringUtils.hasText(reqVO.getName())) {
                        predicateList.add(cb.like(cb.lower(root.get("name").as(String.class)),
                                "%" + reqVO.getName().toLowerCase() + "%"));
                    }
                    if (Objects.nonNull(reqVO.getStatus())) {
                        predicateList.add(cb.equal(root.get("enabled"), reqVO.getStatus()));
                    }
                    query.orderBy(cb.desc(root.get("id")));
                    return cb.and(predicateList.toArray(new Predicate[0]));
                });
    }

}
