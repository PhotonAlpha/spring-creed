/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */

package com.ethan.system.dal.repository.dept;

import com.ethan.system.controller.admin.dept.vo.dept.DeptListReqVO;
import com.ethan.system.dal.entity.dept.SystemDepts;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public interface SystemDeptsRepository extends JpaRepository<SystemDepts, Long>, JpaSpecificationExecutor<SystemDepts> {

    boolean existsByParentId(Long id);

    Optional<SystemDepts> findByNameAndParentId(String name, Long parentId);

    default List<SystemDepts> findByCondition(DeptListReqVO reqVO) {
        return findAll(
                (Specification<SystemDepts>) (root, query, cb) -> {
                    List<Predicate> predicateList = new ArrayList<>();
                    if (StringUtils.hasText(reqVO.getName())) {
                        predicateList.add(cb.like(cb.lower(root.get("name").as(String.class)),
                                "%" + reqVO.getName().toLowerCase() + "%"));
                    }
                    if (Objects.nonNull(reqVO.getStatus())) {
                        predicateList.add(cb.equal(root.get("enabled"), reqVO.getStatus()));
                    }
                    // query.orderBy(cb.desc(root.get("id")));
                    return cb.and(predicateList.toArray(new Predicate[0]));
                }
        );
    }

    List<SystemDepts> findByParentIdIn(Collection<Long> parentIds);

}
