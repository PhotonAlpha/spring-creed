package com.ethan.system.service.permission;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.ethan.common.constant.CommonStatusEnum;
import com.ethan.common.pojo.BaseVersioningXDO;
import com.ethan.common.pojo.PageResult;
import com.ethan.common.utils.ApplicationContextHolder;
import com.ethan.common.utils.collection.CollUtils;
import com.ethan.common.utils.json.JacksonUtils;
import com.ethan.security.websecurity.constant.DataScopeEnum;
import com.ethan.security.websecurity.constant.RoleTypeEnum;
import com.ethan.system.constant.permission.RoleCodeEnum;
import com.ethan.system.controller.admin.permission.vo.role.RolePageReqVO;
import com.ethan.system.controller.admin.permission.vo.role.RoleSaveReqVO;
import com.ethan.system.convert.permission.SystemRolesConvert;
import com.ethan.system.dal.entity.permission.SystemRoles;
import com.ethan.system.dal.redis.RedisKeyConstants;
import com.ethan.system.dal.repository.permission.SystemRolesRepository;
import com.ethan.system.mq.producer.permission.RoleProducer;
import com.google.common.annotations.VisibleForTesting;
import com.mzt.logapi.context.LogRecordContext;
import com.mzt.logapi.service.impl.DiffParseFunction;
import com.mzt.logapi.starter.annotation.LogRecord;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import static com.ethan.common.exception.util.ServiceExceptionUtil.exception;
import static com.ethan.system.api.constant.LogRecordConstants.SYSTEM_ROLE_CREATE_SUB_TYPE;
import static com.ethan.system.api.constant.LogRecordConstants.SYSTEM_ROLE_CREATE_SUCCESS;
import static com.ethan.system.api.constant.LogRecordConstants.SYSTEM_ROLE_DELETE_SUB_TYPE;
import static com.ethan.system.api.constant.LogRecordConstants.SYSTEM_ROLE_DELETE_SUCCESS;
import static com.ethan.system.api.constant.LogRecordConstants.SYSTEM_ROLE_TYPE;
import static com.ethan.system.api.constant.LogRecordConstants.SYSTEM_ROLE_UPDATE_SUB_TYPE;
import static com.ethan.system.api.constant.LogRecordConstants.SYSTEM_ROLE_UPDATE_SUCCESS;
import static com.ethan.system.constant.ErrorCodeConstants.ROLE_ADMIN_CODE_ERROR;
import static com.ethan.system.constant.ErrorCodeConstants.ROLE_CAN_NOT_UPDATE_SYSTEM_TYPE_ROLE;
import static com.ethan.system.constant.ErrorCodeConstants.ROLE_IS_DISABLE;
import static com.ethan.system.constant.ErrorCodeConstants.ROLE_NAME_CODE_DUPLICATE;
import static com.ethan.system.constant.ErrorCodeConstants.ROLE_NOT_EXISTS;


/**
 * 角色 Service 实现类
 *
 * 
 */
@Service
@Slf4j
public class RoleServiceImpl implements RoleService {

    @Resource
    private PermissionService permissionService;

    @Resource
    private SystemRolesRepository rolesRepository;

    @Resource
    private RoleProducer roleProducer;

    @Resource
    @Lazy // 注入自己，所以延迟加载
    private RoleService self;

    @Override
    @Transactional(rollbackFor = Exception.class)
    @LogRecord(type = SYSTEM_ROLE_TYPE, subType = SYSTEM_ROLE_CREATE_SUB_TYPE, bizNo = "{{#_ret}}",
            success = SYSTEM_ROLE_CREATE_SUCCESS)
    public Long createRole(RoleSaveReqVO reqVO, Integer type) {
        log.info("hit createRole:{} type:{}", JacksonUtils.toJsonString(reqVO), type);
        // 1. 校验角色
        validateRoleDuplicate(reqVO.getName(), reqVO.getCode(), null);

        // 2. 插入到数据库
        SystemRoles role = SystemRolesConvert.INSTANCE.convert(reqVO);
        role.setType(RoleTypeEnum.findByType(type));
        rolesRepository.save(role);

        // 3. 记录操作日志上下文
        LogRecordContext.putVariable("role", role);

        // 发送刷新消息. 注意，需要事务提交后，在进行发送刷新消息。不然 db 还未提交，结果缓存先刷新了
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {

            @Override
            public void afterCommit() {
                roleProducer.sendRoleRefreshMessage();
            }

        });
        return role.getId();
    }

    @Override
    @CacheEvict(value = RedisKeyConstants.ROLE, key = "#updateReqVO.id")
    @LogRecord(type = SYSTEM_ROLE_TYPE, subType = SYSTEM_ROLE_UPDATE_SUB_TYPE, bizNo = "{{#updateReqVO.id}}",
            success = SYSTEM_ROLE_UPDATE_SUCCESS)
    public void updateRole(RoleSaveReqVO reqVO) {
        log.info("hit updateRole:{}", JacksonUtils.toJsonString(reqVO));
        // 1.1 校验是否可以更新
        SystemRoles role = validateRoleForUpdate(reqVO.getId());
        // 1.2 校验角色的唯一字段是否重复
        validateRoleDuplicate(reqVO.getName(), reqVO.getCode(), reqVO.getId());

        // 2. 更新到数据库
        SystemRolesConvert.INSTANCE.update(reqVO, role);
        rolesRepository.save(role);

        // 3. 记录操作日志上下文
        LogRecordContext.putVariable("role", role);
    }

    @Override
    @CacheEvict(value = RedisKeyConstants.ROLE, key = "#id")
    public void updateRoleDataScope(Long id, Integer dataScope, Set<Long> dataScopeDeptIds) {
        log.info("hit updateRoleDataScope:{} dataScope:{} dataScopeDeptIds:{}", id, dataScope, dataScopeDeptIds);
        // 校验是否可以更新
        SystemRoles role = validateRoleForUpdate(id);

        // 更新数据范围
        role.setDataScope(DataScopeEnum.findByDataScope(dataScope));
        role.setDataScopeDeptIds(dataScopeDeptIds);
        rolesRepository.save(role);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = RedisKeyConstants.ROLE, key = "#id")
    @LogRecord(type = SYSTEM_ROLE_TYPE, subType = SYSTEM_ROLE_DELETE_SUB_TYPE, bizNo = "{{#id}}",
            success = SYSTEM_ROLE_DELETE_SUCCESS)
    public void deleteRole(Long id) {
        log.info("hit deleteRole:{}", id);
        // 1. 校验是否可以更新
        SystemRoles role = validateRoleForUpdate(id);

        // 2.1 标记删除
        rolesRepository.deleteById(id);
        // 2.2 删除相关数据
        permissionService.processRoleDeleted(id);

        // 3. 记录操作日志上下文
        LogRecordContext.putVariable(DiffParseFunction.OLD_OBJECT, SystemRolesConvert.INSTANCE.convert0(role));
        LogRecordContext.putVariable("role", role);
    }

    @Override
    public SystemRoles getRole(Long id) {
        log.info("hit getRole:{}", id);
        return rolesRepository.findById(id).orElseThrow(() -> exception(ROLE_NOT_EXISTS));
    }

    @Override
    public PageResult<SystemRoles> getRolePage(RolePageReqVO reqVO) {
        log.info("hit getRolePage:{}", JacksonUtils.toJsonString(reqVO));
        Page<SystemRoles> page = rolesRepository.findByPage(reqVO);
        return new PageResult<>(page.getContent(), page.getTotalElements());
    }



    @Override
    @Cacheable(value = RedisKeyConstants.ROLE, key = "#id",
            unless = "#result == null")
    public SystemRoles getRoleFromCache(Long id) {
        log.info("hit getRoleFromCache:{}", id);
        return getRole(id);
    }

    @Override
    public List<SystemRoles> getRoleList(Collection<Long> ids) {
        log.info("hit getRoleList:{}", ids);
        return rolesRepository.findAllById(ids);
    }

    @Override
    public List<SystemRoles> getRoleListFromCache(Collection<Long> ids) {
        log.info("hit getRoleListFromCache:{}", ids);
        if (CollectionUtil.isEmpty(ids)) {
            return Collections.emptyList();
        }
        // 这里采用 for 循环从缓存中获取，主要考虑 Spring CacheManager 无法批量操作的问题
        return CollUtils.convertList(ids, self::getRoleFromCache);
    }

    @Override
    public List<SystemRoles> getRoleListByStatus(Collection<Integer> statuses) {
        log.info("hit getRoleListByStatus:{}", statuses);
        List<CommonStatusEnum> statusEnumList = Optional.ofNullable(statuses).orElse(Collections.emptyList())
                .stream().map(CommonStatusEnum::convert).toList();
        return rolesRepository.findByEnabledIn(statusEnumList);
    }

    @Override
    public List<SystemRoles> getRoleList() {
        log.info("hit getRoleList");
        return rolesRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
    }

    @Override
    public boolean hasAnySuperAdmin(Collection<Long> ids) {
        log.info("hit hasAnySuperAdmin:{}", ids);
        if (CollUtil.isEmpty(ids)) {
            return false;
        }
        return ids.stream().map(self::getRoleFromCache)
                .filter(Objects::nonNull)
                .map(SystemRoles::getCode)
                .anyMatch(RoleCodeEnum::isSuperAdmin);
    }

    @Override
    public void validateRoleList(Collection<Long> ids) {
        log.info("hit validateRoleList:{}", ids);
        if (CollUtil.isEmpty(ids)) {
            return;
        }
        // 获得角色信息
        List<SystemRoles> roles = getRoleList(ids);
        // 校验
        roles.forEach(role -> {
            CommonStatusEnum status = Optional.ofNullable(role)
                    .map(BaseVersioningXDO::getEnabled)
                    .orElseThrow(() -> exception(ROLE_NOT_EXISTS));
            if (!CommonStatusEnum.ENABLE.equals(status)) {
                throw exception(ROLE_IS_DISABLE, role.getName());
            }
        });
    }
    /**
     * 校验角色是否可以被更新
     *
     * @param id 角色编号
     */
    @VisibleForTesting
    SystemRoles validateRoleForUpdate(Long id) {
        Optional<SystemRoles> rolesOptional = rolesRepository.findById(id);
        if (rolesOptional.isEmpty()) {
            throw exception(ROLE_NOT_EXISTS);
        }
        // 内置角色，不允许修改/删除
        return rolesOptional.filter(this::rolesTypePredicate)
                .orElseThrow(() -> exception(ROLE_CAN_NOT_UPDATE_SYSTEM_TYPE_ROLE));
    }

    private boolean rolesTypePredicate(SystemRoles role) {
        return Optional.ofNullable(role).map(SystemRoles::getType)
                .filter(RoleTypeEnum.SYSTEM::equals).isPresent();
    }

    /**
     * 校验角色的唯一字段是否重复
     *
     * 1. 是否存在相同名字的角色
     * 2. 是否存在相同编码的角色
     *
     * @param name 角色名字
     * @param code 角色额编码
     * @param id 角色编号
     */
    @VisibleForTesting
    void validateRoleDuplicate(String name, String code, Long id) {
        // 0. 超级管理员，不允许创建
        if (RoleCodeEnum.isSuperAdmin(code)) {
            throw exception(ROLE_ADMIN_CODE_ERROR, code);
        }
        // 1. 非空判断
        if (!StringUtils.hasText(code) || !StringUtils.hasText(name)) {
            return;
        }
        // 2. 该 name/code 名字被其它角色所使用
        if (Objects.isNull(id)) {
            if (rolesRepository.existsByCodeOrName(code, name)) {
                throw exception(ROLE_NAME_CODE_DUPLICATE, name, code);
            }
        } else {
            rolesRepository.findByCode(code)
                    .map(SystemRoles::getId)
                    .filter(id::equals)
                    .orElseThrow(() -> exception(ROLE_NAME_CODE_DUPLICATE, name, code));
        }
    }
}
