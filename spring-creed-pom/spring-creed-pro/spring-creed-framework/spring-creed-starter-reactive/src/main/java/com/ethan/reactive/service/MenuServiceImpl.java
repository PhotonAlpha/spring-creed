package com.ethan.reactive.service;


import com.ethan.reactive.controller.vo.menu.MenuCreateReqVO;
import com.ethan.reactive.controller.vo.menu.MenuListReqVO;
import com.ethan.reactive.controller.vo.menu.MenuUpdateReqVO;
import com.ethan.reactive.dal.entity.permission.MenuDO;
import com.ethan.reactive.dal.repo.permission.MenuRepository;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.List;


/**
 * 菜单 Service 实现
 *
 * 
 */
@Service
@Slf4j
public class MenuServiceImpl implements MenuService {
    @Resource
    private MenuRepository menuRepository;

    @Override
    @PostConstruct
    public synchronized void initLocalCache() {

    }


    /**
     * 如果菜单发生变化，从数据库中获取最新的全量菜单。
     * 如果未发生变化，则返回空
     *
     * @param maxUpdateTime 当前菜单的最大更新时间
     * @return 菜单列表
     */
    private List<MenuDO> loadMenuIfUpdate(Instant maxUpdateTime) {
        // 第一步，判断是否要更新。
        if (maxUpdateTime == null) { // 如果更新时间为空，说明 DB 一定有新数据
            log.info("[loadMenuIfUpdate][首次加载全量菜单]");
        } else { // 判断数据库中是否有更新的菜单
            if (menuRepository.countByUpdateTimeGreaterThan(LocalDateTime.ofInstant(maxUpdateTime, ZoneId.systemDefault())) == 0) {
                return null;
            }
            log.info("[loadMenuIfUpdate][增量加载全量菜单]");
        }
        // 第二步，如果有更新，则从数据库加载所有菜单
        return menuRepository.findAll();
    }

    @Override
    public Long createMenu(MenuCreateReqVO reqVO) {
        return null;
    }

    @Override
    public void updateMenu(MenuUpdateReqVO reqVO) {

    }

    @Override
    public void deleteMenu(Long id) {

    }

    @Override
    public List<MenuDO> getMenus() {
        return menuRepository.findAll();
    }

    @Override
    public List<MenuDO> getTenantMenus(MenuListReqVO reqVO) {
        return null;
    }

    @Override
    public List<MenuDO> getMenus(MenuListReqVO reqVO) {
        return null;
    }

    @Override
    public List<MenuDO> getMenuListFromCache(Collection<Integer> menuTypes, Collection<Integer> menusStatuses) {
        return null;
    }

    @Override
    public List<MenuDO> getMenuListFromCache(Collection<Long> menuIds, Collection<Integer> menuTypes, Collection<Integer> menusStatuses) {
        return null;
    }

    @Override
    public List<MenuDO> getMenuListByPermissionFromCache(String permission) {
        return null;
    }

    @Override
    public MenuDO getMenu(Long id) {
        return menuRepository.findById(id).orElse(null);
    }
}
