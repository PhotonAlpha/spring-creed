package com.ethan.system.constant.permission;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.Strings;

/**
 * 角色标识枚举
 */
@Getter
@AllArgsConstructor
public enum RoleCodeEnum {

    SUPER_ADMIN("SUPER_ADMIN", "超级管理员"),
    TENANT_ADMIN("TENANT_ADMIN", "租户管理员"),
    ;

    /**
     * 角色编码
     */
    private final String code;
    /**
     * 名字
     */
    private final String name;

    public static boolean isSuperAdmin(String code) {
        return Strings.CI.equalsAny(code, SUPER_ADMIN.getCode());
    }

}
