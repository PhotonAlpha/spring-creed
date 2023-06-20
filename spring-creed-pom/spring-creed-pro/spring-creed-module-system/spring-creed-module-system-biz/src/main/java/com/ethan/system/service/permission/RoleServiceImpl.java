package com.ethan.system.service.permission;

import com.ethan.common.constant.CommonStatusEnum;
import com.ethan.common.pojo.BaseXDO;
import com.ethan.common.pojo.PageResult;
import com.ethan.common.utils.collection.CollUtils;
import com.ethan.security.websecurity.constant.DataScopeEnum;
import com.ethan.security.websecurity.constant.RoleTypeEnum;
import com.ethan.security.websecurity.entity.CreedAuthorities;
import com.ethan.security.websecurity.repository.CreedAuthorityRepository;
import com.ethan.system.constant.permission.RoleCodeEnum;
import com.ethan.system.controller.admin.permission.vo.role.RoleCreateReqVO;
import com.ethan.system.controller.admin.permission.vo.role.RoleExportReqVO;
import com.ethan.system.controller.admin.permission.vo.role.RolePageReqVO;
import com.ethan.system.controller.admin.permission.vo.role.RoleSimpleRespVO;
import com.ethan.system.controller.admin.permission.vo.role.RoleUpdateReqVO;
import com.ethan.system.convert.permission.AuthorityConvert;
import com.ethan.system.mq.producer.permission.RoleProducer;
import com.google.common.annotations.VisibleForTesting;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import jakarta.persistence.criteria.Predicate;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
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

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import static com.ethan.common.exception.util.ServiceExceptionUtil.exception;
import static com.ethan.system.constant.ErrorCodeConstants.ROLE_ADMIN_CODE_ERROR;
import static com.ethan.system.constant.ErrorCodeConstants.ROLE_CAN_NOT_UPDATE_SYSTEM_TYPE_ROLE;
import static com.ethan.system.constant.ErrorCodeConstants.ROLE_CODE_DUPLICATE;
import static com.ethan.system.constant.ErrorCodeConstants.ROLE_IS_DISABLE;
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
     * key：角色编号 {@link CreedAuthorities#getId()}
     *
     * 这里声明 volatile 修饰的原因是，每次刷新时，直接修改指向
     */
    @Getter
    // private volatile Map<String, CreedAuthorities> roleCache;
    private AtomicReference<Map<String, CreedAuthorities>> roleCache = new AtomicReference<>(Collections.emptyMap());

    /**
     * 缓存角色的最大更新时间，用于后续的增量轮询，判断是否有更新
     */
    @Getter
    // private volatile ZonedDateTime maxUpdateTime;
    private AtomicReference<ZonedDateTime> maxUpdateTime = new AtomicReference<>();

    @Resource
    private PermissionService permissionService;

    // @Resource
    // private RoleRepository roleRepository;
    @Resource
    private CreedAuthorityRepository authorityRepository;

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
        List<CreedAuthorities> roleList = loadRoleIfUpdate(maxUpdateTime.get());
        if (CollectionUtils.isEmpty(roleList)) {
            return;
        }

        // 写入缓存
        roleCache.getAndSet(CollUtils.convertMap(roleList, CreedAuthorities::getId));
        maxUpdateTime.getAndSet(CollUtils.getMaxValue(roleList, BaseXDO::getUpdateTime));
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
    private List<CreedAuthorities> loadRoleIfUpdate(ZonedDateTime maxUpdateTime) {
        // 第一步，判断是否要更新。
        if (maxUpdateTime == null) { // 如果更新时间为空，说明 DB 一定有新数据
            log.info("[loadRoleIfUpdate][首次加载全量角色]");
        } else { // 判断数据库中是否有更新的角色
            if (authorityRepository.countByUpdateTimeGreaterThan(maxUpdateTime) == 0) {
                return Collections.emptyList();
            }
            log.info("[loadRoleIfUpdate][增量加载全量角色]");
        }
        // 第二步，如果有更新，则从数据库加载所有角色
        return authorityRepository.findAll();
    }

    @Override
    @Transactional
    public String createAuthority(RoleCreateReqVO reqVO, Integer type) {
        // 校验角色
        checkDuplicateRole(reqVO.getDescription(), reqVO.getAuthority(), null);
        // 插入到数据库
        CreedAuthorities authority = AuthorityConvert.INSTANCE.convert(reqVO);
        authority.setType(ObjectUtils.defaultIfNull(RoleTypeEnum.findByType(type), RoleTypeEnum.CUSTOM));
        authority.setDataScope(DataScopeEnum.ALL); // 默认可查看所有数据。原因是，可能一些项目不需要项目权限
        authorityRepository.save(authority);
        // 发送刷新消息
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                roleProducer.sendRoleRefreshMessage();
            }
        });
        // 返回
        return authority.getId();
    }

    @Override
    public void updateRole(RoleUpdateReqVO reqVO) {
        // 校验是否可以更新
        CreedAuthorities authorities = checkUpdateRole(reqVO.getId());
        // 校验角色的唯一字段是否重复
        checkDuplicateRole(reqVO.getDescription(), reqVO.getAuthority(), reqVO.getId());

        // 更新到数据库
        AuthorityConvert.INSTANCE.update(reqVO, authorities);
        authorityRepository.save(authorities);
        // 发送刷新消息
        roleProducer.sendRoleRefreshMessage();
    }

    @Override
    public void updateRoleStatus(String id, Integer status) {
        // 校验是否可以更新
        CreedAuthorities authorities = checkUpdateRole(id);
        // 更新状态
        authorities.setEnabled(CommonStatusEnum.convert(status));
        authorityRepository.save(authorities);
        // 发送刷新消息
        roleProducer.sendRoleRefreshMessage();
    }

    @Override
    public void updateRoleDataScope(String id, Integer dataScope, Set<Long> dataScopeDeptIds) {
        // 校验是否可以更新
        CreedAuthorities authorities = checkUpdateRole(id);
        // 更新数据范围
        authorities.setDataScope(DataScopeEnum.findByDataScope(dataScope));
        authorities.setDataScopeDeptIds(dataScopeDeptIds);
        authorityRepository.save(authorities);
        // 发送刷新消息
        roleProducer.sendRoleRefreshMessage();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRole(String id) {
        // 校验是否可以更新
        this.checkUpdateRole(id);
        // 标记删除
        authorityRepository.deleteById(id);
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
    public CreedAuthorities getRoleFromCache(String id) {
        return roleCache.get().get(id);
    }

    @Override
    public List<CreedAuthorities> getRoles(@Nullable Collection<Integer> statuses) {
        if (CollectionUtils.isEmpty(statuses)) {
    		return authorityRepository.findAll();
		}
        return authorityRepository.findByEnabledIn(statuses);
    }

    @Override
    public List<CreedAuthorities> getRolesFromCache(Collection<String> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Collections.emptyList();
        }
        return roleCache.get().values().stream().filter(roleDO -> ids.contains(roleDO.getId()))
                .toList();
    }

    @Override
    public boolean hasAnySuperAdmin(Collection<CreedAuthorities> roleList) {
        if (CollectionUtils.isEmpty(roleList)) {
            return false;
        }
        return roleList.stream().anyMatch(role -> RoleCodeEnum.isSuperAdmin(role.getAuthority()));
    }

    @Override
    public CreedAuthorities getRole(String id) {
        return authorityRepository.findById(id).orElse(null);
    }

    @Override
    public PageResult<RoleSimpleRespVO> getRolePage(RolePageReqVO reqVO) {
        Page<CreedAuthorities> page = authorityRepository.findAll(getRoleSpecification(reqVO), PageRequest.of(reqVO.getPageNo(), reqVO.getPageSize()));
        List<RoleSimpleRespVO> roleSimpleResp = AuthorityConvert.INSTANCE.convertList02(page.getContent());
        return new PageResult<>(roleSimpleResp, page.getTotalElements());
    }


    private static Specification<CreedAuthorities> getRoleSpecification(RolePageReqVO reqVO) {
        return (Specification<CreedAuthorities>) (root, query, cb) -> {
            List<Predicate> predicateList = new ArrayList<>();
            if (org.apache.commons.lang3.StringUtils.isNotBlank(reqVO.getDescription())) {
                predicateList.add(cb.like(root.get("description"),
                        "%" + reqVO.getDescription() + "%"));
            }
            if (org.apache.commons.lang3.StringUtils.isNotBlank(reqVO.getAuthority())) {
                predicateList.add(cb.like(root.get("authority"),
                        "%" + reqVO.getAuthority() + "%"));
            }
            if (Objects.nonNull(reqVO.getStatus())) {
                predicateList.add(cb.equal(root.get("enabled"), reqVO.getStatus()));
            }
            if (Objects.nonNull(reqVO.getStatus())) {
                predicateList.add(cb.greaterThan(root.get("createTime"), reqVO.getCreateTime()));
            }
            return cb.and(predicateList.toArray(new Predicate[0]));
        };
    }
    private static Specification<CreedAuthorities> getExportReqSpecification(RoleExportReqVO reqVO) {
        return (Specification<CreedAuthorities>) (root, query, cb) -> {
            List<Predicate> predicateList = new ArrayList<>();
            if (org.apache.commons.lang3.StringUtils.isNotBlank(reqVO.getDescription())) {
                predicateList.add(cb.like(root.get("description"),
                        "%" + reqVO.getDescription() + "%"));
            }
            if (org.apache.commons.lang3.StringUtils.isNotBlank(reqVO.getAuthority())) {
                predicateList.add(cb.like(root.get("authority"),
                        "%" + reqVO.getAuthority() + "%"));
            }
            if (Objects.nonNull(reqVO.getStatus())) {
                predicateList.add(cb.equal(root.get("enabled"), reqVO.getStatus()));
            }
            if (Objects.nonNull(reqVO.getStatus())) {
                predicateList.add(cb.greaterThan(root.get("createTime"), reqVO.getCreateTime()));
            }
            return cb.and(predicateList.toArray(new Predicate[0]));
        };
    }

    @Override
    public List<CreedAuthorities> getRoleList(RoleExportReqVO reqVO) {
        return authorityRepository.findAll(getExportReqSpecification(reqVO));
    }

    /**
     * 校验角色的唯一字段是否重复
     *
     * 1. 是否存在相同名字的角色
     * 2. 是否存在相同编码的角色
     *
     * @param description 角色名字
     * @param authority 角色额编码
     * @param id 角色编号
     */
    @VisibleForTesting
    public void checkDuplicateRole(String description, String authority, String id) {
        // 0. 超级管理员，不允许创建
        if (RoleCodeEnum.isSuperAdmin(authority)) {
            throw exception(ROLE_ADMIN_CODE_ERROR, authority);
        }
        // 1. 该 name 名字被其它角色所使用
        Optional<CreedAuthorities> roleOptional = authorityRepository.findByAuthority(authority);
        if (roleOptional.map(CreedAuthorities::getId)
                .filter(s -> !StringUtils.equals(s, id))
                .isPresent()) {
            throw exception(ROLE_CODE_DUPLICATE, authority);
        }
    }

    /**
     * 校验角色是否可以被更新
     *
     * @param id 角色编号
     * @return
     */
    @VisibleForTesting
    public CreedAuthorities checkUpdateRole(String id) {
        Optional<CreedAuthorities> roleOptional = authorityRepository.findById(id);
        if (roleOptional.isEmpty()) {
            throw exception(ROLE_NOT_EXISTS);
        }
        // 内置角色，不允许删除
        if (RoleTypeEnum.SYSTEM.getType().equals(roleOptional.get().getType())) {
            throw exception(ROLE_CAN_NOT_UPDATE_SYSTEM_TYPE_ROLE);
        }
        return roleOptional.get();
    }

    @Override
    public void validRoles(Collection<String> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return;
        }
        // 获得角色信息
        List<CreedAuthorities> authorities = authorityRepository.findAllById(ids);
        Map<String, CreedAuthorities> roleMap = CollUtils.convertMap(authorities, CreedAuthorities::getId);
        // 校验
        ids.forEach(id -> {
            CreedAuthorities authorities1 = roleMap.get(id);
            if (authorities1 == null) {
                throw exception(ROLE_NOT_EXISTS);
            }
            if (!CommonStatusEnum.ENABLE.equals(authorities1.getEnabled())) {
                throw exception(ROLE_IS_DISABLE, authorities1.getAuthority());
            }
        });
    }
}
