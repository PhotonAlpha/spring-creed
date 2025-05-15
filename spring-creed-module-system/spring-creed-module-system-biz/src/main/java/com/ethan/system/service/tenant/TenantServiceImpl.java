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
import com.ethan.system.dal.repository.tenant.TenantRepository;
import com.ethan.system.service.tenant.TenantService;
import com.ethan.system.service.tenant.handler.TenantInfoHandler;
import com.ethan.system.service.tenant.handler.TenantMenuHandler;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Set;

@Service
public class TenantServiceImpl implements TenantService {
    @Resource
    private TenantRepository tenantRepository;

    @Override
    public TenantDO getTenantByName(String name) {
        Assert.notNull(name, "query name can not be null");
        return tenantRepository.findByName(name).orElse(null);
    }

    @Override
    public Long createTenant(TenantCreateReqVO createReqVO) {
        return null;
    }

    @Override
    public void updateTenant(TenantUpdateReqVO updateReqVO) {

    }

    @Override
    public void updateTenantRoleMenu(Long tenantId, Set<Long> menuIds) {

    }

    @Override
    public void deleteTenant(Long id) {

    }

    @Override
    public TenantDO getTenant(Long id) {
        return null;
    }

    @Override
    public Page<TenantDO> getTenantPage(TenantPageReqVO pageReqVO) {
        return null;
    }

    @Override
    public List<TenantDO> getTenantList(TenantExportReqVO exportReqVO) {
        return null;
    }

    @Override
    public Long getTenantCountByPackageId(Long packageId) {
        return null;
    }

    @Override
    public List<TenantDO> getTenantListByPackageId(Long packageId) {
        return null;
    }

    @Override
    public void handleTenantInfo(TenantInfoHandler handler) {

    }

    @Override
    public void handleTenantMenu(TenantMenuHandler handler) {

    }

    @Override
    public List<Long> getTenantIds() {
        return null;
    }

    @Override
    public void validTenant(Long id) {

    }
}
