/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */

package com.ethan.system.dal.repository.dict;

import com.ethan.system.controller.admin.dict.vo.data.DictDataExportReqVO;
import com.ethan.system.controller.admin.dict.vo.data.DictDataPageReqVO;
import com.ethan.system.dal.entity.dict.DictDataDO;
import jakarta.persistence.criteria.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Repository
public interface DictDataRepository extends JpaRepository<DictDataDO, Long>, JpaSpecificationExecutor<DictDataDO> {

    default Page<DictDataDO> selectPage(DictDataPageReqVO reqVO) {
        return findAll(
                (Specification<DictDataDO>) (root, query, cb) -> {
                    List<Predicate> predicateList = new ArrayList<>();
                    if (StringUtils.isNotBlank(reqVO.getLabel())) {
                        // // 本处我都转为小写，进行模糊匹配
                        predicateList.add(cb.like(root.get("label"), "%" + reqVO.getLabel() + "%"));
                    }
                    if (StringUtils.isNotBlank(reqVO.getDictType())) {
                        // // 本处我都转为小写，进行模糊匹配
                        predicateList.add(cb.like(root.get("dictType"), "%" + reqVO.getDictType() + "%"));
                    }
                    if (Objects.nonNull(reqVO.getStatus())) {
                        predicateList.add(cb.equal(root.get("status"), reqVO.getStatus()));
                    }
                    cb.desc(root.get("id"));
                    return cb.and(predicateList.toArray(new Predicate[0]));
                }, PageRequest.of(reqVO.getPageNo(), reqVO.getPageSize()));
    }
    default List<DictDataDO> selectList(DictDataExportReqVO reqVO) {
        return findAll(
                (Specification<DictDataDO>) (root, query, cb) -> {
                    List<Predicate> predicateList = new ArrayList<>();
                    if (StringUtils.isNotBlank(reqVO.getLabel())) {
                        // // 本处我都转为小写，进行模糊匹配
                        predicateList.add(cb.like(root.get("label"), "%" + reqVO.getLabel() + "%"));
                    }
                    if (StringUtils.isNotBlank(reqVO.getDictType())) {
                        // // 本处我都转为小写，进行模糊匹配
                        predicateList.add(cb.like(root.get("dictType"), "%" + reqVO.getDictType() + "%"));
                    }
                    if (Objects.nonNull(reqVO.getStatus())) {
                        predicateList.add(cb.equal(root.get("status"), reqVO.getStatus()));
                    }
                    return cb.and(predicateList.toArray(new Predicate[0]));
                });
    }

    long countByDictType(String dictType);

    DictDataDO findByDictTypeAndValue(String dictType, String value);

    List<DictDataDO> findByDictTypeAndValueIn(String dictType, Collection<String> values);

    DictDataDO findByDictTypeAndLabel(String dictType, String label);
}
