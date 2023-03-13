package com.ethan.system.service.permission;

import com.ethan.common.constant.CommonStatusEnum;
import com.ethan.common.utils.collection.CollUtils;
import com.ethan.system.constant.permission.DataScopeEnum;
import com.ethan.system.constant.permission.RoleCodeEnum;
import com.ethan.system.constant.permission.RoleTypeEnum;
import com.ethan.system.controller.admin.permission.vo.role.RoleCreateReqVO;
import com.ethan.system.controller.admin.permission.vo.role.RoleExportReqVO;
import com.ethan.system.controller.admin.permission.vo.role.RolePageReqVO;
import com.ethan.system.controller.admin.permission.vo.role.RoleUpdateReqVO;
import com.ethan.system.convert.permission.RoleConvert;
import com.ethan.system.dal.entity.permission.RoleDO;
import com.ethan.system.dal.repository.permission.RoleRepository;
import com.ethan.system.mq.permission.RoleProducer;
import com.google.common.annotations.VisibleForTesting;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import jakarta.persistence.criteria.Predicate;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.ethan.common.exception.util.ServiceExceptionUtil.exception;
import static com.ethan.system.constant.ErrorCodeConstants.ROLE_ADMIN_CODE_ERROR;
import static com.ethan.system.constant.ErrorCodeConstants.ROLE_CAN_NOT_UPDATE_SYSTEM_TYPE_ROLE;
import static com.ethan.system.constant.ErrorCodeConstants.ROLE_CODE_DUPLICATE;
import static com.ethan.system.constant.ErrorCodeConstants.ROLE_IS_DISABLE;
import static com.ethan.system.constant.ErrorCodeConstants.ROLE_NAME_DUPLICATE;
import static com.ethan.system.constant.ErrorCodeConstants.ROLE_NOT_EXISTS;


/**
 * 角色 Service 实现类
 *
 * 
 */
@Service
@Slf4j
public class RoleServiceImpl implements RoleService {

    /**
     * 定时执行 {@link #schedulePeriodicRefresh()} 的周期
     * 因为已经通过 Redis Pub/Sub 机制，所以频率不需要高
     */
    private static final long SCHEDULER_PERIOD = 5 * 60 * 1000L;

    /**
     * 角色缓存
     * key：角色编号 {@link RoleDO#getId()}
     *
     * 这里声明 volatile 修饰的原因是，每次刷新时，直接修改指向
     */
    @Getter
    private volatile Map<Long, RoleDO> roleCache;
    /**
     * 缓存角色的最大更新时间，用于后续的增量轮询，判断是否有更新
     */
    @Getter
    private volatile Instant maxUpdateTime;

    @Resource
    private PermissionService permissionService;

    @Resource
    private RoleRepository roleRepository;

    @Resource
    private RoleProducer roleProducer;

    @Resource
    @Lazy // 注入自己，所以延迟加载
    private RoleService self;

    /**
     * 初始化 {@link #roleCache} 缓存
     */
    @Override
    @PostConstruct
    // @TenantIgnore // 忽略自动多租户，全局初始化缓存
    public void initLocalCache() {
        // 获取角色列表，如果有更新
        List<RoleDO> roleList = loadRoleIfUpdate(maxUpdateTime);
        if (CollectionUtils.isEmpty(roleList)) {
            return;
        }

        // 写入缓存
        roleCache = CollUtils.convertMap(roleList, RoleDO::getId);
        maxUpdateTime = CollUtils.getMaxValue(roleList, RoleDO::getUpdateTime);
        log.info("[initLocalCache][初始化 Role 数量为 {}]", roleList.size());
    }

    @Scheduled(fixedDelay = SCHEDULER_PERIOD, initialDelay = SCHEDULER_PERIOD)
    public void schedulePeriodicRefresh() {
       self.initLocalCache();
    }

    /**
     * 如果角色发生变化，从数据库中获取最新的全量角色。
     * 如果未发生变化，则返回空
     *
     * @param maxUpdateTime 当前角色的最大更新时间
     * @return 角色列表
     */
    private List<RoleDO> loadRoleIfUpdate(Instant maxUpdateTime) {
        // 第一步，判断是否要更新。
        if (maxUpdateTime == null) { // 如果更新时间为空，说明 DB 一定有新数据
            log.info("[loadRoleIfUpdate][首次加载全量角色]");
        } else { // 判断数据库中是否有更新的角色
            if (roleRepository.countByUpdateTimeGreaterThan(LocalDateTime.ofInstant(maxUpdateTime, ZoneId.systemDefault())) == 0) {
                return null;
            }
            log.info("[loadRoleIfUpdate][增量加载全量角色]");
        }
        // 第二步，如果有更新，则从数据库加载所有角色
        return roleRepository.findAll();
    }

    @Override
    @Transactional
    public Long createRole(RoleCreateReqVO reqVO, Integer type) {
        // 校验角色
        checkDuplicateRole(reqVO.getName(), reqVO.getCode(), null);
        // 插入到数据库
        RoleDO role = RoleConvert.INSTANCE.convert(reqVO);
        role.setType(ObjectUtils.defaultIfNull(type, RoleTypeEnum.CUSTOM.getType()));
        role.setStatus(CommonStatusEnum.ENABLE.getStatus());
        role.setDataScope(DataScopeEnum.ALL.getScope()); // 默认可查看所有数据。原因是，可能一些项目不需要项目权限
        roleRepository.save(role);
        // 发送刷新消息
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                roleProducer.sendRoleRefreshMessage();
            }
        });
        // 返回
        return role.getId();
    }

    @Override
    public void updateRole(RoleUpdateReqVO reqVO) {
        // 校验是否可以更新
        checkUpdateRole(reqVO.getId());
        // 校验角色的唯一字段是否重复
        checkDuplicateRole(reqVO.getName(), reqVO.getCode(), reqVO.getId());

        // 更新到数据库
        RoleDO updateObject = RoleConvert.INSTANCE.convert(reqVO);
        roleRepository.save(updateObject);
        // 发送刷新消息
        roleProducer.sendRoleRefreshMessage();
    }

    @Override
    public void updateRoleStatus(Long id, Integer status) {
        // 校验是否可以更新
        checkUpdateRole(id);
        // 更新状态
        RoleDO updateObject = new RoleDO();
        updateObject.setId(id);
        updateObject.setStatus(status);
        roleRepository.save(updateObject);
        // 发送刷新消息
        roleProducer.sendRoleRefreshMessage();
    }

    @Override
    public void updateRoleDataScope(Long id, Integer dataScope, Set<Long> dataScopeDeptIds) {
        // 校验是否可以更新
        checkUpdateRole(id);
        // 更新数据范围
        RoleDO updateObject = new RoleDO();
        updateObject.setId(id);
        updateObject.setDataScope(dataScope);
        updateObject.setDataScopeDeptIds(dataScopeDeptIds);
        roleRepository.save(updateObject);
        // 发送刷新消息
        roleProducer.sendRoleRefreshMessage();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRole(Long id) {
        // 校验是否可以更新
        this.checkUpdateRole(id);
        // 标记删除
        roleRepository.deleteById(id);
        // 删除相关数据
        permissionService.processRoleDeleted(id);
        // 发送刷新消息. 注意，需要事务提交后，在进行发送刷新消息。不然 db 还未提交，结果缓存先刷新了
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {

            @Override
            public void afterCommit() {
                roleProducer.sendRoleRefreshMessage();
            }

        });
    }

    @Override
    public RoleDO getRoleFromCache(Long id) {
        return roleCache.get(id);
    }

    @Override
    public List<RoleDO> getRoles(@Nullable Collection<Integer> statuses) {
        if (CollectionUtils.isEmpty(statuses)) {
    		return roleRepository.findAll();
		}
        return roleRepository.findByStatusIn(statuses);
    }

    @Override
    public List<RoleDO> getRolesFromCache(Collection<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Collections.emptyList();
        }
        return roleCache.values().stream().filter(roleDO -> ids.contains(roleDO.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean hasAnySuperAdmin(Collection<RoleDO> roleList) {
        if (CollectionUtils.isEmpty(roleList)) {
            return false;
        }
        return roleList.stream().anyMatch(role -> RoleCodeEnum.isSuperAdmin(role.getCode()));
    }

    @Override
    public RoleDO getRole(Long id) {
        return roleRepository.findById(id).orElse(null);
    }

    @Override
    public Page<RoleDO> getRolePage(RolePageReqVO reqVO) {
        return roleRepository.findAll(getRoleSpecification(reqVO), PageRequest.of(reqVO.getPageNo(), reqVO.getPageSize()));
    }


    private static Specification<RoleDO> getRoleSpecification(RolePageReqVO reqVO) {
        return (Specification<RoleDO>) (root, query, cb) -> {
            List<Predicate> predicateList = new ArrayList<>();
            if (org.apache.commons.lang3.StringUtils.isNotBlank(reqVO.getName())) {
                predicateList.add(cb.like(root.get("name"),
                        "%" + reqVO.getName() + "%"));
            }
            if (org.apache.commons.lang3.StringUtils.isNotBlank(reqVO.getCode())) {
                predicateList.add(cb.like(root.get("code"),
                        "%" + reqVO.getCode() + "%"));
            }
            if (Objects.nonNull(reqVO.getStatus())) {
                predicateList.add(cb.equal(root.get("status"), reqVO.getStatus()));
            }
            if (Objects.nonNull(reqVO.getStatus())) {
                predicateList.add(cb.greaterThan(root.get("createTime"), reqVO.getCreateTime()));
            }
            return cb.and(predicateList.toArray(new Predicate[0]));
        };
    }
    private static Specification<RoleDO> getExportReqSpecification(RoleExportReqVO reqVO) {
        return (Specification<RoleDO>) (root, query, cb) -> {
            List<Predicate> predicateList = new ArrayList<>();
            if (org.apache.commons.lang3.StringUtils.isNotBlank(reqVO.getName())) {
                predicateList.add(cb.like(root.get("name"),
                        "%" + reqVO.getName() + "%"));
            }
            if (org.apache.commons.lang3.StringUtils.isNotBlank(reqVO.getCode())) {
                predicateList.add(cb.like(root.get("code"),
                        "%" + reqVO.getCode() + "%"));
            }
            if (Objects.nonNull(reqVO.getStatus())) {
                predicateList.add(cb.equal(root.get("status"), reqVO.getStatus()));
            }
            if (Objects.nonNull(reqVO.getStatus())) {
                predicateList.add(cb.greaterThan(root.get("createTime"), reqVO.getCreateTime()));
            }
            return cb.and(predicateList.toArray(new Predicate[0]));
        };
    }

    @Override
    public List<RoleDO> getRoleList(RoleExportReqVO reqVO) {
        return roleRepository.findAll(getExportReqSpecification(reqVO));
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
    public void checkDuplicateRole(String name, String code, Long id) {
        // 0. 超级管理员，不允许创建
        if (RoleCodeEnum.isSuperAdmin(code)) {
            throw exception(ROLE_ADMIN_CODE_ERROR, code);
        }
        // 1. 该 name 名字被其它角色所使用
        Optional<RoleDO> roleOptional = roleRepository.findByName(name);
        if (roleOptional.isPresent() && !roleOptional.get().getId().equals(id)) {
            throw exception(ROLE_NAME_DUPLICATE, name);
        }
        // 2. 是否存在相同编码的角色
        if (!StringUtils.hasText(code)) {
            return;
        }
        // 该 code 编码被其它角色所使用
        roleOptional = roleRepository.findByCode(code);
        if (roleOptional.isPresent() && !roleOptional.get().getId().equals(id)) {
            throw exception(ROLE_CODE_DUPLICATE, code);
        }
    }

    /**
     * 校验角色是否可以被更新
     *
     * @param id 角色编号
     */
    @VisibleForTesting
    public void checkUpdateRole(Long id) {
        Optional<RoleDO> roleOptional = roleRepository.findById(id);
        if (roleOptional.isEmpty()) {
            throw exception(ROLE_NOT_EXISTS);
        }
        // 内置角色，不允许删除
        if (RoleTypeEnum.SYSTEM.getType().equals(roleOptional.get().getType())) {
            throw exception(ROLE_CAN_NOT_UPDATE_SYSTEM_TYPE_ROLE);
        }
    }

    @Override
    public void validRoles(Collection<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return;
        }
        // 获得角色信息
        List<RoleDO> roles = roleRepository.findAllById(ids);
        Map<Long, RoleDO> roleMap = CollUtils.convertMap(roles, RoleDO::getId);
        // 校验
        ids.forEach(id -> {
            RoleDO role = roleMap.get(id);
            if (role == null) {
                throw exception(ROLE_NOT_EXISTS);
            }
            if (!CommonStatusEnum.ENABLE.getStatus().equals(role.getStatus())) {
                throw exception(ROLE_IS_DISABLE, role.getName());
            }
        });
    }
}
