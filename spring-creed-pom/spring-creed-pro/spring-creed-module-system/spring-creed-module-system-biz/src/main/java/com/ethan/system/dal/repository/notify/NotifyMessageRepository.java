package com.ethan.system.dal.repository.notify;


import com.ethan.system.controller.admin.notify.vo.message.NotifyMessageMyPageReqVO;
import com.ethan.system.controller.admin.notify.vo.message.NotifyMessagePageReqVO;
import com.ethan.system.dal.entity.notify.NotifyMessageDO;
import jakarta.persistence.criteria.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Repository
public interface NotifyMessageRepository extends JpaRepository<NotifyMessageDO, Long>, JpaSpecificationExecutor<NotifyMessageDO> {

    default Page<NotifyMessageDO> findByPage(NotifyMessagePageReqVO reqVO) {
        return findAll((Specification<NotifyMessageDO>) (root, query, cb) -> {
            List<Predicate> predicateList = new ArrayList<>();
            if (Objects.nonNull(reqVO.getUserId())) {
                predicateList.add(cb.equal(root.get("userId"), reqVO.getUserId()));
            }
            if (Objects.nonNull(reqVO.getUserType())) {
                predicateList.add(cb.equal(root.get("getUserType"), reqVO.getUserType()));
            }
            if (StringUtils.isNotBlank(reqVO.getTemplateCode())) {
                predicateList.add(cb.like(root.get("templateCode"), "%" + reqVO.getTemplateCode() + "%"));
            }
            if (Objects.nonNull(reqVO.getTemplateType())) {
                predicateList.add(cb.equal(root.get("templateType"), reqVO.getTemplateType()));
            }
            if (Objects.nonNull(reqVO.getCreateTime())) {
                predicateList.add(cb.greaterThan(root.get("createTime"), reqVO.getCreateTime()));
            }
            query.orderBy(cb.desc(root.get("id")));
            return cb.and(predicateList.toArray(new Predicate[0]));
        }, PageRequest.of(reqVO.getPageNo(), reqVO.getPageSize()));
    }

    default Page<NotifyMessageDO> findByPage(NotifyMessageMyPageReqVO reqVO, Long userId, Integer userType) {
        /* return selectPage(reqVO, new LambdaQueryWrapperX<NotifyMessageDO>()
                .eqIfPresent(NotifyMessageDO::getReadStatus, reqVO.getReadStatus())
                .betweenIfPresent(NotifyMessageDO::getCreateTime, reqVO.getCreateTime())
                .eq(NotifyMessageDO::getUserId, userId)
                .eq(NotifyMessageDO::getUserType, userType)
                .orderByDesc(NotifyMessageDO::getId)); */
        return findAll((Specification<NotifyMessageDO>) (root, query, cb) -> {
            List<Predicate> predicateList = new ArrayList<>();
            if (Objects.nonNull(reqVO.getReadStatus())) {
                predicateList.add(cb.equal(root.get("readStatus"), reqVO.getReadStatus()));
            }
            if (Objects.nonNull(userId)) {
                predicateList.add(cb.equal(root.get("userId"), userId));
            }
            if (Objects.nonNull(userType)) {
                predicateList.add(cb.equal(root.get("getUserType"), userType));
            }
            if (Objects.nonNull(reqVO.getCreateTime())) {
                predicateList.add(cb.greaterThan(root.get("createTime"), reqVO.getCreateTime()));
            }
            query.orderBy(cb.desc(root.get("id")));
            return cb.and(predicateList.toArray(new Predicate[0]));
        }, PageRequest.of(reqVO.getPageNo(), reqVO.getPageSize()));
    }

    default List<NotifyMessageDO> updateListRead(Collection<Long> ids, Long userId, Integer userType) {
        List<NotifyMessageDO> list = findAll((Specification<NotifyMessageDO>) (root, query, cb) -> {
            List<Predicate> predicateList = new ArrayList<>();
            if (!CollectionUtils.isEmpty(ids)) {
                predicateList.add(root.get("id").in(ids));
            }
            if (Objects.nonNull(userId)) {
                predicateList.add(cb.equal(root.get("userId"), userId));
            }
            if (Objects.nonNull(userType)) {
                predicateList.add(cb.greaterThan(root.get("userType"), userType));
            }
            predicateList.add(cb.equal(root.get("readStatus"), false));
            query.orderBy(cb.desc(root.get("id")));
            return cb.and(predicateList.toArray(new Predicate[0]));
        });
        list.forEach(n -> {
            n.setReadStatus(true);
            n.setReadTime(LocalDateTime.now());
        });
        return saveAll(list);
    }

    default List<NotifyMessageDO> updateListRead(Long userId, Integer userType) {
        return updateListRead(null, userId, userType);
    }

    default List<NotifyMessageDO> findByUnreadListByUserIdAndUserType(Long userId, Integer userType, Integer size) {
        return findAll((Specification<NotifyMessageDO>) (root, query, cb) -> {
            List<Predicate> predicateList = new ArrayList<>();
            if (Objects.nonNull(userId)) {
                predicateList.add(cb.equal(root.get("userId"), userId));
            }
            if (Objects.nonNull(userType)) {
                predicateList.add(cb.greaterThan(root.get("userType"), userType));
            }
            predicateList.add(cb.equal(root.get("readStatus"), false));
            query.orderBy(cb.desc(root.get("id")));
            return cb.and(predicateList.toArray(new Predicate[0]));
        }, Pageable.ofSize(size)).toList();
    }

    default Long countByUnreadCountByUserIdAndUserType(Long userId, Integer userType) {
        return count((Specification<NotifyMessageDO>) (root, query, cb) -> {
            List<Predicate> predicateList = new ArrayList<>();
            if (Objects.nonNull(userId)) {
                predicateList.add(cb.equal(root.get("userId"), userId));
            }
            if (Objects.nonNull(userType)) {
                predicateList.add(cb.greaterThan(root.get("userType"), userType));
            }
            predicateList.add(cb.equal(root.get("readStatus"), false));
            query.orderBy(cb.desc(root.get("id")));
            return cb.and(predicateList.toArray(new Predicate[0]));
        });
    }

}
