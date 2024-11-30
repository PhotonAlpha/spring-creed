package com.ethan.system.service.permission;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;
import com.ethan.common.constant.CommonStatusEnum;
import com.ethan.common.pojo.BaseVersioningXDO;
import com.ethan.common.utils.collection.CollUtils;
import com.ethan.system.api.permission.dto.DeptDataPermissionRespDTO;
import com.ethan.system.dal.entity.permission.SystemAuthorities;
import com.ethan.system.dal.entity.permission.SystemGroupRoles;
import com.ethan.system.dal.entity.permission.SystemGroupUsers;
import com.ethan.system.dal.entity.permission.SystemGroups;
import com.ethan.system.dal.entity.permission.SystemMenuRoles;
import com.ethan.system.dal.entity.permission.SystemMenus;
import com.ethan.system.dal.entity.permission.SystemRoleAuthorities;
import com.ethan.system.dal.entity.permission.SystemRoles;
import com.ethan.system.dal.entity.permission.SystemUserAuthorities;
import com.ethan.system.dal.entity.permission.SystemUserRoles;
import com.ethan.system.dal.entity.permission.SystemUsers;
import com.ethan.system.dal.redis.RedisKeyConstants;
import com.ethan.system.dal.repository.permission.SystemGroupRolesRepository;
import com.ethan.system.dal.repository.permission.SystemMenuRolesRepository;
import com.ethan.system.dal.repository.permission.SystemRoleAuthoritiesRepository;
import com.ethan.system.dal.repository.permission.SystemUserRolesRepository;
import com.ethan.system.dal.repository.permission.SystemUsersRepository;
import com.ethan.system.mq.producer.permission.PermissionProducer;
import com.ethan.system.service.dept.DeptService;
import com.ethan.system.service.user.AdminUserService;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Sets;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 权限 Service 实现类
 *
 * 
 */
@Service
@Slf4j
public class PermissionServiceImpl implements PermissionService {

    @Resource
    private SystemMenuRolesRepository menuRolesRepository;
    @Resource
    private SystemGroupRolesRepository groupRolesRepository;
    @Resource
    private SystemRoleAuthoritiesRepository roleAuthoritiesRepository;
    @Resource
    private SystemUserRolesRepository userRolesRepository;
    @Resource
    private SystemUsersRepository usersRepository;

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

    /**
     * 判断指定角色，是否拥有该 permission 权限
     *
     * @param userId 指定角色数组
     * @param permissions 权限标识
     * @return 是否拥有
     */
    @Override
    public boolean hasAnyPermissions(Long userId, String... permissions) {
        // 如果为空，说明已经有权限
        if (ArrayUtils.isEmpty(permissions)) {
            return true;
        }

        // 获得当前登录的角色。如果为空，说明没有权限
        List<SystemAuthorities> auths = getEnableUserAuthoritiesByUserIdFromCache(userId);
        if (CollUtil.isEmpty(auths)) {
            return false;
        }

        // 情况一：遍历判断每个权限，如果有一满足，说明有权限
        Set<String> authoritiesSet = auths.stream().map(SystemAuthorities::getAuthority).collect(Collectors.toSet());
        for (String permission : permissions) {
            if (authoritiesSet.contains(permission)) {
                return true;
            }
        }
        List<SystemRoles> systemRoles = getEnableUserRolesByUserIdFromCache(userId);
        // 情况二：如果是超管，也说明有权限
        return roleService.hasAnySuperAdmin(CollUtils.convertSet(systemRoles, SystemRoles::getId));
    }

    /**
     * 获得用户拥有的开启状态的权限
     *
     * @param userId 用户编号
     * @return 获得用户拥有的权限集合
     */
    @VisibleForTesting
    private List<SystemAuthorities> getEnableUserAuthoritiesByUserIdFromCache(Long userId) {
        // 获得用户拥有的权限集合
        Set<SystemAuthorities> auths = self.getUserAuthoritiesByUserIdFromCache(userId);
        return auths.stream().filter(getEnabledPredicate()).toList();
    }
    /**
     * 获得用户拥有的开启状态的权限
     *
     * @param userId 用户编号
     * @return 获得用户拥有的角色集合
     */
    @VisibleForTesting
    private List<SystemRoles> getEnableUserRolesByUserIdFromCache(Long userId) {
        // 获得用户拥有的角色集合
        Set<SystemRoles> roles = self.getUserRolesByUserIdFromCache(userId);
        return roles.stream().filter(getEnabledPredicate()).toList();
    }

    @Override
    public boolean hasAnyRoles(Long userId, String... roles) {
        // 如果为空，说明已经有权限
        if (ArrayUtil.isEmpty(roles)) {
            return true;
        }

        // 获得当前登录的角色。如果为空，说明没有权限
        List<SystemRoles> roleList = getEnableUserRolesByUserIdFromCache(userId);
        if (CollUtil.isEmpty(roleList)) {
            return false;
        }

        // 判断是否有角色
        Set<String> userRoles = CollUtils.convertSet(roleList, SystemRoles::getCode);
        return CollUtil.containsAny(userRoles, Sets.newHashSet(roles));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = RedisKeyConstants.MENU_ROLE_ID_LIST,
            allEntries = true) // allEntries 清空所有缓存，主要一次更新涉及到的 menuIds 较多，反倒批量会更快
    public void assignRoleMenu(Long roleId, Set<Long> menuIds) {
        SystemRoles roleFromCache = roleService.getRoleFromCache(roleId);

        Map<Long, SystemMenuRoles> menuIdMapping = CollUtils.convertMap(roleFromCache.getMenuRoles(), CollUtils.combine(SystemMenuRoles::getMenus, SystemMenus::getId), Function.identity());
        // 获得角色拥有菜单编号
        // 计算新增和删除的菜单编号
        Set<Long> menuIdList = CollUtil.emptyIfNull(menuIds);
        Collection<Long> createMenuIds = CollUtil.subtract(menuIdList, menuIdMapping.keySet());
        Collection<Long> deleteMenuIds = CollUtil.subtract(menuIdMapping.keySet(), menuIdList);
        // 执行新增和删除。对于已经授权的菜单，不用做任何处理
        log.info("creating role menu:{}", createMenuIds);
        if (CollUtil.isNotEmpty(createMenuIds)) {
            menuRolesRepository.saveAll(CollUtils.convertList(createMenuIds,
                    menuId -> new SystemMenuRoles(menuService.getMenuFromCache(menuId), roleFromCache),
                    menuId -> Objects.nonNull(menuService.getMenuFromCache(menuId))));
        }
        log.info("deleting role menu:{}", createMenuIds);
        if (CollUtil.isNotEmpty(deleteMenuIds)) {
            menuRolesRepository.deleteAll(
                CollUtils.convertList(deleteMenuIds, menuIdMapping::get,
                        menuId -> Objects.nonNull(menuService.getMenuFromCache(menuId)))
            );
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Caching(evict = {
            @CacheEvict(value = RedisKeyConstants.MENU_ROLE_ID_LIST,
                    allEntries = true), // allEntries 清空所有缓存，此处无法方便获得 roleId 对应的 menu 缓存们
            @CacheEvict(value = RedisKeyConstants.USER_ROLE_ID_LIST,
                    allEntries = true) // allEntries 清空所有缓存，此处无法方便获得 roleId 对应的 user 缓存们
    })
    public void processRoleDeleted(Long roleId) {
        SystemRoles roleFromCache = roleService.getRoleFromCache(roleId);
        // 标记删除 RoleAuthorities
        List<SystemRoleAuthorities> roleAuthorities = roleFromCache.getRoleAuthorities();
        roleAuthoritiesRepository.deleteAll(roleAuthorities);
        // 标记删除 GroupRole
        List<SystemGroupRoles> groupRoles = roleFromCache.getGroupRoles();
        groupRolesRepository.deleteAll(groupRoles);
        // 标记删除 UserRole
        List<SystemUserRoles> userRoles = roleFromCache.getUserRoles();
        userRolesRepository.deleteAll(userRoles);
        // 标记删除 RoleMenu
        List<SystemMenuRoles> menuRoles = roleFromCache.getMenuRoles();
        menuRolesRepository.deleteAll(menuRoles);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = RedisKeyConstants.MENU_ROLE_ID_LIST, key = "#menuId")
    public void processMenuDeleted(Long menuId) {
        List<SystemMenuRoles> menuRoles = Optional.ofNullable(menuService.getMenuFromCache(menuId))
                .map(SystemMenus::getMenuRoles).orElse(Collections.emptyList());
        menuRolesRepository.deleteAll(menuRoles);
    }

    @Override
    public Set<Long> getRoleMenuListByRoleId(Collection<Long> roleIds) {
        if (CollUtil.isEmpty(roleIds)) {
            return Collections.emptySet();
        }

        // 如果是管理员的情况下，获取全部菜单编号
        if (roleService.hasAnySuperAdmin(roleIds)) {
            return CollUtils.convertSet(menuService.getMenuList(), SystemMenus::getId);
        }
        // 如果是非管理员的情况下，获得拥有的菜单编号
        List<SystemMenus> systemMenus = roleService.getRoleListFromCache(roleIds).stream().map(SystemRoles::getMenuRoles)
                .flatMap(Collection::stream)
                .map(SystemMenuRoles::getMenus).toList();
        return CollUtils.convertSet(systemMenus, SystemMenus::getId);
    }

    @Override
    @Cacheable(value = RedisKeyConstants.MENU_ROLE_ID_LIST, key = "#menuId")
    public Set<Long> getMenuRoleIdListByMenuIdFromCache(Long menuId) {
        return Optional.ofNullable(menuService.getMenuFromCache(menuId))
                .map(SystemMenus::getMenuRoles).orElse(Collections.emptyList())
                .stream().map(SystemMenuRoles::getRoles)
                .map(SystemRoles::getId).collect(Collectors.toSet());
    }

    @Override
    @CacheEvict(value = RedisKeyConstants.USER_ROLE_ID_LIST, key = "#userId")
    public void assignUserRole(Long userId, Set<Long> roleIds) {
        // 获得角色拥有角色编号 TODO
        // adminUserService.getUser()
        // Set<Long> dbRoleIds = CollUtils.convertSet(userRoleMapper.selectListByUserId(userId),
        //         UserRoleDO::getRoleId);
        // // 计算新增和删除的角色编号
        // Set<Long> roleIdList = CollUtil.emptyIfNull(roleIds);
        // Collection<Long> createRoleIds = CollUtil.subtract(roleIdList, dbRoleIds);
        // Collection<Long> deleteMenuIds = CollUtil.subtract(dbRoleIds, roleIdList);
        // // 执行新增和删除。对于已经授权的角色，不用做任何处理
        // if (!CollectionUtil.isEmpty(createRoleIds)) {
        //     userRoleMapper.insertBatch(CollectionUtils.convertList(createRoleIds, roleId -> {
        //         UserRoleDO entity = new UserRoleDO();
        //         entity.setUserId(userId);
        //         entity.setRoleId(roleId);
        //         return entity;
        //     }));
        // }
        // if (!CollectionUtil.isEmpty(deleteMenuIds)) {
        //     userRoleMapper.deleteListByUserIdAndRoleIdIds(userId, deleteMenuIds);
        // }
    }

    @Override
    @CacheEvict(value = RedisKeyConstants.USER_ROLE_ID_LIST, key = "#userId")
    public void processUserDeleted(Long userId) {
        //TODO
    }

    @Override
    public Set<Long> getUserRoleIdListByRoleId(Collection<Long> roleIds) {
        // return convertSet(userRoleMapper.selectListByUserId(userId), UserRoleDO::getRoleId); TODO
        return null;
    }

    /**
     *
     * @param userId 用户编号
     * @return 获取该用户的所有 permissions
     */
    @Override
    public Set<SystemAuthorities> getUserAuthoritiesByUserId(Long userId) {
        Optional<SystemUsers> systemUsersOptional = usersRepository.findById(userId);
        List<SystemAuthorities> groupRoleAuths = systemUsersOptional.map(SystemUsers::getGroupUsers)
                .orElse(Collections.emptyList())
                .stream().map(SystemGroupUsers::getGroups)
                .filter(getEnabledPredicate())
                .map(SystemGroups::getGroupRoles)
                .flatMap(Collection::stream)
                .map(SystemGroupRoles::getRoles)
                .filter(getEnabledPredicate())
                .map(SystemRoles::getRoleAuthorities)
                .flatMap(Collection::stream)
                .map(SystemRoleAuthorities::getAuthorities).toList();
        log.info("::::::groupRoleAuths:{}", groupRoleAuths);

        List<SystemAuthorities> roleAuths = systemUsersOptional.map(SystemUsers::getUserRoles)
                .orElse(Collections.emptyList())
                .stream().map(SystemUserRoles::getRoles)
                .filter(getEnabledPredicate())
                .map(SystemRoles::getRoleAuthorities)
                .flatMap(Collection::stream)
                .map(SystemRoleAuthorities::getAuthorities).toList();
        log.info("::::::roleAuths:{}", roleAuths);
        List<SystemAuthorities> userAuths = systemUsersOptional.map(SystemUsers::getUserAuthorities)
                .orElse(Collections.emptyList())
                .stream().map(SystemUserAuthorities::getAuthorities).toList();
        log.info("::::::userAuths:{}", userAuths);
        return Stream.of(groupRoleAuths, roleAuths, userAuths)
                .flatMap(Collection::stream).collect(Collectors.toSet());
    }

    @Override
    public Set<SystemRoles> getUserRolesByUserId(Long userId) {
        Optional<SystemUsers> systemUsersOptional = usersRepository.findById(userId);
        List<SystemRoles> groupRoles = systemUsersOptional.map(SystemUsers::getGroupUsers)
                .orElse(Collections.emptyList())
                .stream().map(SystemGroupUsers::getGroups)
                .filter(getEnabledPredicate())
                .map(SystemGroups::getGroupRoles)
                .flatMap(Collection::stream)
                .map(SystemGroupRoles::getRoles).toList();
        log.info("::::::groupRoles:{}", groupRoles.size());

        List<SystemRoles> roleRoles = systemUsersOptional.map(SystemUsers::getUserRoles)
                .orElse(Collections.emptyList())
                .stream().map(SystemUserRoles::getRoles).toList();
        log.info("::::::roles:{}", roleRoles.size());
        return Stream.concat(groupRoles.stream(), roleRoles.stream())
                .collect(Collectors.toSet());
    }

    @NotNull
    private static Predicate<? super BaseVersioningXDO> getEnabledPredicate() {
        return b -> CommonStatusEnum.ENABLE.equals(b.getEnabled());
    }

    @Override
    @Cacheable(value = RedisKeyConstants.USER_AUTHORITIES, key = "#userId")
    public Set<SystemAuthorities> getUserAuthoritiesByUserIdFromCache(Long userId) {
        log.info("hit getUserAuthoritiesByUserIdFromCache:{}", userId);
        return getUserAuthoritiesByUserId(userId);
    }

    @Override
    @Cacheable(value = RedisKeyConstants.USER_ROLE_ID_LIST, key = "#userId")
    public Set<SystemRoles> getUserRolesByUserIdFromCache(Long userId) {
        log.info("hit getUserRolesByUserIdFromCache:{}", userId);
        return getUserRolesByUserId(userId);
    }

    @Override
    public Set<Long> getUserRoleIdListByUserId(Long userId) {
        // return convertSet(userRoleMapper.selectListByUserId(userId), UserRoleDO::getRoleId); TODO
        return null;
    }

    @Override
    @Cacheable(value = RedisKeyConstants.USER_ROLE_ID_LIST, key = "#userId")
    public Set<Long> getUserRoleIdListByUserIdFromCache(Long userId) {
        return null; //TODO
    }

    @Override
    public void assignRoleDataScope(Long roleId, Integer dataScope, Set<Long> dataScopeDeptIds) {
        roleService.updateRoleDataScope(roleId, dataScope, dataScopeDeptIds);
    }

    @Override
    // @DataPermission(enable = false) // 关闭数据权限，不然就会出现递归获取数据权限的问题
    public DeptDataPermissionRespDTO getDeptDataPermission(Long userId) {
        // // 获得用户的角色 TODO
        // List<RoleDO> roles = getEnableUserRoleListByUserIdFromCache(userId);
        //
        // // 如果角色为空，则只能查看自己
        // DeptDataPermissionRespDTO result = new DeptDataPermissionRespDTO();
        // if (CollUtil.isEmpty(roles)) {
        //     result.setSelf(true);
        //     return result;
        // }
        //
        // // 获得用户的部门编号的缓存，通过 Guava 的 Suppliers 惰性求值，即有且仅有第一次发起 DB 的查询
        // Supplier<Long> userDeptId = Suppliers.memoize(() -> userService.getUser(userId).getDeptId());
        // // 遍历每个角色，计算
        // for (RoleDO role : roles) {
        //     // 为空时，跳过
        //     if (role.getDataScope() == null) {
        //         continue;
        //     }
        //     // 情况一，ALL
        //     if (Objects.equals(role.getDataScope(), DataScopeEnum.ALL.getScope())) {
        //         result.setAll(true);
        //         continue;
        //     }
        //     // 情况二，DEPT_CUSTOM
        //     if (Objects.equals(role.getDataScope(), DataScopeEnum.DEPT_CUSTOM.getScope())) {
        //         CollUtil.addAll(result.getDeptIds(), role.getDataScopeDeptIds());
        //         // 自定义可见部门时，保证可以看到自己所在的部门。否则，一些场景下可能会有问题。
        //         // 例如说，登录时，基于 t_user 的 username 查询会可能被 dept_id 过滤掉
        //         CollUtil.addAll(result.getDeptIds(), userDeptId.get());
        //         continue;
        //     }
        //     // 情况三，DEPT_ONLY
        //     if (Objects.equals(role.getDataScope(), DataScopeEnum.DEPT_ONLY.getScope())) {
        //         CollectionUtils.addIfNotNull(result.getDeptIds(), userDeptId.get());
        //         continue;
        //     }
        //     // 情况四，DEPT_DEPT_AND_CHILD
        //     if (Objects.equals(role.getDataScope(), DataScopeEnum.DEPT_AND_CHILD.getScope())) {
        //         CollUtil.addAll(result.getDeptIds(), deptService.getChildDeptIdListFromCache(userDeptId.get()));
        //         // 添加本身部门编号
        //         CollUtil.addAll(result.getDeptIds(), userDeptId.get());
        //         continue;
        //     }
        //     // 情况五，SELF
        //     if (Objects.equals(role.getDataScope(), DataScopeEnum.SELF.getScope())) {
        //         result.setSelf(true);
        //         continue;
        //     }
        //     // 未知情况，error log 即可
        //     log.error("[getDeptDataPermission][LoginUser({}) role({}) 无法处理]", userId, toJsonString(result));
        // }
        // return result;
        //
        return null;
    }

}
