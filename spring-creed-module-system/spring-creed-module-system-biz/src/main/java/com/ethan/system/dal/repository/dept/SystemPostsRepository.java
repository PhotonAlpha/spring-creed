/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */

package com.ethan.system.dal.repository.dept;

import com.ethan.system.controller.admin.dept.vo.post.PostPageReqVO;
import com.ethan.system.dal.entity.dept.SystemDepts;
import com.ethan.system.dal.entity.dept.SystemPosts;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public interface SystemPostsRepository extends JpaRepository<SystemPosts, Long>, JpaSpecificationExecutor<SystemPosts> {
    Optional<SystemPosts> findByName(String name);

    Optional<SystemPosts> findByCode(String code);

    default Page<SystemPosts> findByCondition(PostPageReqVO req) {
        return findAll(
                (Specification<SystemPosts>) (root, query, cb) -> {
                    List<Predicate> predicateList = new ArrayList<>();
                    if (StringUtils.hasText(req.getName())) {
                        predicateList.add(cb.like(cb.lower(root.get("name").as(String.class)),
                                "%" + req.getName().toLowerCase() + "%"));
                    }
                    if (StringUtils.hasText(req.getCode())) {
                        predicateList.add(cb.like(cb.lower(root.get("code").as(String.class)),
                                "%" + req.getCode().toLowerCase() + "%"));
                    }
                    if (Objects.nonNull(req.getStatus())) {
                        predicateList.add(cb.equal(root.get("enabled"), req.getStatus()));
                    }
                    query.orderBy(cb.desc(root.get("id")));
                    return cb.and(predicateList.toArray(new Predicate[0]));
                }, PageRequest.of(req.getPageNo(), req.getPageSize())
        );
    }

    default List<SystemPosts> findByIdInAndEnabledIn(Collection<Long> ids, Collection<Integer> statuses) {
        return findAll(
                (Specification<SystemPosts>) (root, query, cb) -> {
                    List<Predicate> predicateList = new ArrayList<>();
                    if (!CollectionUtils.isEmpty(ids)) {
                        predicateList.add(root.get("id").in(ids));
                    }
                    if (!CollectionUtils.isEmpty(statuses)) {
                        predicateList.add(root.get("enabled").in(statuses));
                    }
                    return cb.and(predicateList.toArray(new Predicate[0]));
                }
        );
    }
}
