/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */

package com.ethan.system.dal.repository.permission;

import com.ethan.common.constant.CommonStatusEnum;
import com.ethan.common.utils.date.DateUtils;
import com.ethan.system.controller.admin.permission.vo.role.RolePageReqVO;
import com.ethan.system.dal.entity.permission.SystemRoles;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public interface SystemRolesRepository extends JpaRepository<SystemRoles, Long>, JpaSpecificationExecutor<SystemRoles> {
    List<SystemRoles> findByEnabledIn(List<CommonStatusEnum> statusEnum);
    Optional<SystemRoles> findByCode(String code);

    boolean existsByCodeOrName(String code, String name);

    default Page<SystemRoles> findByPage(RolePageReqVO reqVO) {
        return findAll(
                (Specification<SystemRoles>) (root, query, cb) -> {
                    List<Predicate> predicateList = new ArrayList<>();
                    if (StringUtils.hasText(reqVO.getName())) {
                        // 本处我都转为小写，进行模糊匹配
                        predicateList.add(cb.like(cb.lower(root.get("name").as(String.class)),
                                "%" + reqVO.getName().toLowerCase() + "%"));
                    }
                    if (StringUtils.hasText(reqVO.getCode())) {
                        // // 本处我都转为小写，进行模糊匹配
                        predicateList.add(cb.like(root.get("code"), "%" + reqVO.getCode() + "%"));
                    }
                    if (Objects.nonNull(reqVO.getEnabled())) {
                        predicateList.add(cb.equal(root.get("enabled"), reqVO.getEnabled()));
                    }
                    if (Objects.nonNull(reqVO.getCreateTime())) {
                        predicateList.add(DateUtils.instantCriteriaBuilder(cb, root.get("createTime"), reqVO.getCreateTime()));
                    }
                    query.orderBy(cb.desc(root.get("id")));
                    return cb.and(predicateList.toArray(new Predicate[0]));
                }, PageRequest.of(reqVO.getPageNo(), reqVO.getPageSize()));
    }
}
