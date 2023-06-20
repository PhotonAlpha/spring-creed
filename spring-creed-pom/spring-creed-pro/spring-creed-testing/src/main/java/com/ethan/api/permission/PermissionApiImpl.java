package com.ethan.api.permission;

import com.ethan.security.core.service.PermissionApi;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Set;

/**
 * 权限 API 实现类
 *
 */
@Service
public class PermissionApiImpl implements PermissionApi {



    @Override
    public Set<String> getUserRoleIdListByRoleIds(Collection<String> roleIds) {
        return Set.of("ADMIN");
    }

    @Override
    public boolean hasAnyPermissions(String userId, String... permissions) {
        return true;
    }

    @Override
    public boolean hasAnyRoles(String userId, String... roles) {
        return true;
    }
}
