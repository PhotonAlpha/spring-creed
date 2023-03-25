package com.ethan.security.core.service;

import com.ethan.common.utils.collection.CollUtils;
import com.ethan.security.utils.SecurityFrameworkUtils;
import jakarta.annotation.Resource;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;

import java.util.Arrays;

import static com.ethan.common.utils.WebFrameworkUtils.getLoginUserId;

/**
 * 默认的 {@link SecurityFrameworkService} 实现类
 *
 */
@AllArgsConstructor
// @Component("ss")
public class SecurityFrameworkServiceImpl implements SecurityFrameworkService {

    @Resource
    private PermissionApi permissionApi;


    @Override
    public boolean hasPermission(String permission) {
        return hasAnyPermissions(permission);
    }

    @Override
    public boolean hasAnyPermissions(String... permissions) {
        return permissionApi.hasAnyPermissions(getLoginUserId(), permissions);
    }

    @Override
    public boolean hasRole(String role) {
        return hasAnyRoles(role);
    }

    @Override
    public boolean hasAnyRoles(String... roles) {
        return permissionApi.hasAnyRoles(getLoginUserId(), roles);
    }

    @Override
    public boolean hasScope(String scope) {
        return hasAnyScopes(scope);
    }

    @Override
    public boolean hasAnyScopes(String... scope) {
        Authentication authentication = SecurityFrameworkUtils.getAuthentication();
        if (authentication == null) {
            return false;
        }
        return CollUtils.containsAny(authentication.getAuthorities(), Arrays.asList(scope));
    }

}
