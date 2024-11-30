package com.ethan.system.service.permission;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.ethan.common.constant.CommonStatusEnum;
import com.ethan.common.utils.collection.CollUtils;
import com.ethan.system.constant.permission.MenuTypeEnum;
import com.ethan.system.controller.admin.permission.vo.menu.MenuListReqVO;
import com.ethan.system.controller.admin.permission.vo.menu.MenuSaveVO;
import com.ethan.system.convert.permission.MenuConvert;
import com.ethan.system.dal.entity.permission.SystemAuthorities;
import com.ethan.system.dal.entity.permission.SystemMenuRoles;
import com.ethan.system.dal.entity.permission.SystemMenus;
import com.ethan.system.dal.entity.permission.SystemRoleAuthorities;
import com.ethan.system.dal.entity.permission.SystemRoles;
import com.ethan.system.dal.redis.RedisKeyConstants;
import com.ethan.system.dal.repository.permission.SystemAuthoritiesRepository;
import com.ethan.system.dal.repository.permission.SystemMenusRepository;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.mzt.logapi.context.LogRecordContext;
import com.mzt.logapi.starter.annotation.LogRecord;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static com.ethan.common.exception.util.ServiceExceptionUtil.exception;
import static com.ethan.common.utils.collection.CollUtils.convertList;
import static com.ethan.system.api.constant.LogRecordConstants.SYSTEM_MENU_CREATE_SUB_TYPE;
import static com.ethan.system.api.constant.LogRecordConstants.SYSTEM_MENU_CREATE_SUCCESS;
import static com.ethan.system.api.constant.LogRecordConstants.SYSTEM_MENU_TYPE;
import static com.ethan.system.api.constant.LogRecordConstants.SYSTEM_ROLE_TYPE;
import static com.ethan.system.api.constant.LogRecordConstants.SYSTEM_ROLE_UPDATE_SUB_TYPE;
import static com.ethan.system.api.constant.LogRecordConstants.SYSTEM_ROLE_UPDATE_SUCCESS;
import static com.ethan.system.constant.ErrorCodeConstants.MENU_EXISTS_CHILDREN;
import static com.ethan.system.constant.ErrorCodeConstants.MENU_NAME_DUPLICATE;
import static com.ethan.system.constant.ErrorCodeConstants.MENU_NOT_EXISTS;
import static com.ethan.system.constant.ErrorCodeConstants.MENU_PARENT_ERROR;
import static com.ethan.system.constant.ErrorCodeConstants.MENU_PARENT_NOT_DIR_OR_MENU;
import static com.ethan.system.constant.ErrorCodeConstants.MENU_PARENT_NOT_EXISTS;

/**
 * 菜单 Service 实现
 *
 * 
 */
@Service
@Slf4j
public class MenuServiceImpl implements MenuService {
    @Resource
    private SystemMenusRepository menusRepository;
    @Resource
    private SystemAuthoritiesRepository authoritiesRepository;
    @Resource
    private PermissionService permissionService;

    @Override
    @CacheEvict(value = RedisKeyConstants.PERMISSION_MENU_ID_LIST, key = "#result",
            condition = "#result != null")
    @LogRecord(type = SYSTEM_MENU_TYPE, subType = SYSTEM_MENU_CREATE_SUB_TYPE, bizNo = "{{#menu.id}}",
            success = SYSTEM_MENU_CREATE_SUCCESS)
    public Long createMenu(MenuSaveVO createReqVO) {
        // 校验父菜单存在
        validateParentMenu(createReqVO.getParentId(), null);
        // 校验菜单（自己）
        validateMenu(createReqVO.getParentId(), createReqVO.getName(), null);

        // 插入数据库
        SystemMenus menu = MenuConvert.INSTANCE.convert(createReqVO);
        initMenuProperty(menu);
        menusRepository.save(menu);
        // 3. 记录操作日志上下文
        LogRecordContext.putVariable("menu", menu);
        // 返回
        return menu.getId();
    }

    @Override
    @CacheEvict(value = RedisKeyConstants.PERMISSION_MENU_ID_LIST, key = "#updateReqVO.id",
            condition = "#updateReqVO.id != null")
    @LogRecord(type = SYSTEM_ROLE_TYPE, subType = SYSTEM_ROLE_UPDATE_SUB_TYPE, bizNo = "{{#updateReqVO.id}}",
            success = SYSTEM_ROLE_UPDATE_SUCCESS)
    public void updateMenu(MenuSaveVO updateReqVO) {
        // 校验更新的菜单是否存在
        SystemMenus systemMenus = menusRepository.findById(updateReqVO.getId()).orElseThrow(() -> exception(MENU_NOT_EXISTS));
        // 校验父菜单存在
        validateParentMenu(updateReqVO.getParentId(), updateReqVO.getId());
        // 校验菜单（自己）
        validateMenu(updateReqVO.getParentId(), updateReqVO.getName(), updateReqVO.getId());

        // 更新到数据库
        MenuConvert.INSTANCE.update(updateReqVO, systemMenus);
        initMenuProperty(systemMenus);
        menusRepository.save(systemMenus);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = RedisKeyConstants.PERMISSION_MENU_ID_LIST, key = "#id",
            condition = "#id != null")
    public void deleteMenu(Long id) {
        // 校验是否还有子菜单
        if (menusRepository.countByParentId(id) > 0) {
            throw exception(MENU_EXISTS_CHILDREN);
        }
        // 校验删除的菜单是否存在
        SystemMenus systemMenus = menusRepository.findById(id).orElseThrow(() -> exception(MENU_NOT_EXISTS));
        // 标记删除
        menusRepository.delete(systemMenus);
        // 删除授予给角色的权限
        permissionService.processMenuDeleted(id);
    }

    @Override
    public List<SystemMenus> getMenuList() {
        return menusRepository.findAll();
    }

    @Override
    public List<SystemMenus> getMenuListByTenant(MenuListReqVO reqVO) {
        // 查询所有菜单，并过滤掉关闭的节点
        List<SystemMenus> menus = getMenuList(reqVO);
        // 开启多租户的情况下，需要过滤掉未开通的菜单 TODO
        // tenantService.handleTenantMenu(menuIds -> menus.removeIf(menu -> !CollUtil.contains(menuIds, menu.getId())));
        return menus;
    }

    @Override
    public List<SystemMenus> filterDisableMenus(List<SystemMenus> list) {
        if (CollUtil.isEmpty(list)){
            return Collections.emptyList();
        }
        Map<Long, SystemMenus> menuMap = CollUtils.convertMap(list, SystemMenus::getId);

        // 遍历 menu 菜单，查找不是禁用的菜单，添加到 enabledMenus 结果
        List<SystemMenus> enabledMenus = new ArrayList<>();
        Set<Long> disabledMenuCache = new HashSet<>(); // 存下递归搜索过被禁用的菜单，防止重复的搜索
        for (SystemMenus menu : list) {
            if (isMenuDisabled(menu, menuMap, disabledMenuCache)) {
                continue;
            }
            enabledMenus.add(menu);
        }
        return enabledMenus;
    }

    private boolean isMenuDisabled(SystemMenus node, Map<Long, SystemMenus> menuMap, Set<Long> disabledMenuCache) {
        // 如果已经判定是禁用的节点，直接结束
        if (disabledMenuCache.contains(node.getId())) {
            return true;
        }

        // 1. 遍历到 parentId 为根节点，则无需判断
        Long parentId = node.getParentId();
        if (ObjectUtil.equal(parentId, SystemMenus.ID_ROOT)) {
            if (CommonStatusEnum.DISABLE.equals(node.getEnabled())) {
                disabledMenuCache.add(node.getId());
                return true;
            }
            return false;
        }

        // 2. 继续遍历 parent 节点
        SystemMenus parent = menuMap.get(parentId);
        if (parent == null || isMenuDisabled(parent, menuMap, disabledMenuCache)) {
            disabledMenuCache.add(node.getId());
            return true;
        }
        return false;
    }

    @Override
    public List<SystemMenus> getMenuList(MenuListReqVO reqVO) {
        return menusRepository.findByCondition(reqVO);
    }

    @Override
    @Cacheable(value = RedisKeyConstants.PERMISSION_MENU_ID_LIST, key = "#permission")
    public List<Long> getMenuIdListByPermissionFromCache(String permission) {
        Optional<SystemAuthorities> authoritiesOptional = authoritiesRepository.findByAuthority(permission);
        List<SystemMenus> menus = authoritiesOptional.map(SystemAuthorities::getRoleAuthorities)
                .orElse(Collections.emptyList())
                .stream().map(SystemRoleAuthorities::getRoles)
                .map(SystemRoles::getMenuRoles)
                .flatMap(Collection::stream)
                .map(SystemMenuRoles::getMenus).toList();
        return convertList(menus, SystemMenus::getId);
    }

    @Override
    public SystemMenus getMenu(Long id) {
        return menusRepository.findById(id).orElse(null);
    }

    @Override
    @Cacheable(value = RedisKeyConstants.ROLE, key = "#id",
            unless = "#result == null")
    public SystemMenus getMenuFromCache(Long id) {
        return getMenu(id);
    }

    @Override
    public List<SystemMenus> getMenuList(Collection<Long> ids) {
        // 当 ids 为空时，返回一个空的实例对象
        if (CollUtil.isEmpty(ids)) {
            return Lists.newArrayList();
        }
        return menusRepository.findAllById(ids);
    }


    /**
     * 校验父菜单是否合法
     * <p>
     * 1. 不能设置自己为父菜单
     * 2. 父菜单不存在
     * 3. 父菜单必须是 {@link MenuTypeEnum#MENU} 菜单类型
     *
     * @param parentId 父菜单编号
     * @param childId  当前菜单编号
     */
    @VisibleForTesting
    void validateParentMenu(Long parentId, Long childId) {
        if (parentId == null || SystemMenus.ID_ROOT.equals(parentId)) {
            return;
        }
        // 不能设置自己为父菜单
        if (parentId.equals(childId)) {
            throw exception(MENU_PARENT_ERROR);
        }
        Optional<SystemMenus> menuOptional = menusRepository.findById(parentId);
        // 父菜单不存在
        if (menuOptional.isEmpty()) {
            throw exception(MENU_PARENT_NOT_EXISTS);
        }
        // 父菜单必须是目录或者菜单类型
        if (menuOptional.map(SystemMenus::getType)
                .filter(Arrays.asList(MenuTypeEnum.DIR, MenuTypeEnum.MENU)::contains)
                .isEmpty()) {
            throw exception(MENU_PARENT_NOT_DIR_OR_MENU);
        }

    }

    /**
     * 校验菜单是否合法
     * <p>
     * 1. 校验相同父菜单编号下，是否存在相同的菜单名
     *
     * @param name     菜单名字
     * @param parentId 父菜单编号
     * @param id       菜单编号
     */
    @VisibleForTesting
    void validateMenu(Long parentId, String name, Long id) {
        Optional<SystemMenus> menuOptional = menusRepository.findByParentIdAndName(parentId, name);
        if (menuOptional.isEmpty()) {
            return;
        }
        // 如果 id 为空，说明不用比较是否为相同 id 的菜单
        if (id == null) {
            throw exception(MENU_NAME_DUPLICATE);
        }
        if (menuOptional.map(SystemMenus::getId)
                .filter(id::equals)
                .isEmpty()) {
            throw exception(MENU_NAME_DUPLICATE);
        }
    }

    /**
     * 初始化菜单的通用属性。
     * <p>
     * 例如说，只有目录或者菜单类型的菜单，才设置 icon
     *
     * @param menu 菜单
     */
    private void initMenuProperty(SystemMenus menu) {
        // 菜单为按钮类型时，无需 component、icon、path 属性，进行置空
        if (MenuTypeEnum.BUTTON.equals(menu.getType())) {
            menu.setComponent("");
            menu.setComponentName("");
            menu.setIcon("");
            menu.setPath("");
        }
    }
}
