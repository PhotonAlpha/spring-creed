/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */

package com.ethan.system.dal.repository.permission;

import com.ethan.common.constant.CommonStatusEnum;
import com.ethan.common.utils.date.DateUtils;
import com.ethan.system.controller.admin.user.vo.user.UserPageReqVO;
import com.ethan.system.dal.entity.dept.SystemDepts;
import com.ethan.system.dal.entity.permission.SystemUsers;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.apache.poi.ss.formula.functions.T;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Repository
public interface SystemUsersRepository extends JpaRepository<SystemUsers, Long>, JpaSpecificationExecutor<SystemUsers> {
    Optional<SystemUsers> findByUsername(String username);

    Optional<SystemUsers> findByEmail(String email);

    Optional<SystemUsers> findByPhone(String mobile);


    /**
     * @EntityGraph(value = "User.list", type = EntityGraph.EntityGraphType.FETCH)
     * 在分页查询会报错：cannot simultaneously fetch multiple bags
     *
     * 但是使用之后就不能清楚知道limit与offset，只能将数据返回给后端，让JPA处理，组装完成后再返回一个Page对象。
     *
     * 现在的处理方式是先通过普通查询，将需要的数据ID找出，再通过数据的ID和@Entity查询详细的细节。
     *
     * @param reqVO
     * @param deptCondition
     * @return
     */
    default Page<SystemUsers> findByCondition(UserPageReqVO reqVO, Set<Long> deptCondition) {
        return findAll(
                (Specification<SystemUsers>) (root, query, cb) -> {
                    List<Predicate> predicateList = new ArrayList<>();

                    if (StringUtils.hasText(reqVO.getUsername())) {
                        predicateList.add(cb.like(cb.lower(root.get("username").as(String.class)),
                                "%" + reqVO.getUsername().toLowerCase() + "%"));
                    }
                    if (StringUtils.hasText(reqVO.getMobile())) {
                        predicateList.add(cb.like(cb.lower(root.get("phone").as(String.class)),
                                "%" + reqVO.getMobile().toLowerCase() + "%"));
                    }
                    if (Objects.nonNull(reqVO.getStatus())) {
                        predicateList.add(cb.equal(root.get("enabled"), reqVO.getStatus()));
                    }
                    if (Objects.nonNull(reqVO.getCreateTime())) {
                        predicateList.add(DateUtils.instantCriteriaBuilder(cb, root.get("createTime"), reqVO.getCreateTime()));
                    }
                    if (!CollectionUtils.isEmpty(deptCondition)) {
                        Join<SystemDepts, SystemUsers> deptUsers = root.join("deptUsers");
                        predicateList.add(deptUsers.get("id").in(deptCondition));
                    }
                    query.orderBy(cb.desc(root.get("id")));
                    return cb.and(predicateList.toArray(new Predicate[0]));
                }, PageRequest.of(reqVO.getPageNo(), reqVO.getPageSize())
        );
    }

    List<SystemUsers> findByNickname(String nickname);

    List<SystemUsers> findByEnabled(CommonStatusEnum enabled);
}
