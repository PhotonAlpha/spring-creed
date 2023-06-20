package com.ethan.system.service.user;

import com.ethan.common.constant.CommonStatusEnum;
import com.ethan.common.pojo.PageResult;
import com.ethan.common.utils.collection.CollUtils;
import com.ethan.security.websecurity.entity.CreedUser;
import com.ethan.system.controller.admin.user.vo.profile.UserProfileUpdatePasswordReqVO;
import com.ethan.system.controller.admin.user.vo.profile.UserProfileUpdateReqVO;
import com.ethan.system.controller.admin.user.vo.user.UserCreateReqVO;
import com.ethan.system.controller.admin.user.vo.user.UserExportReqVO;
import com.ethan.system.controller.admin.user.vo.user.UserImportExcelVO;
import com.ethan.system.controller.admin.user.vo.user.UserImportRespVO;
import com.ethan.system.controller.admin.user.vo.user.UserPageReqVO;
import com.ethan.system.controller.admin.user.vo.user.UserUpdateReqVO;
import jakarta.validation.Valid;
import org.springframework.util.CollectionUtils;

import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 后台用户 Service 接口
 *
 * 
 */
public interface AdminUserService {

    /**
     * 创建用户
     *
     * @param reqVO 用户信息
     * @return 用户编号
     */
    String createUser(@Valid UserCreateReqVO reqVO);

    /**
     * 修改用户
     *
     * @param reqVO 用户信息
     */
    void updateUser(@Valid UserUpdateReqVO reqVO);

    /**
     * 更新用户的最后登陆信息
     *
     * @param id 用户编号
     * @param loginIp 登陆 IP
     */
    void updateUserLogin(String id, String loginIp);

    /**
     * 修改用户个人信息
     *
     * @param id 用户编号
     * @param reqVO 用户个人信息
     */
    void updateUserProfile(String id, @Valid UserProfileUpdateReqVO reqVO);

    /**
     * 修改用户个人密码
     *
     * @param id 用户编号
     * @param reqVO 更新用户个人密码
     */
    void updateUserPassword(String id, @Valid UserProfileUpdatePasswordReqVO reqVO);

    /**
     * 更新用户头像
     *
     * @param id         用户 id
     * @param avatarFile 头像文件
     */
    String updateUserAvatar(String id, InputStream avatarFile) throws Exception;

    /**
     * 修改密码
     *
     * @param id       用户编号
     * @param password 密码
     */
    void updateUserPassword(String id, String password);

    /**
     * 修改状态
     *
     * @param id     用户编号
     * @param status 状态
     */
    void updateUserStatus(String id, Integer status);

    /**
     * 删除用户
     *
     * @param id 用户编号
     */
    void deleteUser(String id);

    /**
     * 通过用户名查询用户
     *
     * @param username 用户名
     * @return 用户对象信息
     */
    CreedUser getUserByUsername(String username);

    /**
     * 通过手机号获取用户
     *
     * @param mobile 手机号
     * @return 用户对象信息
     */
    CreedUser getUserByMobile(String mobile);

    /**
     * 获得用户分页列表
     *
     * @param reqVO 分页条件
     * @return 分页列表
     */
    PageResult<CreedUser> getUserPage(UserPageReqVO reqVO);

    /**
     * 通过用户 ID 查询用户
     *
     * @param id 用户ID
     * @return 用户对象信息
     */
    CreedUser getUser(String id);

    /**
     * 获得指定部门的用户数组
     *
     * @param deptIds 部门数组
     * @return 用户数组
     */
    List<CreedUser> getUsersByDeptIds(Collection<Long> deptIds);

    /**
     * 获得指定岗位的用户数组
     *
     * @param postIds 岗位数组
     * @return 用户数组
     */
    List<CreedUser> getUsersByPostIds(Collection<Long> postIds);

    /**
     * 获得用户列表
     *
     * @param ids 用户编号数组
     * @return 用户列表
     */
    List<CreedUser> getUsers(Collection<String> ids);

    /**
     * 校验用户们是否有效。如下情况，视为无效：
     * 1. 用户编号不存在
     * 2. 用户被禁用
     *
     * @param ids 用户编号数组
     */
    void validUsers(Set<String> ids);

    /**
     * 获得用户 Map
     *
     * @param ids 用户编号数组
     * @return 用户 Map
     */
    default Map<String, CreedUser> getUserMap(Collection<String> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return new HashMap<>();
        }
        return CollUtils.convertMap(getUsers(ids), CreedUser::getId);
    }

    /**
     * 获得用户列表
     *
     * @param reqVO 列表请求
     * @return 用户列表
     */
    List<CreedUser> getUsers(UserExportReqVO reqVO);

    /**
     * 获得用户列表，基于昵称模糊匹配
     *
     * @param nickname 昵称
     * @return 用户列表
     */
    List<CreedUser> getUsersByNickname(String nickname);

    /**
     * 获得用户列表，基于用户账号模糊匹配
     *
     * @param username 用户账号
     * @return 用户列表
     */
    List<CreedUser> getUsersByUsername(String username);

    /**
     * 批量导入用户
     *
     * @param importUsers     导入用户列表
     * @param isUpdateSupport 是否支持更新
     * @return 导入结果
     */
    UserImportRespVO importUsers(List<UserImportExcelVO> importUsers, boolean isUpdateSupport);

    /**
     * 获得指定状态的用户们
     *
     * @param status 状态
     * @return 用户们
     */
    List<CreedUser> getUsersByStatus(CommonStatusEnum status);

    /**
     * 判断密码是否匹配
     *
     * @param rawPassword 未加密的密码
     * @param encodedPassword 加密后的密码
     * @return 是否匹配
     */
    boolean isPasswordMatch(String rawPassword, String encodedPassword);

}
