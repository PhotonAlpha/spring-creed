/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */

package com.ethan.system.service.tenant.impl;

import com.ethan.system.dal.entity.tenant.TenantDO;
import com.ethan.system.dal.repository.tenant.TenantRepository;
import com.ethan.system.service.tenant.TenantService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
public class TenantServiceImpl implements TenantService {
    @Resource
    private TenantRepository tenantRepository;

    @Override
    public TenantDO getTenantByName(String name) {
        Assert.notNull(name, "query name can not be null");
        return tenantRepository.findByName(name).orElse(null);
    }
}
