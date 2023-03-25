package com.ethan.system.api.permission;

import com.ethan.security.core.service.PermissionApi;
import com.ethan.system.service.permission.PermissionService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Set;

/**
 * 权限 API 实现类
 *
 */
@Service
public class PermissionApiImpl implements PermissionApi {

    @Resource
    private PermissionService permissionService;

    @Override
    public Set<String> getUserRoleIdListByRoleIds(Collection<String> roleIds) {
        return permissionService.getUserRoleIdListByRoleIds(roleIds);
    }

    @Override
    public boolean hasAnyPermissions(String userId, String... permissions) {
        return permissionService.hasAnyPermissions(userId, permissions);
    }

    @Override
    public boolean hasAnyRoles(String userId, String... roles) {
        return permissionService.hasAnyRoles(userId, roles);
    }

/*     @Override
    public DeptDataPermissionRespDTO getDeptDataPermission(Long userId) {
        return permissionService.getDeptDataPermission(userId);
    } */

}
