package com.ethan.system.api.constant;

/**
 * System 操作日志枚举
 * 目的：统一管理，也减少 Service 里各种“复杂”字符串
 */
public interface LogRecordConstants {

    // ======================= SYSTEM_USER 用户 =======================

    String SYSTEM_USER_TYPE = "SYSTEM 用户";
    String SYSTEM_USER_CREATE_SUB_TYPE = "创建用户";
    String SYSTEM_USER_CREATE_SUCCESS = "创建了用户【{{#user.nickname}}】";
    String SYSTEM_USER_UPDATE_SUB_TYPE = "更新用户";
    String SYSTEM_USER_UPDATE_SUCCESS = "更新了用户【{{#user.nickname}}】: {_DIFF{#updateReqVO}}";
    String SYSTEM_USER_DELETE_SUB_TYPE = "删除用户";
    String SYSTEM_USER_DELETE_SUCCESS = "删除了用户【{{#user.nickname}}】";
    String SYSTEM_USER_UPDATE_PASSWORD_SUB_TYPE = "重置用户密码";
    String SYSTEM_USER_UPDATE_PASSWORD_SUCCESS = "将用户【{{#user.nickname}}】的密码从【{{#user.password}}】重置为【{{#newPassword}}】";

    // ======================= SYSTEM_ROLE 角色 =======================

    String SYSTEM_ROLE_TYPE = "SYSTEM 角色";
    String SYSTEM_ROLE_CREATE_SUB_TYPE = "创建角色";
    String SYSTEM_ROLE_CREATE_SUCCESS = "创建了角色【{{#role.name}}】";
    String SYSTEM_ROLE_UPDATE_SUB_TYPE = "更新角色";
    String SYSTEM_ROLE_UPDATE_SUCCESS = "更新了角色【{{#role.name}}】: {_DIFF{#updateReqVO}}";
    String SYSTEM_ROLE_DELETE_SUB_TYPE = "删除角色";
    String SYSTEM_ROLE_DELETE_SUCCESS = "删除了角色【{{#role.name}}】";

    // ======================= SYSTEM_AUTHORITY 权限 =======================

    String SYSTEM_AUTHORITY_TYPE = "SYSTEM 权限";
    String SYSTEM_AUTHORITY_CREATE_SUB_TYPE = "创建权限";
    String SYSTEM_AUTHORITY_CREATE_SUCCESS = "创建了权限【{{#role.name}}】";
    String SYSTEM_AUTHORITY_UPDATE_SUB_TYPE = "更新权限";
    String SYSTEM_AUTHORITY_UPDATE_SUCCESS = "更新了权限【{{#role.name}}】: {_DIFF{#updateReqVO}}";
    String SYSTEM_AUTHORITY_DELETE_SUB_TYPE = "删除权限";
    String SYSTEM_AUTHORITY_DELETE_SUCCESS = "删除了权限【{{#role.name}}】";
    // ======================= SYSTEM_MENUS 菜单 =======================

    String SYSTEM_MENU_TYPE = "SYSTEM 菜单";
    String SYSTEM_MENU_CREATE_SUB_TYPE = "创建菜单";
    String SYSTEM_MENU_CREATE_SUCCESS = "创建了菜单【{{#menu.name}}】";
    String SYSTEM_MENU_UPDATE_SUB_TYPE = "更新菜单";
    String SYSTEM_MENU_UPDATE_SUCCESS = "更新了菜单【{{#menu.name}}】: {_DIFF{#updateReqVO}}";
    String SYSTEM_MENU_DELETE_SUB_TYPE = "删除菜单";
    String SYSTEM_MENU_DELETE_SUCCESS = "删除了菜单【{{#menu.name}}】";

}
