/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */

package com.ethan.system.service.tenant;

import com.ethan.system.controller.admin.tenant.vo.tenant.TenantCreateReqVO;
import com.ethan.system.controller.admin.tenant.vo.tenant.TenantExportReqVO;
import com.ethan.system.controller.admin.tenant.vo.tenant.TenantPageReqVO;
import com.ethan.system.controller.admin.tenant.vo.tenant.TenantUpdateReqVO;
import com.ethan.system.dal.entity.tenant.TenantDO;
import com.ethan.system.service.tenant.handler.TenantInfoHandler;
import com.ethan.system.service.tenant.handler.TenantMenuHandler;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Set;

public interface TenantService {

    /**
     * 创建租户
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createTenant(@Valid TenantCreateReqVO createReqVO);

    /**
     * 更新租户
     *
     * @param updateReqVO 更新信息
     */
    void updateTenant(@Valid TenantUpdateReqVO updateReqVO);

    /**
     * 更新租户的角色菜单
     *
     * @param tenantId 租户编号
     * @param menuIds 菜单编号数组
     */
    void updateTenantRoleMenu(Long tenantId, Set<Long> menuIds);

    /**
     * 删除租户
     *
     * @param id 编号
     */
    void deleteTenant(Long id);

    /**
     * 获得租户
     *
     * @param id 编号
     * @return 租户
     */
    TenantDO getTenant(Long id);

    /**
     * 获得租户分页
     *
     * @param pageReqVO 分页查询
     * @return 租户分页
     */
    Page<TenantDO> getTenantPage(TenantPageReqVO pageReqVO);

    /**
     * 获得租户列表, 用于 Excel 导出
     *
     * @param exportReqVO 查询条件
     * @return 租户列表
     */
    List<TenantDO> getTenantList(TenantExportReqVO exportReqVO);

    /**
     * 获得名字对应的租户
     *
     * @param name 组户名
     * @return 租户
     */
    TenantDO getTenantByName(String name);

    /**
     * 获得使用指定套餐的租户数量
     *
     * @param packageId 租户套餐编号
     * @return 租户数量
     */
    Long getTenantCountByPackageId(Long packageId);

    /**
     * 获得使用指定套餐的租户数组
     *
     * @param packageId 租户套餐编号
     * @return 租户数组
     */
    List<TenantDO> getTenantListByPackageId(Long packageId);

    /**
     * 进行租户的信息处理逻辑
     * 其中，租户编号从 {@link TenantContextHolder} 上下文中获取
     *
     * @param handler 处理器
     */
    void handleTenantInfo(TenantInfoHandler handler);

    /**
     * 进行租户的菜单处理逻辑
     * 其中，租户编号从 {@link TenantContextHolder} 上下文中获取
     *
     * @param handler 处理器
     */
    void handleTenantMenu(TenantMenuHandler handler);

    /**
     * 获得所有租户
     *
     * @return 租户编号数组
     */
    List<Long> getTenantIds();

    /**
     * 校验租户是否合法
     *
     * @param id 租户编号
     */
    void validTenant(Long id);
}
