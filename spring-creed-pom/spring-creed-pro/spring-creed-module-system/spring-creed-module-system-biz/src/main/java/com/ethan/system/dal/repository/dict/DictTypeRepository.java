/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */

package com.ethan.system.dal.repository.dict;

import com.ethan.system.controller.admin.dict.vo.type.DictTypeExportReqVO;
import com.ethan.system.controller.admin.dict.vo.type.DictTypePageReqVO;
import com.ethan.system.dal.entity.dict.DictTypeDO;
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
public interface DictTypeRepository extends JpaRepository<DictTypeDO, Long>, JpaSpecificationExecutor<DictTypeDO> {
    default Page<DictTypeDO> findPage(DictTypePageReqVO reqVO) {
        return findAll((Specification<DictTypeDO>) (root, query, cb) -> {
            List<Predicate> predicateList = new ArrayList<>();
            if (StringUtils.isNotBlank(reqVO.getName())) {
                // // 本处我都转为小写，进行模糊匹配
                predicateList.add(cb.like(root.get("name"), "%" + reqVO.getName() + "%"));
            }
            if (StringUtils.isNotBlank(reqVO.getType())) {
                // // 本处我都转为小写，进行模糊匹配
                predicateList.add(cb.like(root.get("type"), "%" + reqVO.getType() + "%"));
            }
            if (Objects.nonNull(reqVO.getStatus())) {
                predicateList.add(cb.equal(root.get("status"), reqVO.getStatus()));
            }
            if (Objects.nonNull(reqVO.getCreateTime())) {
                predicateList.add(cb.greaterThan(root.get("createTime"), reqVO.getCreateTime()));
            }
            cb.desc(root.get("id"));
            return cb.and(predicateList.toArray(new Predicate[0]));
        }, PageRequest.of(reqVO.getPageNo(), reqVO.getPageSize()));
    }

    default List<DictTypeDO> findList(DictTypeExportReqVO reqVO) {
        return findAll((Specification<DictTypeDO>) (root, query, cb) -> {
            List<Predicate> predicateList = new ArrayList<>();
            if (StringUtils.isNotBlank(reqVO.getName())) {
                // // 本处我都转为小写，进行模糊匹配
                predicateList.add(cb.like(root.get("name"), "%" + reqVO.getName() + "%"));
            }
            if (StringUtils.isNotBlank(reqVO.getType())) {
                // // 本处我都转为小写，进行模糊匹配
                predicateList.add(cb.like(root.get("type"), "%" + reqVO.getType() + "%"));
            }
            if (Objects.nonNull(reqVO.getStatus())) {
                predicateList.add(cb.equal(root.get("status"), reqVO.getStatus()));
            }
            if (Objects.nonNull(reqVO.getCreateTime())) {
                predicateList.add(cb.greaterThan(root.get("createTime"), reqVO.getCreateTime()));
            }
            cb.desc(root.get("id"));
            return cb.and(predicateList.toArray(new Predicate[0]));
        });
    }

    DictTypeDO findByType(String type);

    DictTypeDO findByName(String name);
}
