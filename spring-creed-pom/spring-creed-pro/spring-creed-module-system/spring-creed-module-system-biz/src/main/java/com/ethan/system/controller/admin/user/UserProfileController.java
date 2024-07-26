package com.ethan.system.controller.admin.user;

import com.ethan.common.common.R;
import com.ethan.common.exception.util.ServiceExceptionUtil;
import com.ethan.security.websecurity.entity.CreedUser;
import com.ethan.system.constant.ErrorCodeConstants;
import com.ethan.system.controller.admin.user.vo.profile.UserProfileRespVO;
import com.ethan.system.controller.admin.user.vo.profile.UserProfileUpdatePasswordReqVO;
import com.ethan.system.controller.admin.user.vo.profile.UserProfileUpdateReqVO;
import com.ethan.system.convert.user.UserConvert;
import com.ethan.system.dal.entity.permission.SystemUsers;
import com.ethan.system.service.dept.DeptService;
import com.ethan.system.service.dept.PostService;
import com.ethan.system.service.permission.PermissionService;
import com.ethan.system.service.permission.RoleService;
import com.ethan.system.service.social.SocialUserService;
import com.ethan.system.service.user.AdminUserService;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import static com.ethan.common.common.R.success;
import static com.ethan.common.utils.WebFrameworkUtils.getLoginUserId;
import static com.ethan.common.utils.WebFrameworkUtils.getLoginUserIdL;

@Tag(name = "管理后台 - 用户个人中心")
@RestController
@RequestMapping("/system/user/profile")
@Validated
@Slf4j
public class UserProfileController {

    @Resource
    private AdminUserService userService;
    @Resource
    private DeptService deptService;
    @Resource
    private PostService postService;
    @Resource
    private PermissionService permissionService;
    @Resource
    private RoleService roleService;
    @Resource
    private SocialUserService socialService;

    @GetMapping("/get")
    @Schema(name = "获得登录用户信息")
    // @DataPermission(enable = false) // 关闭数据权限，避免只查看自己时，查询不到部门。
    public R<UserProfileRespVO> profile() {
        // 获得用户基本信息
        SystemUsers user = userService.getUser(getLoginUserIdL());
        UserProfileRespVO resp = UserConvert.INSTANCE.convert2ProfileResp(user);
        // 获得用户角色
        // List<RoleDO> userRoles = roleService.getRolesFromCache(permissionService.getUserRoleIdListByUserId(user.getId()));
        // resp.setRoles(UserConvert.INSTANCE.convertList(userRoles));
        // 获得部门信息
        // if (user.getDeptId() != null) {
        //     DeptDO dept = deptService.getDept(user.getDeptId());
        //     resp.setDept(UserConvert.INSTANCE.convert02(dept));
        // }
        // 获得岗位信息
        // if (!CollectionUtils.isEmpty(user.getPostIds())) {
        //     List<PostDO> posts = postService.getPosts(user.getPostIds());
        //     resp.setPosts(UserConvert.INSTANCE.convertList02(posts));
        // }
        // 获得社交用户信息
        // List<SocialUserDO> socialUsers = socialService.getSocialUserList(user.getId(), UserTypeEnum.ADMIN.getValue());
        // resp.setSocialUsers(UserConvert.INSTANCE.convertList03(socialUsers));
        return success(resp);
    }

    @PutMapping("/update")
    @Schema(name = "修改用户个人信息")
    public R<Boolean> updateUserProfile(@Valid @RequestBody UserProfileUpdateReqVO reqVO) {
        userService.updateUserProfile(getLoginUserIdL(), reqVO);
        return success(true);
    }

    @PutMapping("/update-password")
    @Schema(name = "修改用户个人密码")
    public R<Boolean> updateUserProfilePassword(@Valid @RequestBody UserProfileUpdatePasswordReqVO reqVO) {
        userService.updateUserPassword(getLoginUserIdL(), reqVO);
        return success(true);
    }

    @RequestMapping(value = "/update-avatar", method = {RequestMethod.POST, RequestMethod.PUT}) // 解决 uni-app 不支持 Put 上传文件的问题
    @Schema(name = "上传用户个人头像")
    public R<String> updateUserAvatar(@RequestParam("avatarFile") MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.FILE_IS_EMPTY);
        }
        String avatar = userService.updateUserAvatar(getLoginUserIdL(), file.getInputStream());
        return success(avatar);
    }

}
