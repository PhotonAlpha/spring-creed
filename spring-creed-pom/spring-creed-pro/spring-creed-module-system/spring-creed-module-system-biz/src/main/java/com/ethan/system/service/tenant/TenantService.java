/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */

package com.ethan.system.service.tenant;

import com.ethan.system.dal.entity.tenant.TenantDO;

public interface TenantService {

    TenantDO getTenantByName(String name);

}
