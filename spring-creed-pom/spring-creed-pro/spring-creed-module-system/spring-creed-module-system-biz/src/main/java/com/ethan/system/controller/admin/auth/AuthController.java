package com.ethan.system.controller.admin.auth;

import cn.hutool.core.collection.CollUtil;
import com.ethan.common.common.R;
import com.ethan.common.constant.CommonStatusEnum;
import com.ethan.framework.operatelog.annotations.OperateLog;
import com.ethan.security.websecurity.entity.CreedAuthorities;
import com.ethan.security.websecurity.entity.CreedUser;
import com.ethan.system.constant.logger.LoginLogTypeEnum;
import com.ethan.system.controller.admin.auth.vo.AuthLoginReqVO;
import com.ethan.system.controller.admin.auth.vo.AuthLoginRespVO;
import com.ethan.system.controller.admin.auth.vo.AuthPermissionInfoRespVO;
import com.ethan.system.controller.admin.auth.vo.AuthSmsLoginReqVO;
import com.ethan.system.controller.admin.auth.vo.AuthSmsSendReqVO;
import com.ethan.system.controller.admin.auth.vo.AuthSocialLoginReqVO;
import com.ethan.system.convert.auth.AuthConvert;
import com.ethan.system.dal.entity.permission.MenuDO;
import com.ethan.system.dal.entity.permission.RoleDO;
import com.ethan.system.dal.entity.permission.SystemMenus;
import com.ethan.system.dal.entity.permission.SystemRoles;
import com.ethan.system.dal.entity.permission.SystemUsers;
import com.ethan.system.dal.entity.user.AdminUserDO;
import com.ethan.system.service.auth.AdminAuthService;
import com.ethan.system.service.permission.MenuService;
import com.ethan.system.service.permission.PermissionService;
import com.ethan.system.service.permission.RoleService;
import com.ethan.system.service.social.SocialUserService;
import com.ethan.system.service.user.AdminUserService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.annotation.security.PermitAll;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.oauth2.server.resource.web.DefaultBearerTokenResolver;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static com.ethan.common.common.R.success;
import static com.ethan.common.utils.WebFrameworkUtils.getLoginUserId;
import static com.ethan.common.utils.WebFrameworkUtils.getLoginUserIdL;
import static com.ethan.common.utils.collection.CollUtils.convertSet;

@Tag(name = "管理后台 - 认证")
@RestController
@RequestMapping("/system/auth")
@Validated
@Slf4j
public class AuthController {

    @Resource
    private AdminAuthService authService;
    @Resource
    private AdminUserService userService;
    @Resource
    private RoleService roleService;
    @Resource
    private PermissionService permissionService;
    @Resource
    private SocialUserService socialUserService;
    @Resource
    private MenuService menuService;

    @PostMapping("/login")
    @PermitAll
    @Schema(name = "使用账号密码登录")
    @OperateLog(enable = false) // 避免 Post 请求被记录操作日志
    public R<AuthLoginRespVO> login(@RequestBody @Valid AuthLoginReqVO reqVO) {
        System.out.println("abc==>test2");
        log.info("====login==={}",reqVO);
        CompletableFuture<String> future1 = authService.async1();
        CompletableFuture<String> future2 = authService.async2();
        return success(authService.login(reqVO));
    }

    @PostMapping("/logout")
    @PermitAll
    @Schema(name = "登出系统")
    @OperateLog(enable = false) // 避免 Post 请求被记录操作日志
    public R<Boolean> logout(HttpServletRequest request) {
        DefaultBearerTokenResolver defaultBearerTokenResolver = new DefaultBearerTokenResolver();
        String token = defaultBearerTokenResolver.resolve(request);
        if (StringUtils.isNotBlank(token)) {
            authService.logout(token, LoginLogTypeEnum.LOGOUT_SELF.getType());
        }
        return success(true);
    }

    @PostMapping("/refresh-token")
    @PermitAll
    @Schema(name = "刷新令牌")
    @Parameter(name = "refreshToken", description = "刷新令牌", required = true, schema = @Schema(implementation = String.class))
    @OperateLog(enable = false) // 避免 Post 请求被记录操作日志
    public R<AuthLoginRespVO> refreshToken(@RequestParam("refreshToken") String refreshToken) {
        return success(authService.refreshToken(refreshToken));
    }

    @GetMapping("/get-permission-info")
    @Schema(name = "获取登录用户的权限信息")
    public R<AuthPermissionInfoRespVO> getPermissionInfo() {
        // 1.1 获得用户信息
        SystemUsers user = userService.getUser(getLoginUserIdL());
        if (user == null) {
            return success(null);
        }

        // 1.2 获得角色列表
        Set<Long> roleIds = permissionService.getUserRoleIdListByUserId(getLoginUserIdL());
        if (CollUtil.isEmpty(roleIds)) {
            return success(AuthConvert.INSTANCE.convert(user, Collections.emptyList(), Collections.emptyList()));
        }
        List<SystemRoles> roles = roleService.getRoleList(roleIds);
        roles.removeIf(role -> !CommonStatusEnum.ENABLE.getStatus().equals(role.getEnabled())); // 移除禁用的角色

        // 1.3 获得菜单列表
        Set<Long> menuIds = permissionService.getRoleMenuListByRoleId(convertSet(roles, SystemRoles::getId));
        List<SystemMenus> menuList = menuService.getMenuList(menuIds);
        menuList = menuService.filterDisableMenus(menuList);

        // 2. 拼接结果返回
        return success(AuthConvert.INSTANCE.convert(user, roles, menuList));
    }

    /* @GetMapping("/list-menus")
    @Schema(name = "获得登录用户的菜单列表")
    public R<List<MenuVO>> getMenus() {
        // 获得角色列表
        Set<String> roleIds = permissionService.getUserRoleIdsFromCache(getLoginUserId(), singleton(CommonStatusEnum.ENABLE.getStatus()));
        // 获得用户拥有的菜单列表
        List<MenuDO> menuList = permissionService.getRoleMenuListFromCache(roleIds,
                SetUtils.asSet(MenuTypeEnum.DIR.getType(), MenuTypeEnum.MENU.getType()), // 只要目录和菜单类型
                singleton(CommonStatusEnum.ENABLE.getStatus())); // 只要开启的
        // 转换成 Tree 结构返回
        return success(AuthConvert.INSTANCE.buildMenuTree(menuList));
    } */

    // ========== 短信登录相关 ==========

    @PostMapping("/sms-login")
    @PermitAll
    @Schema(name = "使用短信验证码登录")
    @OperateLog(enable = false) // 避免 Post 请求被记录操作日志
    public R<AuthLoginRespVO> smsLogin(@RequestBody @Valid AuthSmsLoginReqVO reqVO) {
        return success(authService.smsLogin(reqVO));
    }

    @PostMapping("/send-sms-code")
    @PermitAll
    @Schema(name = "发送手机验证码")
    @OperateLog(enable = false) // 避免 Post 请求被记录操作日志
    public R<Boolean> sendLoginSmsCode(@RequestBody @Valid AuthSmsSendReqVO reqVO) {
        authService.sendSmsCode(reqVO);
        return success(true);
    }

    // ========== 社交登录相关 ==========

    @GetMapping("/social-auth-redirect")
    @PermitAll
    @Schema(name = "社交授权的跳转")
    @Parameters({
            @Parameter(name = "type", description = "社交类型", required = true, schema = @Schema(implementation = Integer.class)),
            @Parameter(name = "redirectUri", description = "回调路径", schema = @Schema(implementation = String.class))
    })
    public R<String> socialLogin(@RequestParam("type") Integer type,
                                 @RequestParam("redirectUri") String redirectUri) {
        return success(socialUserService.getAuthorizeUrl(type, redirectUri));
    }

    @PostMapping("/social-login")
    @PermitAll
    @Schema(name = "社交快捷登录，使用 code 授权码", description = "适合未登录的用户，但是社交账号已绑定用户")
    @OperateLog(enable = false) // 避免 Post 请求被记录操作日志
    public R<AuthLoginRespVO> socialQuickLogin(@RequestBody @Valid AuthSocialLoginReqVO reqVO) {
        return success(authService.socialLogin(reqVO));
    }

}
