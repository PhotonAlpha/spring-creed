package com.ethan.system.service.logger;

import com.ethan.common.pojo.PageResult;
import com.ethan.framework.logger.core.dto.LoginLogCreateReqDTO;
import com.ethan.framework.logger.core.vo.loginlog.LoginLogExportReqVO;
import com.ethan.framework.logger.core.vo.loginlog.LoginLogPageReqVO;
import com.ethan.framework.operatelog.converter.LoginLogConvert;
import com.ethan.framework.operatelog.entity.LoginLogDO;
import com.ethan.framework.operatelog.repository.LoginLogRepository;
import jakarta.annotation.Resource;
import jakarta.persistence.criteria.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 登录日志 Service 实现
 */
@Service
@Validated
public class LoginLogServiceImpl implements LoginLogService {

    @Resource
    private LoginLogRepository loginLogRepository;

    @Override
    public PageResult<LoginLogDO> getLoginLogPage(LoginLogPageReqVO reqVO) {
        Page<LoginLogDO> page = loginLogRepository.findAll(getLoginLogPageSpecification(reqVO), PageRequest.of(reqVO.getPageNo(), reqVO.getPageSize()));
        return new PageResult<>(page.getContent(), page.getTotalElements());
    }

    private static Specification<LoginLogDO> getLoginLogPageSpecification(LoginLogPageReqVO reqVO) {
        return (Specification<LoginLogDO>) (root, query, cb) -> {
            List<Predicate> predicateList = new ArrayList<>();
            if (StringUtils.isNotBlank(reqVO.getUserIp())) {
                predicateList.add(cb.like(root.get("user_ip"),
                        "%" + reqVO.getUserIp() + "%"));
            }
            if (StringUtils.isNotBlank(reqVO.getUsername())) {
                predicateList.add(cb.like(root.get("username"),
                        "%" + reqVO.getUsername() + "%"));
            }
            if (Objects.nonNull(reqVO.getStatus())) {
                predicateList.add(cb.equal(root.get("status"), reqVO.getStatus()));
            }
            cb.desc(root.get("id"));
            return cb.and(predicateList.toArray(new Predicate[0]));
        };
    }

    @Override
    public List<LoginLogDO> getLoginLogList(LoginLogExportReqVO reqVO) {
        return loginLogRepository.findAll(getLoginLogListSpecification(reqVO));
    }

    private static Specification<LoginLogDO> getLoginLogListSpecification(LoginLogExportReqVO reqVO) {
        return (Specification<LoginLogDO>) (root, query, cb) -> {
            List<Predicate> predicateList = new ArrayList<>();
            if (StringUtils.isNotBlank(reqVO.getUserIp())) {
                predicateList.add(cb.like(root.get("user_ip"),
                        "%" + reqVO.getUserIp() + "%"));
            }
            if (StringUtils.isNotBlank(reqVO.getUsername())) {
                predicateList.add(cb.like(root.get("username"),
                        "%" + reqVO.getUsername() + "%"));
            }
            if (Objects.nonNull(reqVO.getStatus())) {
                predicateList.add(cb.equal(root.get("status"), reqVO.getStatus()));
            }
            cb.desc(root.get("id"));
            return cb.and(predicateList.toArray(new Predicate[0]));
        };
    }
    @Override
    public void createLoginLog(LoginLogCreateReqDTO reqDTO) {
        LoginLogDO loginLog = LoginLogConvert.INSTANCE.convert(reqDTO);
        loginLogRepository.save(loginLog);
    }

}
