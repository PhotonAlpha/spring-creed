package com.ethan.system.dal.repository.notify;


import com.ethan.system.controller.admin.notify.vo.template.NotifyTemplatePageReqVO;
import com.ethan.system.dal.entity.notify.NotifyTemplateDO;
import jakarta.persistence.criteria.Predicate;
import org.apache.commons.lang3.StringUtils;
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
public interface NotifyTemplateRepository extends JpaRepository<NotifyTemplateDO, Long>, JpaSpecificationExecutor<NotifyTemplateDO> {
    NotifyTemplateDO findTop1ByCodeOrderByIdDesc(String code);
    default NotifyTemplateDO findByCode(String code) {
        return findTop1ByCodeOrderByIdDesc(code);
    }

    default Page<NotifyTemplateDO> findByPage(NotifyTemplatePageReqVO reqVO) {
        return findAll((Specification<NotifyTemplateDO>) (root, query, cb) -> {
            List<Predicate> predicateList = new ArrayList<>();
            if (StringUtils.isNotBlank(reqVO.getCode())) {
                predicateList.add(cb.like(root.get("code"), "%" + reqVO.getCode() + "%"));
            }
            if (StringUtils.isNotBlank(reqVO.getName())) {
                predicateList.add(cb.like(root.get("name"), "%" + reqVO.getName() + "%"));
            }
            if (Objects.nonNull(reqVO.getStatus())) {
                predicateList.add(cb.equal(root.get("status"), reqVO.getStatus()));
            }
            if (Objects.nonNull(reqVO.getCreateTime())) {
                predicateList.add(cb.greaterThan(root.get("createTime"), reqVO.getCreateTime()));
            }
            query.orderBy(cb.desc(root.get("id")));
            return cb.and(predicateList.toArray(new Predicate[0]));
        }, PageRequest.of(reqVO.getPageNo(), reqVO.getPageSize()));
    }

}
