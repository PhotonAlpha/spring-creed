package com.ethan.system.controller.admin.oauth2;

import com.ethan.common.common.R;
import com.ethan.security.websecurity.entity.CreedUser;
import com.ethan.system.controller.admin.oauth2.vo.user.OAuth2UserInfoRespVO;
import com.ethan.system.controller.admin.oauth2.vo.user.OAuth2UserUpdateReqVO;
import com.ethan.system.convert.oauth2.OAuth2UserConvert;
import com.ethan.system.service.dept.DeptService;
import com.ethan.system.service.dept.PostService;
import com.ethan.system.service.user.AdminUserService;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.ethan.common.common.R.success;
import static com.ethan.common.utils.WebFrameworkUtils.getLoginUserId;

/**
 * 提供给外部应用调用为主
 *
 * 1. 在 getUserInfo 方法上，添加 @PreAuthorize("@ss.hasScope('user.read')") 注解，声明需要满足 scope = user.read
 * 2. 在 updateUserInfo 方法上，添加 @PreAuthorize("@ss.hasScope('user.write')") 注解，声明需要满足 scope = user.write
 *
 * 
 */
@Tag(name = "管理后台 - OAuth2.0 用户")
@RestController
@RequestMapping("/system/oauth2/user")
@Validated
@Slf4j
public class OAuth2UserController {

    @Resource
    private AdminUserService userService;
    @Resource
    private DeptService deptService;
    @Resource
    private PostService postService;

    @GetMapping("/get")
    @Schema(name = "获得用户基本信息")
    @PreAuthorize("@ss.hasScope('user.read')") //
    public R<OAuth2UserInfoRespVO> getUserInfo() {
        // 获得用户基本信息
        CreedUser user = userService.getUser(getLoginUserId());
        OAuth2UserInfoRespVO resp = OAuth2UserConvert.INSTANCE.convert(user);
        // 获得部门信息
        // if (user.getDeptId() != null) {
        //     DeptDO dept = deptService.getDept(user.getDeptId());
        //     resp.setDept(OAuth2UserConvert.INSTANCE.convert(dept));
        // }
        // 获得岗位信息
        // if (CollUtil.isNotEmpty(user.getPostIds())) {
        //     List<PostDO> posts = postService.getPosts(user.getPostIds());
        //     resp.setPosts(OAuth2UserConvert.INSTANCE.convertList(posts));
        // }
        return success(resp);
    }

    @PutMapping("/update")
    @Schema(name = "更新用户基本信息")
    @PreAuthorize("@ss.hasScope('user.write')")
    public R<Boolean> updateUserInfo(@Valid @RequestBody OAuth2UserUpdateReqVO reqVO) {
        // 这里将 UserProfileUpdateReqVO =》UserProfileUpdateReqVO 对象，实现接口的复用。
        // 主要是，AdminUserService 没有自己的 BO 对象，所以复用只能这么做
        userService.updateUserProfile(getLoginUserId(), OAuth2UserConvert.INSTANCE.convert(reqVO));
        return success(true);
    }

}
