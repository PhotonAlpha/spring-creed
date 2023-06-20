package com.ethan.system.dal.repository.notice;

import com.ethan.system.controller.admin.notice.vo.NoticePageReqVO;
import com.ethan.system.dal.entity.notice.NoticeDO;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Repository
public interface NoticeRepository extends JpaRepository<NoticeDO, Long>, JpaSpecificationExecutor<NoticeDO> {

    default Page<NoticeDO> findByPage(NoticePageReqVO reqVO) {
    /*     return selectPage(reqVO, new LambdaQueryWrapperX<NoticeDO>()
                .likeIfPresent(NoticeDO::getTitle, reqVO.getTitle())
                .eqIfPresent(NoticeDO::getStatus, reqVO.getStatus())
                .orderByDesc(NoticeDO::getId)); */

        return findAll((Specification<NoticeDO>) (root, query, cb) -> {
            List<Predicate> predicateList = new ArrayList<>();
            if (Objects.nonNull(reqVO.getTitle())) {
                predicateList.add(cb.like(root.get("title"), "%" + reqVO.getTitle() + "%"));
            }
            if (Objects.nonNull(reqVO.getStatus())) {
                predicateList.add(cb.equal(root.get("status"), reqVO.getStatus()));
            }
            query.orderBy(cb.desc(root.get("id")));
            return cb.and(predicateList.toArray(new Predicate[0]));
        }, PageRequest.of(reqVO.getPageNo(), reqVO.getPageSize()));
    }

}
