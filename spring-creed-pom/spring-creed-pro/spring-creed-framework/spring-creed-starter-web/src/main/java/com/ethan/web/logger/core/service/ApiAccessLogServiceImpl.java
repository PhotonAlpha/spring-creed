package com.ethan.web.logger.core.service;

import com.ethan.web.logger.core.convert.ApiAccessLogConvert;
import com.ethan.web.logger.core.dto.ApiAccessLogCreateReqDTO;
import com.ethan.web.logger.core.entity.ApiAccessLogDO;
import com.ethan.web.logger.core.repository.ApiAccessLogRepository;
import com.ethan.web.logger.core.vo.accesslog.ApiAccessLogExportReqVO;
import com.ethan.web.logger.core.vo.accesslog.ApiAccessLogPageReqVO;
import jakarta.annotation.Resource;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.support.PageableUtils;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * API 访问日志 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class ApiAccessLogServiceImpl implements ApiAccessLogService {
    private static final Logger log = LoggerFactory.getLogger(ApiAccessLogServiceImpl.class);
    @Resource
    private ApiAccessLogRepository apiAccessLogRepository;

    @Override
    public void createApiAccessLog(ApiAccessLogCreateReqDTO createDTO) {
        ApiAccessLogDO apiAccessLog = ApiAccessLogConvert.INSTANCE.convert(createDTO);
        apiAccessLogRepository.save(apiAccessLog);
    }

    @Override
    public Page<ApiAccessLogDO> getApiAccessLogPage(ApiAccessLogPageReqVO pageReqVO) {
        Pageable pageable = PageRequest.of(pageReqVO.getPageNo(), pageReqVO.getPageSize());
        Page<ApiAccessLogDO> page = apiAccessLogRepository.findAll(getApiAccessLogSpecification(pageReqVO), pageable);
        log.info("page:" + page.getTotalElements());
        return page;
    }

    private static Specification<ApiAccessLogDO> getApiAccessLogSpecification(ApiAccessLogPageReqVO pageReqVO) {
        return (Specification<ApiAccessLogDO>) (root, query, cb) -> {
            List<Predicate> predicateList = new ArrayList<>();
            if (Objects.nonNull(pageReqVO.getUserId())) {
                // // 本处我都转为小写，进行模糊匹配
                // predicateList.add(cb.equal(cb.lower(root.get("userName").as(String.class)),
                //         "%" + userName.toLowerCase() + "%"));
                predicateList.add(cb.equal(root.get("userId"), pageReqVO.getUserId()));
            }
            if (Objects.nonNull(pageReqVO.getUserType())) {
                predicateList.add(cb.equal(root.get("userType"), pageReqVO.getUserType()));
            }
            if (Objects.nonNull(pageReqVO.getApplicationName())) {
                predicateList.add(cb.equal(root.get("applicationName"), pageReqVO.getApplicationName()));
            }
            if (StringUtils.isNotBlank(pageReqVO.getRequestUrl())) {
                predicateList.add(cb.like(root.get("requestUrl"), "%" + pageReqVO.getRequestUrl() + "%"));
            }
            if (Objects.nonNull(pageReqVO.getBeginTime())) {
                predicateList.add(cb.greaterThanOrEqualTo(root.get("beginTime"), pageReqVO.getBeginTime()));
            }
            if (Objects.nonNull(pageReqVO.getDuration())) {
                predicateList.add(cb.ge(root.get("duration"), pageReqVO.getDuration()));
            }
            if (Objects.nonNull(pageReqVO.getResultCode())) {
                predicateList.add(cb.ge(root.get("resultCode"), pageReqVO.getResultCode()));
            }

            cb.desc(root.get("id"));
            return cb.and(predicateList.toArray(new Predicate[0]));
        };
    }

    @Override
    public List<ApiAccessLogDO> getApiAccessLogList(ApiAccessLogPageReqVO exportReqVO) {
        return apiAccessLogRepository.findAll(getApiAccessLogSpecification(exportReqVO));
    }

}
