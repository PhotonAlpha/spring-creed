package com.ethan.system.service.permission;

import com.ethan.common.exception.util.ServiceExceptionUtil;
import com.ethan.common.utils.collection.CollUtils;
import com.ethan.system.constant.permission.MenuIdEnum;
import com.ethan.system.constant.permission.MenuTypeEnum;
import com.ethan.system.controller.admin.permission.vo.menu.MenuCreateReqVO;
import com.ethan.system.controller.admin.permission.vo.menu.MenuListReqVO;
import com.ethan.system.controller.admin.permission.vo.menu.MenuUpdateReqVO;
import com.ethan.system.convert.permission.MenuConvert;
import com.ethan.system.dal.entity.permission.MenuDO;
import com.ethan.system.dal.repository.permission.MenuRepository;
import com.ethan.system.mq.producer.permission.MenuProducer;
import com.ethan.system.service.tenant.TenantService;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.nimbusds.oauth2.sdk.util.CollectionUtils;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

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

    /**
     * 定时执行 {@link #schedulePeriodicRefresh()} 的周期
     * 因为已经通过 Redis Pub/Sub 机制，所以频率不需要高
     */
    private static final long SCHEDULER_PERIOD = 5 * 60 * 1000L;

    /**
     * 菜单缓存
     * key：菜单编号
     *
     * 这里声明 volatile 修饰的原因是，每次刷新时，直接修改指向
     */
    private volatile Map<Long, MenuDO> menuCache;
    /**
     * 权限与菜单缓存
     * key：权限 {@link MenuDO#getPermission()}
     * value：MenuDO 数组，因为一个权限可能对应多个 MenuDO 对象
     *
     * 这里声明 volatile 修饰的原因是，每次刷新时，直接修改指向
     */
    private volatile Multimap<String, MenuDO> permissionMenuCache;
    /**
     * 缓存菜单的最大更新时间，用于后续的增量轮询，判断是否有更新
     */
    private volatile ZonedDateTime maxUpdateTime;

    @Resource
    private MenuRepository menuRepository;
    @Resource
    private PermissionService permissionService;
    @Resource
    @Lazy // 延迟，避免循环依赖报错
    private TenantService tenantService;

    @Resource
    private MenuProducer menuProducer;

    /**
     * 初始化 {@link #menuCache} 和 {@link #permissionMenuCache} 缓存
     */
    @Override
    @PostConstruct
    public synchronized void initLocalCache() {
        // 获取菜单列表，如果有更新
        List<MenuDO> menuList = this.loadMenuIfUpdate(maxUpdateTime);
        if (CollectionUtils.isEmpty(menuList)) {
            return;
        }

        // 构建缓存
        ImmutableMap.Builder<Long, MenuDO> menuCacheBuilder = ImmutableMap.builder();
        ImmutableMultimap.Builder<String, MenuDO> permMenuCacheBuilder = ImmutableMultimap.builder();
        menuList.forEach(menuDO -> {
            menuCacheBuilder.put(menuDO.getId(), menuDO);
            if (StringUtils.isNotEmpty(menuDO.getPermission())) { // 会存在 permission 为 null 的情况，导致 put 报 NPE 异常
                permMenuCacheBuilder.put(menuDO.getPermission(), menuDO);
            }
        });
        menuCache = menuCacheBuilder.build();
        permissionMenuCache = permMenuCacheBuilder.build();
        maxUpdateTime = CollUtils.getMaxValue(menuList, MenuDO::getUpdateTime);
        log.info("[initLocalCache][缓存菜单，数量为:{}]", menuList.size());
    }

    @Scheduled(fixedDelay = SCHEDULER_PERIOD, initialDelay = SCHEDULER_PERIOD)
    public void schedulePeriodicRefresh() {
        initLocalCache();
    }

    /**
     * 如果菜单发生变化，从数据库中获取最新的全量菜单。
     * 如果未发生变化，则返回空
     *
     * @param maxUpdateTime 当前菜单的最大更新时间
     * @return 菜单列表
     */
    private List<MenuDO> loadMenuIfUpdate(ZonedDateTime maxUpdateTime) {
        // 第一步，判断是否要更新。
        if (maxUpdateTime == null) { // 如果更新时间为空，说明 DB 一定有新数据
            log.info("[loadMenuIfUpdate][首次加载全量菜单]");
        } else { // 判断数据库中是否有更新的菜单
            if (menuRepository.countByUpdateTimeGreaterThan(maxUpdateTime) == 0) {
                return null;
            }
            log.info("[loadMenuIfUpdate][增量加载全量菜单]");
        }
        // 第二步，如果有更新，则从数据库加载所有菜单
        return menuRepository.findAll();
    }

    @Override
    public Long createMenu(MenuCreateReqVO reqVO) {
        // 校验父菜单存在
        checkParentResource(reqVO.getParentId(), null);
        // 校验菜单（自己）
        checkResource(reqVO.getParentId(), reqVO.getName(), null);
        // 插入数据库
        MenuDO menu = MenuConvert.INSTANCE.convert(reqVO);
        initMenuProperty(menu);
        menuRepository.save(menu);
        // 发送刷新消息
        menuProducer.sendMenuRefreshMessage();
        // 返回
        return menu.getId();
    }

    @Override
    public void updateMenu(MenuUpdateReqVO reqVO) {
        // 校验更新的菜单是否存在
        if (menuRepository.findById(reqVO.getId()) == null) {
            throw ServiceExceptionUtil.exception(MENU_NOT_EXISTS);
        }
        // 校验父菜单存在
        checkParentResource(reqVO.getParentId(), reqVO.getId());
        // 校验菜单（自己）
        checkResource(reqVO.getParentId(), reqVO.getName(), reqVO.getId());
        // 更新到数据库
        MenuDO updateObject = MenuConvert.INSTANCE.convert(reqVO);
        initMenuProperty(updateObject);
        menuRepository.save(updateObject);
        // 发送刷新消息
        menuProducer.sendMenuRefreshMessage();
    }

    /**
     * 删除菜单
     *
     * @param menuId 菜单编号
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteMenu(Long menuId) {
        // 校验是否还有子菜单
        if (menuRepository.countByParentId(menuId) > 0) {
            throw ServiceExceptionUtil.exception(MENU_EXISTS_CHILDREN);
        }
        // 校验删除的菜单是否存在
        if (menuRepository.findById(menuId) == null) {
            throw ServiceExceptionUtil.exception(MENU_NOT_EXISTS);
        }
        // 标记删除
        menuRepository.deleteById(menuId);
        // 删除授予给角色的权限
        permissionService.processMenuDeleted(menuId);
        // 发送刷新消息. 注意，需要事务提交后，在进行发送刷新消息。不然 db 还未提交，结果缓存先刷新了
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {

            @Override
            public void afterCommit() {
                menuProducer.sendMenuRefreshMessage();
            }

        });
    }

    @Override
    public List<MenuDO> getMenus() {
        return menuRepository.findAll();
    }

    @Override
    public List<MenuDO> getTenantMenus(MenuListReqVO reqVO) {
        List<MenuDO> menus = getMenus(reqVO);
        // 开启多租户的情况下，需要过滤掉未开通的菜单
        tenantService.handleTenantMenu(menuIds -> menus.removeIf(menu -> !menuIds.contains(menu.getId())));
        return menus;
    }

    @Override
    public List<MenuDO> getMenus(MenuListReqVO reqVO) {
        return menuRepository.findAll(getMenusSpecification(reqVO));
    }

    private static Specification<MenuDO> getMenusSpecification(MenuListReqVO reqVO) {
        return (Specification<MenuDO>) (root, query, cb) -> {
            List<Predicate> predicateList = new ArrayList<>();
            if (StringUtils.isNotBlank(reqVO.getName())) {
                predicateList.add(cb.equal(root.get("name"), reqVO.getName()));
            }

            if (Objects.nonNull(reqVO.getStatus())) {
                predicateList.add(cb.equal(root.get("status"), reqVO.getStatus()));
            }
            return cb.and(predicateList.toArray(new Predicate[0]));
        };
    }

    @Override
    public List<MenuDO> getMenuListFromCache(Collection<Integer> menuTypes, Collection<Integer> menusStatuses) {
        // 任一一个参数为空，则返回空
        if (CollUtils.isAnyEmpty(menuTypes, menusStatuses)) {
            return Collections.emptyList();
        }
        // 创建新数组，避免缓存被修改
        return menuCache.values().stream().filter(menu -> menuTypes.contains(menu.getType())
                && menusStatuses.contains(menu.getStatus()))
                .collect(Collectors.toList());
    }

    @Override
    public List<MenuDO> getMenuListFromCache(Collection<Long> menuIds, Collection<Integer> menuTypes,
                                             Collection<Integer> menusStatuses) {
        // 任一一个参数为空，则返回空
        if (CollUtils.isAnyEmpty(menuIds, menuTypes, menusStatuses)) {
            return Collections.emptyList();
        }
        return menuCache.values().stream().filter(menu -> menuIds.contains(menu.getId())
                && menuTypes.contains(menu.getType())
                && menusStatuses.contains(menu.getStatus()))
                .collect(Collectors.toList());
    }

    @Override
    public List<MenuDO> getMenuListByPermissionFromCache(String permission) {
        return new ArrayList<>(permissionMenuCache.get(permission));
    }

    @Override
    public MenuDO getMenu(Long id) {
        return menuRepository.findById(id).orElse(null);
    }

    /**
     * 校验父菜单是否合法
     *
     * 1. 不能设置自己为父菜单
     * 2. 父菜单不存在
     * 3. 父菜单必须是 {@link MenuTypeEnum#MENU} 菜单类型
     *
     * @param parentId 父菜单编号
     * @param childId 当前菜单编号
     */
    @VisibleForTesting
    public void checkParentResource(Long parentId, Long childId) {
        if (parentId == null || MenuIdEnum.ROOT.getId().equals(parentId)) {
            return;
        }
        // 不能设置自己为父菜单
        if (parentId.equals(childId)) {
            throw ServiceExceptionUtil.exception(MENU_PARENT_ERROR);
        }
        Optional<MenuDO> menuOptional = menuRepository.findById(parentId);
        // 父菜单不存在
        if (menuOptional.isEmpty()) {
            throw ServiceExceptionUtil.exception(MENU_PARENT_NOT_EXISTS);
        }
        // 父菜单必须是目录或者菜单类型
        if (!MenuTypeEnum.DIR.getType().equals(menuOptional.get().getType())
            && !MenuTypeEnum.MENU.getType().equals(menuOptional.get().getType())) {
            throw ServiceExceptionUtil.exception(MENU_PARENT_NOT_DIR_OR_MENU);
        }
    }

    /**
     * 校验菜单是否合法
     *
     * 1. 校验相同父菜单编号下，是否存在相同的菜单名
     *
     * @param name 菜单名字
     * @param parentId 父菜单编号
     * @param id 菜单编号
     */
    @VisibleForTesting
    public void checkResource(Long parentId, String name, Long id) {
        Optional<MenuDO> menuOptional = menuRepository.findByParentIdAndName(parentId, name);
        if (menuOptional.isEmpty()) {
            return;
        }
        // 如果 id 为空，说明不用比较是否为相同 id 的菜单
        if (id == null) {
            throw ServiceExceptionUtil.exception(MENU_NAME_DUPLICATE);
        }
        if (!menuOptional.get().getId().equals(id)) {
            throw ServiceExceptionUtil.exception(MENU_NAME_DUPLICATE);
        }
    }

    /**
     * 初始化菜单的通用属性。
     *
     * 例如说，只有目录或者菜单类型的菜单，才设置 icon
     *
     * @param menu 菜单
     */
    private void initMenuProperty(MenuDO menu) {
        // 菜单为按钮类型时，无需 component、icon、path 属性，进行置空
        if (MenuTypeEnum.BUTTON.getType().equals(menu.getType())) {
            menu.setComponent("");
            menu.setIcon("");
            menu.setPath("");
        }
    }

}
