/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */

package com.ethan.system.dal.repository.permission;

import com.ethan.common.constant.CommonStatusEnum;
import com.ethan.common.utils.date.DateUtils;
import com.ethan.system.controller.admin.permission.vo.authority.AuthorityPageReqVO;
import com.ethan.system.dal.entity.permission.SystemAuthorities;
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
public interface SystemAuthoritiesRepository extends JpaRepository<SystemAuthorities, Long>, JpaSpecificationExecutor<SystemAuthorities> {
    Optional<SystemAuthorities> findByAuthority(String authority);

    boolean existsByAuthority(String authority);

    default Page<SystemAuthorities> findByPage(AuthorityPageReqVO reqVO) {
        return findAll(
                (Specification<SystemAuthorities>) (root, query, cb) -> {
                    List<Predicate> predicateList = new ArrayList<>();
                    if (StringUtils.hasText(reqVO.getAuthority())) {
                        // 本处我都转为小写，进行模糊匹配
                        predicateList.add(cb.like(cb.lower(root.get("authority").as(String.class)),
                                "%" + reqVO.getAuthority().toLowerCase() + "%"));
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

    List<SystemAuthorities> findByEnabledIn(List<CommonStatusEnum> statusEnumList);
}
