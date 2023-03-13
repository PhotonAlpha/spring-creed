package com.ethan.system.service.logger;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.ethan.common.pojo.PageResult;
import com.ethan.security.websecurity.entity.CreedConsumer;
import com.ethan.system.service.user.AdminUserService;
import com.ethan.framework.logger.core.dto.OperateLogCreateReqDTO;
import com.ethan.framework.logger.core.vo.operatelog.OperateLogExportReqVO;
import com.ethan.framework.logger.core.vo.operatelog.OperateLogPageReqVO;
import com.ethan.framework.operatelog.converter.OperateLogConvert;
import com.ethan.framework.operatelog.entity.OperateLogDO;
import com.ethan.framework.operatelog.repository.OperateLogRepository;
import jakarta.annotation.Resource;
import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static com.ethan.common.utils.collection.CollUtils.convertSet;
import static com.ethan.framework.operatelog.entity.OperateLogDO.JAVA_METHOD_ARGS_MAX_LENGTH;
import static com.ethan.framework.operatelog.entity.OperateLogDO.RESULT_MAX_LENGTH;

@Service
@Validated
@Slf4j
public class OperateLogServiceImpl implements OperateLogService {

    @Resource
    private OperateLogRepository operateLogRepository;

    @Resource
    private AdminUserService userService;

    @Override
    public void createOperateLog(OperateLogCreateReqDTO createReqDTO) {
        OperateLogDO logDO = OperateLogConvert.INSTANCE.convert(createReqDTO);
        logDO.setJavaMethodArgs(StringUtils.abbreviate(logDO.getJavaMethodArgs(), JAVA_METHOD_ARGS_MAX_LENGTH));
        logDO.setResultData(StringUtils.abbreviate(logDO.getResultData(), RESULT_MAX_LENGTH));
        operateLogRepository.save(logDO);
    }

    @Override
    public PageResult<OperateLogDO> getOperateLogPage(OperateLogPageReqVO reqVO) {
        // 处理基于用户昵称的查询
        Collection<String> userIds = null;
        if (StrUtil.isNotEmpty(reqVO.getUserNickname())) {
            userIds = convertSet(userService.getUsersByNickname(reqVO.getUserNickname()), CreedConsumer::getId);
            if (CollUtil.isEmpty(userIds)) {
                return PageResult.empty();
            }
        }
        // 查询分页
        Page<OperateLogDO> page = operateLogRepository.findAll(getOperateLogPageSpecification(reqVO, userIds), PageRequest.of(reqVO.getPageNo(), reqVO.getPageSize()));
        return new PageResult<>(page.getContent(), page.getTotalElements());
    }
    private static Specification<OperateLogDO> getOperateLogPageSpecification(OperateLogPageReqVO reqVO, Collection<String> userIds) {
        return (Specification<OperateLogDO>) (root, query, cb) -> {
            List<Predicate> predicateList = new ArrayList<>();
            if (StringUtils.isNotBlank(reqVO.getModule())) {
                predicateList.add(cb.like(root.get("module"),
                        "%" + reqVO.getModule() + "%"));
            }
            if (Objects.nonNull(reqVO.getType())) {
                predicateList.add(cb.equal(root.get("type"),reqVO.getType()));
            }
            if (Objects.nonNull(userIds)) {
                predicateList.add(root.get("user_id").in(userIds));
            }
            if (Objects.nonNull(reqVO.getSuccess())) {
                predicateList.add(cb.equal(root.get("success"), reqVO.getSuccess()));
            }
            if (Objects.nonNull(reqVO.getStartTime())) {
                predicateList.add(cb.greaterThan(root.get("start_time"), reqVO.getStartTime()));
            }
            cb.desc(root.get("id"));
            return cb.and(predicateList.toArray(new Predicate[0]));
        };
    }

    @Override
    public List<OperateLogDO> getOperateLogs(OperateLogExportReqVO reqVO) {
        // 处理基于用户昵称的查询
        Collection<String> userIds = null;
        if (StrUtil.isNotEmpty(reqVO.getUserNickname())) {
            userIds = convertSet(userService.getUsersByNickname(reqVO.getUserNickname()), CreedConsumer::getId);
            if (CollUtil.isEmpty(userIds)) {
                return Collections.emptyList();
            }
        }
        // 查询列表
        return operateLogRepository.findAll(getOperateLogsSpecification(reqVO, userIds));
    }

    private static Specification<OperateLogDO> getOperateLogsSpecification(OperateLogExportReqVO reqVO, Collection<String> userIds) {
        return (Specification<OperateLogDO>) (root, query, cb) -> {
            List<Predicate> predicateList = new ArrayList<>();
            if (StringUtils.isNotBlank(reqVO.getModule())) {
                predicateList.add(cb.like(root.get("module"),
                        "%" + reqVO.getModule() + "%"));
            }
            if (Objects.nonNull(reqVO.getType())) {
                predicateList.add(cb.equal(root.get("type"),reqVO.getType()));
            }
            if (Objects.nonNull(userIds)) {
                predicateList.add(root.get("user_id").in(userIds));
            }
            if (Objects.nonNull(reqVO.getSuccess())) {
                predicateList.add(cb.equal(root.get("success"), reqVO.getSuccess()));
            }
            if (Objects.nonNull(reqVO.getStartTime())) {
                predicateList.add(cb.greaterThan(root.get("start_time"), reqVO.getStartTime()));
            }
            cb.desc(root.get("id"));
            return cb.and(predicateList.toArray(new Predicate[0]));
        };
    }

}
