package com.ethan.system.service.permission;

import com.ethan.common.constant.CommonStatusEnum;
import com.ethan.common.utils.collection.CollUtils;
import com.ethan.common.utils.collection.MapUtils;
import com.ethan.common.utils.json.JacksonUtils;
import com.ethan.security.websecurity.constant.DataScopeEnum;
import com.ethan.security.websecurity.entity.CreedAuthorities;
import com.ethan.system.controller.admin.permission.vo.menu.DeptDataPermissionRespDTO;
import com.ethan.system.dal.entity.dept.DeptDO;
import com.ethan.system.dal.entity.permission.MenuDO;
import com.ethan.system.dal.entity.permission.RoleMenuDO;
import com.ethan.system.dal.repository.permission.RoleMenuRepository;
import com.ethan.system.mq.producer.permission.PermissionProducer;
import com.ethan.system.service.dept.DeptService;
import com.ethan.system.service.user.AdminUserService;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.CollectionUtils;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import static com.ethan.common.utils.collection.CollUtils.convertSet;
import static java.util.Collections.singleton;

/**
 * 权限 Service 实现类
 *
 * 
 */
@Service
@Slf4j
public class PermissionServiceImpl implements PermissionService {

    /**
     * 定时执行 {@link #schedulePeriodicRefresh()} 的周期
     * 因为已经通过 Redis Pub/Sub 机制，所以频率不需要高
     */
    private static final long SCHEDULER_PERIOD = 5 * 60 * 1000L;

    /**
     * 角色编号与菜单编号的缓存映射
     * key：角色编号
     * value：菜单编号的数组
     *
     * 这里声明 volatile 修饰的原因是，每次刷新时，直接修改指向
     */
    @Getter
    @Setter // 单元测试需要
    // private volatile Multimap<Long, Long> roleMenuCache;
    private AtomicReference<Multimap<String, Long>> roleMenuCache = new AtomicReference<>();
    /**
     * 菜单编号与角色编号的缓存映射
     * key：菜单编号
     * value：角色编号的数组
     *
     * 这里声明 volatile 修饰的原因是，每次刷新时，直接修改指向
     */
    @Getter
    @Setter // 单元测试需要
    // private volatile Multimap<Long, Long> menuRoleCache;
    private AtomicReference<Multimap<Long, String>> menuRoleCache = new AtomicReference<>();
    /**
     * 缓存 RoleMenu 的最大更新时间，用于后续的增量轮询，判断是否有更新
     */
    @Getter
    // private volatile ZonedDateTime roleMenuMaxUpdateTime;
    private AtomicReference<ZonedDateTime> roleMenuMaxUpdateTime = new AtomicReference<>();

    /**
     * 用户编号与角色编号的缓存映射
     * key：用户编号
     * value：角色编号的数组
     *
     * 这里声明 volatile 修饰的原因是，每次刷新时，直接修改指向
     */
    @Getter
    @Setter // 单元测试需要
    // private volatile Map<String, Set<String>> userRoleCache;
    private AtomicReference<Map<String, Set<String>>> userRoleCache = new AtomicReference<>();
    /**
     * 缓存 UserRole 的最大更新时间，用于后续的增量轮询，判断是否有更新
     */
    @Getter
    // private volatile ZonedDateTime userRoleMaxUpdateTime;
    private AtomicReference<ZonedDateTime> userRoleMaxUpdateTime = new AtomicReference<>();

    @Resource
    private RoleMenuRepository roleMenuRepository;
    @Resource
    private CreedUserService creedUserService;

    @Resource
    private RoleService roleService;
    @Resource
    private MenuService menuService;
    @Resource
    private DeptService deptService;
    @Resource
    private AdminUserService adminUserService;

    @Resource
    private PermissionProducer permissionProducer;

    @Resource
    @Lazy // 注入自己，所以延迟加载
    private PermissionService self;

    @Override
    @PostConstruct
    // @TenantIgnore // 初始化缓存，无需租户过滤
    public void initLocalCache() {
        initUserRoleLocalCache();
        initRoleMenuLocalCache();
    }

    /**
     * 初始化 {@link #roleMenuCache} 和 {@link #menuRoleCache} 缓存
     */
    @VisibleForTesting
    void initRoleMenuLocalCache() {
        // 获取角色与菜单的关联列表，如果有更新
        List<RoleMenuDO> roleMenuList = loadRoleMenuIfUpdate(roleMenuMaxUpdateTime.get());
        if (CollectionUtils.isEmpty(roleMenuList)) {
            return;
        }

        // 初始化 roleMenuCache 和 menuRoleCache 缓存
        ImmutableMultimap.Builder<String, Long> roleMenuCacheBuilder = ImmutableMultimap.builder();
        ImmutableMultimap.Builder<Long, String> menuRoleCacheBuilder = ImmutableMultimap.builder();
        roleMenuList.forEach(roleMenuDO -> {
            roleMenuCacheBuilder.put(roleMenuDO.getRoleId(), roleMenuDO.getMenuId());
            menuRoleCacheBuilder.put(roleMenuDO.getMenuId(), roleMenuDO.getRoleId());
        });
        roleMenuCache.getAndSet(roleMenuCacheBuilder.build());
        menuRoleCache.getAndSet(menuRoleCacheBuilder.build());
        roleMenuMaxUpdateTime.getAndSet(CollUtils.getMaxValue(roleMenuList, RoleMenuDO::getUpdateTime));
        log.info("[initRoleMenuLocalCache][初始化角色与菜单的关联数量为 {}]", roleMenuList.size());
    }

    /**
     * 初始化 {@link #userRoleCache} 缓存
     */
    @VisibleForTesting
    void initUserRoleLocalCache() {
        // 获取用户与角色的关联列表，如果有更新
        Pair<ZonedDateTime, Map<String, Set<String>>> result = creedUserService.initUserRoleLocalCache(userRoleMaxUpdateTime.get());
        if (Objects.isNull(result)) {
            return;
        }
        // 初始化 userRoleCache 缓存
        userRoleCache.getAndSet(result.getRight());
        userRoleMaxUpdateTime.getAndSet(result.getLeft());
    }

    @Scheduled(fixedDelay = SCHEDULER_PERIOD, initialDelay = SCHEDULER_PERIOD)
    public void schedulePeriodicRefresh() {
        self.initLocalCache();
    }

    /**
     * 如果角色与菜单的关联发生变化，从数据库中获取最新的全量角色与菜单的关联。
     * 如果未发生变化，则返回空
     *
     * @param maxUpdateTime 当前角色与菜单的关联的最大更新时间
     * @return 角色与菜单的关联列表
     */
    protected List<RoleMenuDO> loadRoleMenuIfUpdate(ZonedDateTime maxUpdateTime) {
        // 第一步，判断是否要更新。
        if (maxUpdateTime == null) { // 如果更新时间为空，说明 DB 一定有新数据
            log.info("[loadRoleMenuIfUpdate][首次加载全量角色与菜单的关联]");
        } else { // 判断数据库中是否有更新的角色与菜单的关联
            if (roleMenuRepository.countByUpdateTimeGreaterThan(maxUpdateTime) == 0) {
                return null;
            }
            log.info("[loadRoleMenuIfUpdate][增量加载全量角色与菜单的关联]");
        }
        // 第二步，如果有更新，则从数据库加载所有角色与菜单的关联
        return roleMenuRepository.findAll();
    }


    @Override
    public List<MenuDO> getRoleMenuListFromCache(Collection<String> roleIds, Collection<Integer> menuTypes,
                                                 Collection<Integer> menusStatuses) {
        // 任一一个参数为空时，不返回任何菜单
        if (CollUtils.isAnyEmpty(roleIds, menuTypes, menusStatuses)) {
        // if (CollUtils.isAnyEmpty(menuTypes, menusStatuses)) {
            return Collections.emptyList();
        }

        // 判断角色是否包含超级管理员。如果是超级管理员，获取到全部
        List<CreedAuthorities> roleList = roleService.getRolesFromCache(roleIds);
        if (roleService.hasAnySuperAdmin(roleList)) {
            return menuService.getMenuListFromCache(menuTypes, menusStatuses);
        }

        // 获得角色拥有的菜单关联
        List<Long> menuIds = MapUtils.getList(roleMenuCache.get(), roleIds);
        return menuService.getMenuListFromCache(menuIds, menuTypes, menusStatuses);
    }

    @Override
    public Set<String> getUserRoleIdsFromCache(String userId, Collection<Integer> roleStatuses) {
        Set<String> cacheRoleIds = userRoleCache.get().get(userId);
        // 创建用户的时候没有分配角色，会存在空指针异常
        if (CollectionUtils.isEmpty(cacheRoleIds)) {
            return Collections.emptySet();
        }
        Set<String> roleIds = new HashSet<>(cacheRoleIds);
        // 过滤角色状态
        if (!CollectionUtils.isEmpty(roleStatuses)) {
            roleIds.removeIf(roleId -> {
                CreedAuthorities role = roleService.getRoleFromCache(roleId);
                return role == null || !roleStatuses.contains(role.getEnabled().getStatus());
            });
        }
        return roleIds;
    }

    @Override
    public Set<Long> getRoleMenuIds(String roleId) {
        // 如果是管理员的情况下，获取全部菜单编号
        if (roleService.hasAnySuperAdmin(Collections.singleton(roleId))) {
            return convertSet(menuService.getMenus(), MenuDO::getId);
        }
        // 如果是非管理员的情况下，获得拥有的菜单编号
        return convertSet(roleMenuRepository.findByRoleId(roleId), RoleMenuDO::getMenuId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignRoleMenu(String roleId, Set<Long> menuIds) {
        // 获得角色拥有菜单编号
        Set<Long> dbMenuIds = convertSet(roleMenuRepository.findByRoleId(roleId),
                RoleMenuDO::getMenuId);
        // 计算新增和删除的菜单编号
        Collection<Long> createMenuIds = CollUtils.subtract(menuIds, dbMenuIds);
        Collection<Long> deleteMenuIds = CollUtils.subtract(dbMenuIds, menuIds);
        // 执行新增和删除。对于已经授权的菜单，不用做任何处理
        if (!CollectionUtils.isEmpty(createMenuIds)) {
            roleMenuRepository.saveAll(CollUtils.convertList(createMenuIds, menuId -> {
                RoleMenuDO entity = new RoleMenuDO();
                entity.setRoleId(roleId);
                entity.setMenuId(menuId);
                return entity;
            }));
        }
        if (!CollectionUtils.isEmpty(deleteMenuIds)) {
            roleMenuRepository.deleteByRoleIdAndMenuIdIn(roleId, deleteMenuIds);
        }
        // 发送刷新消息. 注意，需要事务提交后，在进行发送刷新消息。不然 db 还未提交，结果缓存先刷新了
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {

            @Override
            public void afterCommit() {
                permissionProducer.sendRoleMenuRefreshMessage();
            }

        });
    }

    @Override
    public Set<String> getUserRoleIdListByUserId(String userId) {
        return creedUserService.getUserRoleIdListByUserId(userId);
    }

    @Override
    public Set<String> getUserRoleIdListByRoleIds(Collection<String> roleIds) {
        return creedUserService.getUserRoleIdListByRoleIds(roleIds);
    }

    @Override
    public void assignUserRole(String userId, Set<String> roleIds) {
        creedUserService.assignUserRole(userId, roleIds);
    }

    @Override
    public void assignRoleDataScope(String roleId, Integer dataScope, Set<Long> dataScopeDeptIds) {
        roleService.updateRoleDataScope(roleId, dataScope, dataScopeDeptIds);
    }

    @Override
    public void processRoleDeleted(String roleId) {
        creedUserService.processRoleDeleted(roleId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void processMenuDeleted(Long menuId) {
        roleMenuRepository.deleteByMenuId(menuId);
        // 发送刷新消息. 注意，需要事务提交后，在进行发送刷新消息。不然 db 还未提交，结果缓存先刷新了
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {

            @Override
            public void afterCommit() {
                permissionProducer.sendRoleMenuRefreshMessage();
            }

        });
    }

    @Override
    public void processUserDeleted(String userId) {
        creedUserService.processUserDeleted(userId);
    }

    @Override
    public boolean hasAnyPermissions(String userId, String... permissions) {
        // 如果为空，说明已经有权限
        if (ArrayUtils.isEmpty(permissions)) {
            return true;
        }

        // 获得当前登录的角色。如果为空，说明没有权限
        Set<String> roleIds = getUserRoleIdsFromCache(userId, singleton(CommonStatusEnum.ENABLE.getStatus()));
        if (CollectionUtils.isEmpty(roleIds)) {
            return false;
        }
        // 判断是否是超管。如果是，当然符合条件
        if (roleService.hasAnySuperAdmin(roleIds)) {
            return true;
        }

        // 遍历权限，判断是否有一个满足
        return Arrays.stream(permissions).anyMatch(permission -> {
            List<MenuDO> menuList = menuService.getMenuListByPermissionFromCache(permission);
            // 采用严格模式，如果权限找不到对应的 Menu 的话，认为
            if (CollectionUtils.isEmpty(menuList)) {
                return false;
            }
            // 获得是否拥有该权限，任一一个
            return menuList.stream().anyMatch(menu -> CollectionUtils.containsAny(roleIds,
                    menuRoleCache.get().get(menu.getId())));
        });
    }

    @Override
    public boolean hasAnyRoles(String userId, String... roles) {
        // 如果为空，说明已经有权限
        if (ArrayUtils.isEmpty(roles)) {
            return true;
        }

        // 获得当前登录的角色。如果为空，说明没有权限
        Set<String> roleIds = getUserRoleIdsFromCache(userId, singleton(CommonStatusEnum.ENABLE.getStatus()));
        if (CollectionUtils.isEmpty(roleIds)) {
            return false;
        }
        // 判断是否是超管。如果是，当然符合条件
        if (roleService.hasAnySuperAdmin(roleIds)) {
            return true;
        }
        Set<String> userRoles = convertSet(roleService.getRolesFromCache(roleIds),
                CreedAuthorities::getAuthority);
        return CollUtils.containsAny(userRoles, Sets.newHashSet(roles));
    }

    @Override
    // @DataPermission(enable = false) // 关闭数据权限，不然就会出现递归获取数据权限的问题
    // @TenantIgnore // 忽略多租户的自动过滤。如果不忽略，会导致添加租户时，因为切换租户，导致获取不到 User。即使忽略，本身该方法不存在跨租户的操作，不会存在问题。
    public DeptDataPermissionRespDTO getDeptDataPermission(String userId) {
        // 获得用户的角色
        Set<String> roleIds = getUserRoleIdsFromCache(userId, singleton(CommonStatusEnum.ENABLE.getStatus()));
        // 如果角色为空，则只能查看自己
        DeptDataPermissionRespDTO result = new DeptDataPermissionRespDTO();
        if (CollectionUtils.isEmpty(roleIds)) {
            result.setSelf(true);
            return result;
        }
        List<CreedAuthorities> authorities = roleService.getRolesFromCache(roleIds);

        // 获得用户的部门编号的缓存，通过 Guava 的 Suppliers 惰性求值，即有且仅有第一次发起 DB 的查询
        Supplier<Long> userDeptIdCache = Suppliers.memoize(() -> Long.parseLong(adminUserService.getUser(userId + "").getId()));
        // 遍历每个角色，计算
        for (CreedAuthorities role : authorities) {
            // 为空时，跳过
            if (role.getDataScope() == null) {
                continue;
            }
            // 情况一，ALL
            if (Objects.equals(role.getDataScope(), DataScopeEnum.ALL.getScope())) {
                result.setAll(true);
                continue;
            }
            // 情况二，DEPT_CUSTOM
            if (Objects.equals(role.getDataScope(), DataScopeEnum.DEPT_CUSTOM.getScope())) {
                CollUtils.addAll(result.getDeptIds(), role.getDataScopeDeptIds());
                // 自定义可见部门时，保证可以看到自己所在的部门。否则，一些场景下可能会有问题。
                // 例如说，登录时，基于 t_user 的 username 查询会可能被 dept_id 过滤掉
                CollUtils.addAll(result.getDeptIds(), userDeptIdCache.get());
                continue;
            }
            // 情况三，DEPT_ONLY
            if (Objects.equals(role.getDataScope(), DataScopeEnum.DEPT_ONLY.getScope())) {
                CollUtils.addIfNotNull(result.getDeptIds(), userDeptIdCache.get());
                continue;
            }
            // 情况四，DEPT_DEPT_AND_CHILD
            if (Objects.equals(role.getDataScope(), DataScopeEnum.DEPT_AND_CHILD.getScope())) {
                List<DeptDO> depts = deptService.getDeptsByParentIdFromCache(userDeptIdCache.get(), true);
                CollUtils.addAll(result.getDeptIds(), CollUtils.convertList(depts, DeptDO::getId));
                // 添加本身部门编号
                CollUtils.addAll(result.getDeptIds(), userDeptIdCache.get());
                continue;
            }
            // 情况五，SELF
            if (Objects.equals(role.getDataScope(), DataScopeEnum.SELF.getScope())) {
                result.setSelf(true);
                continue;
            }
            // 未知情况，error log 即可
            log.error("[getDeptDataPermission][LoginUser({}) role({}) 无法处理]", userId, JacksonUtils.toJsonString(result));
        }
        return result;
    }

}
