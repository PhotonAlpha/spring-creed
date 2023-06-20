package com.ethan.system.service.user;

import com.ethan.common.constant.CommonStatusEnum;
import com.ethan.common.exception.ServiceException;
import com.ethan.common.pojo.PageResult;
import com.ethan.common.utils.collection.CollUtils;
import com.ethan.security.websecurity.entity.CreedUser;
import com.ethan.security.websecurity.repository.CreedUserRepository;
import com.ethan.system.controller.admin.user.vo.profile.UserProfileUpdatePasswordReqVO;
import com.ethan.system.controller.admin.user.vo.profile.UserProfileUpdateReqVO;
import com.ethan.system.controller.admin.user.vo.user.UserCreateReqVO;
import com.ethan.system.controller.admin.user.vo.user.UserExportReqVO;
import com.ethan.system.controller.admin.user.vo.user.UserImportExcelVO;
import com.ethan.system.controller.admin.user.vo.user.UserImportRespVO;
import com.ethan.system.controller.admin.user.vo.user.UserPageReqVO;
import com.ethan.system.controller.admin.user.vo.user.UserUpdateReqVO;
import com.ethan.system.convert.user.UserConvert;
import com.ethan.system.dal.entity.dept.DeptDO;
import com.ethan.system.dal.entity.dept.UserPostDO;
import com.ethan.system.dal.repository.dept.UserPostRepository;
import com.ethan.system.service.dept.DeptService;
import com.ethan.system.service.dept.PostService;
import com.ethan.system.service.file.FileService;
import com.ethan.system.service.permission.PermissionService;
import com.ethan.system.service.tenant.TenantService;
import com.google.common.annotations.VisibleForTesting;
import jakarta.annotation.Resource;
import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import static com.ethan.common.exception.util.ServiceExceptionUtil.exception;
import static com.ethan.common.utils.collection.CollUtils.convertSet;
import static com.ethan.system.constant.ErrorCodeConstants.USER_EMAIL_EXISTS;
import static com.ethan.system.constant.ErrorCodeConstants.USER_IMPORT_LIST_IS_EMPTY;
import static com.ethan.system.constant.ErrorCodeConstants.USER_IS_DISABLE;
import static com.ethan.system.constant.ErrorCodeConstants.USER_MOBILE_EXISTS;
import static com.ethan.system.constant.ErrorCodeConstants.USER_NOT_EXISTS;
import static com.ethan.system.constant.ErrorCodeConstants.USER_PASSWORD_FAILED;
import static com.ethan.system.constant.ErrorCodeConstants.USER_USERNAME_EXISTS;

/**
 * 后台用户 Service 实现类
 * 
 */
@Service("adminUserService")
@Slf4j
public class AdminUserServiceImpl implements AdminUserService {

    @Value("${sys.user.init-password:1}")
    private String userInitPassword;

    // @Resource
    // private AdminUserRepository adminUserRepository;

    @Resource
    private CreedUserRepository consumerRepository;
    // @Resource
    // private CreedUserDetailsManager userDetailsManager;

    @Resource
    private DeptService deptService;
    @Resource
    private PostService postService;
    @Resource
    private PermissionService permissionService;
    @Resource
    private PasswordEncoder passwordEncoder;
    @Resource
    @Lazy // 延迟，避免循环依赖报错
    private TenantService tenantService;

    @Resource
    private UserPostRepository userPostRepository;

    @Resource
    private FileService fileService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String createUser(UserCreateReqVO reqVO) {
        // 校验账户配合
        /* tenantService.handleTenantInfo(tenant -> {
            long count = adminUserRepository.count();
            if (count >= tenant.getAccountCount()) {
                throw exception(USER_COUNT_MAX, tenant.getAccountCount());
            }
        }); */
        // 校验正确性
        checkCreateOrUpdate(null, reqVO.getUsername(), reqVO.getPhone(), reqVO.getEmail(),
                reqVO.getDeptId(), reqVO.getPostIds());
        // 插入用户
        CreedUser user = UserConvert.INSTANCE.convert(reqVO);
        user.setPassword(encodePassword(reqVO.getPassword())); // 加密密码
        consumerRepository.save(user);
        // 插入关联岗位 TODO
        // if (!CollectionUtils.isEmpty(user.getPostIds())) {
        //     userPostRepository.saveAll(convertList(user.getPostIds(),
        //             postId -> new UserPostDO().setUserId(user.getId()).setPostId(postId)));
        // }
        return user.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUser(UserUpdateReqVO reqVO) {
        // 校验正确性
        checkCreateOrUpdate(reqVO.getId(), reqVO.getUsername(), reqVO.getPhone(), reqVO.getEmail(),
                reqVO.getDeptId(), reqVO.getPostIds());
        // 更新用户
        CreedUser updateObj = UserConvert.INSTANCE.convert(reqVO);
        consumerRepository.save(updateObj);

        // UserBaseVO baseVO = new UserBaseVO();
        // userDetailsManager.updateUser(baseVO);
        // 更新岗位
        // updateUserPost(reqVO, updateObj);
    }

    private void updateUserPost(UserUpdateReqVO reqVO, CreedUser updateObj) {
        String userId = reqVO.getId();
        // Set<Long> dbPostIds = convertSet(userPostRepository.findByUserId(userId), UserPostDO::getPostId);
        // 计算新增和删除的岗位编号 TODO
        /* Set<Long> postIds = updateObj.getPostIds();
        Collection<Long> createPostIds = CollUtils.subtract(postIds, dbPostIds);
        Collection<Long> deletePostIds = CollUtils.subtract(dbPostIds, postIds);
        // 执行新增和删除。对于已经授权的菜单，不用做任何处理
        if (!CollectionUtils.isEmpty(createPostIds)) {
            userPostRepository.saveAll(convertList(createPostIds,
                    postId -> new UserPostDO().setUserId(userId).setPostId(postId)));
        }
        if (!CollectionUtils.isEmpty(deletePostIds)) {
            userPostRepository.deleteByUserIdAndPostIdIn(userId, deletePostIds);
        } */
    }

    @Override
    public void updateUserLogin(String id, String loginIp) {
        consumerRepository.findById(id).ifPresent(c -> {
            c.setLoginIp(loginIp);
            consumerRepository.save(c);
        });
    }

    @Override
    public void updateUserProfile(String id, UserProfileUpdateReqVO reqVO) {
        // 校验正确性
        checkUserExists(id);
        checkEmailUnique(id, reqVO.getEmail());
        checkMobileUnique(id, reqVO.getMobile());
        // 执行更新
        // adminUserRepository.save(UserConvert.INSTANCE.convert(reqVO).setId(id));
    }

    @Override
    public void updateUserPassword(String id, UserProfileUpdatePasswordReqVO reqVO) {
        // 校验旧密码密码
        checkOldPassword(id, reqVO.getOldPassword());
        // 执行更新
        updateUserPassword(id, reqVO.getNewPassword());
        // Optional<CreedConsumer> updateObj = consumerRepository.findById(id);
        // updateObj.ifPresent(c -> {
        //     c.setPassword(encodePassword(reqVO.getNewPassword()));
        //     consumerRepository.save(c);
        // }); // 加密密码
    }

    @Override
    public String updateUserAvatar(String id, InputStream avatarFile) throws Exception {
        checkUserExists(id);
        // 存储文件
        String avatar = fileService.createFile(null, null, IOUtils.toByteArray(avatarFile));
        // 更新路径
        // CreedConsumer sysUserDO = new CreedConsumer();
        // sysUserDO.setId(id);
        // sysUserDO.setAvatar(avatar);
        // adminUserRepository.save(sysUserDO);

        Optional<CreedUser> updateObj = consumerRepository.findById(id);
        updateObj.ifPresent(c -> {
            c.setAvatar(avatar);
            consumerRepository.save(c);
        });
        return avatar;
    }

    @Override
    public void updateUserPassword(String id, String password) {
        // 校验用户存在
        checkUserExists(id);
        // 更新密码
        // CreedConsumer updateObj = new CreedConsumer();
        // updateObj.setId(id);
        // updateObj.setPassword(encodePassword(password)); // 加密密码
        // adminUserRepository.save(updateObj);
        Optional<CreedUser> updateObj = consumerRepository.findById(id);
        updateObj.ifPresent(c -> {
            c.setPassword(encodePassword(password)); // 加密密码
            consumerRepository.save(c);
        });
    }

    @Override
    public void updateUserStatus(String id, Integer status) {
        // 校验用户存在
        checkUserExists(id);
        // 更新状态
        Optional<CreedUser> updateObj = consumerRepository.findById(id);
        updateObj.ifPresent(c -> {
            c.setEnabled(CommonStatusEnum.convert(status)); // 加密密码
            consumerRepository.save(c);
        });
        // CreedConsumer updateObj = new CreedConsumer();
        // updateObj.setId(id);
        // updateObj.setStatus(status);
        // adminUserRepository.save(updateObj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUser(String id) {
        // 校验用户存在
        checkUserExists(id);
        // 删除用户
        consumerRepository.deleteById(id);
        // 删除用户关联数据 TODO
        // permissionService.processUserDeleted(id);
        // 删除用户岗位 TODO
        // userPostRepository.deleteByUserId(id);
    }

    @Override
    public CreedUser getUserByUsername(String username) {
        return consumerRepository.findByUsername(username).orElse(null);
    }

    @Override
    public CreedUser getUserByMobile(String mobile) {
        return consumerRepository.findByPhone(mobile).orElse(null);
    }

    @Override
    public PageResult<CreedUser> getUserPage(UserPageReqVO reqVO) {
        Page<CreedUser> page = consumerRepository.findAll(getUserPageSpecification(reqVO, getDeptCondition(reqVO.getDeptId())), PageRequest.of(reqVO.getPageNo(), reqVO.getPageSize()));
        return new PageResult(page.getContent(), page.getTotalElements());
    }

    private static Specification<CreedUser> getUserPageSpecification(UserPageReqVO reqVO , Collection<Long> deptIds) {
        return (Specification<CreedUser>) (root, query, cb) -> {
            List<Predicate> predicateList = new ArrayList<>();
            if (StringUtils.isNotBlank(reqVO.getUsername())) {
                predicateList.add(cb.like(cb.lower(root.get("username").as(String.class)),
                        "%" + reqVO.getUsername().toLowerCase() + "%"));
            }
            if (StringUtils.isNotBlank(reqVO.getMobile())) {
                predicateList.add(cb.like(cb.lower(root.get("phone").as(String.class)),
                        "%" + reqVO.getMobile().toLowerCase() + "%"));
            }
            if (Objects.nonNull(reqVO.getStatus())) {
                predicateList.add(cb.equal(root.get("status"), reqVO.getStatus()));
            }
            if (Objects.nonNull(reqVO.getDeptId())) {
                predicateList.add(root.get("dept_id").in(deptIds));
            }
            query.orderBy(cb.desc(root.get("id")));
            return cb.and(predicateList.toArray(new Predicate[0]));
        };
    }

    @Override
    public CreedUser getUser(String id) {
        return consumerRepository.findById(id).orElse(null);
    }

    @Override
    public List<CreedUser> getUsersByDeptIds(Collection<Long> deptIds) {
        if (CollectionUtils.isEmpty(deptIds)) {
            return Collections.emptyList();
        }
        // return adminUserRepository.findByDeptIdIn(deptIds);
        return null;
    }

    @Override
    public List<CreedUser> getUsersByPostIds(Collection<Long> postIds) {
        if (CollectionUtils.isEmpty(postIds)) {
            return Collections.emptyList();
        }
        Set<Long> userIds = convertSet(userPostRepository.findByPostIdIn(postIds), UserPostDO::getUserId);
        if (CollectionUtils.isEmpty(userIds)) {
            return Collections.emptyList();
        }
        // return adminUserRepository.findAllById(userIds);
        return null;
    }

    @Override
    public List<CreedUser> getUsers(Collection<String> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Collections.emptyList();
        }
        return consumerRepository.findAllById(ids);
    }

    @Override
    public void validUsers(Set<String> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return;
        }
        // 获得岗位信息
        List<CreedUser> users = consumerRepository.findAllById(ids);
        Map<String, CreedUser> userMap = CollUtils.convertMap(users, CreedUser::getId);
        // 校验
        ids.forEach(id -> {
            CreedUser user = userMap.get(id);
            if (user == null) {
                throw exception(USER_NOT_EXISTS);
            }
            if (!CommonStatusEnum.ENABLE.equals(user.getEnabled())) {
                throw exception(USER_IS_DISABLE, user.getNickname());
            }
        });
    }

    @Override
    public List<CreedUser> getUsers(UserExportReqVO reqVO) {
        return consumerRepository.findAll(getUsersSpecification(reqVO, getDeptCondition(reqVO.getDeptId())));
    }

    private static Specification<CreedUser> getUsersSpecification(UserExportReqVO reqVO , Collection<Long> deptIds) {
        return (root, query, cb) -> {
            List<Predicate> predicateList = new ArrayList<>();
            if (StringUtils.isNotBlank(reqVO.getUsername())) {
                predicateList.add(cb.like(cb.lower(root.get("username").as(String.class)),
                        "%" + reqVO.getUsername().toLowerCase() + "%"));
            }
            if (StringUtils.isNotBlank(reqVO.getMobile())) {
                predicateList.add(cb.like(cb.lower(root.get("phone").as(String.class)),
                        "%" + reqVO.getMobile().toLowerCase() + "%"));
            }
            if (Objects.nonNull(reqVO.getStatus())) {
                predicateList.add(cb.equal(root.get("status"), reqVO.getStatus()));
            }
            if (Objects.nonNull(reqVO.getDeptId())) {
                predicateList.add(root.get("dept_id").in(deptIds));
            }
            query.orderBy(cb.desc(root.get("id")));
            return cb.and(predicateList.toArray(new Predicate[0]));
        };
    }

    @Override
    public List<CreedUser> getUsersByNickname(String nickname) {
        return consumerRepository.findByNickname(nickname);
    }

    @Override
    public List<CreedUser> getUsersByUsername(String username) {
        return consumerRepository.findByUsernameContainingIgnoreCase(username);
    }

    /**
     * 获得部门条件：查询指定部门的子部门编号们，包括自身
     * @param deptId 部门编号
     * @return 部门编号集合
     */
    private Set<Long> getDeptCondition(Long deptId) {
        if (deptId == null) {
            return Collections.emptySet();
        }
        Set<Long> deptIds = convertSet(deptService.getDeptsByParentIdFromCache(
                deptId, true), DeptDO::getId);
        deptIds.add(deptId); // 包括自身
        return deptIds;
    }

    private void checkCreateOrUpdate(String id, String username, String mobile, String email,
                                     Long deptId, Set<Long> postIds) {
        // 校验用户存在
        checkUserExists(id);
        // 校验用户名唯一
        checkUsernameUnique(id, username);
        // 校验手机号唯一
        checkMobileUnique(id, mobile);
        // 校验邮箱唯一
        checkEmailUnique(id, email);
        // 校验部门处于开启状态
        deptService.validDepts(Collections.singleton(deptId));
        // 校验岗位处于开启状态
        postService.validPosts(postIds);
    }

    @VisibleForTesting
    public void checkUserExists(String id) {
        if (id == null) {
            return;
        }
        Optional<CreedUser> userOptional = consumerRepository.findById(id);
        if (userOptional.isEmpty()) {
            throw exception(USER_NOT_EXISTS);
        }
    }

    @VisibleForTesting
    public void checkUsernameUnique(String id, String username) {
        if (StringUtils.isBlank(username)) {
            return;
        }
        Optional<CreedUser> userOptional = consumerRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            return;
        }
        // 如果 id 为空，说明不用比较是否为相同 id 的用户
        if (id == null) {
            throw exception(USER_USERNAME_EXISTS);
        }

        if (userOptional.map(CreedUser::getId).filter(id::equals).isPresent()) {
            throw exception(USER_USERNAME_EXISTS);
        }
    }

    @VisibleForTesting
    public void checkEmailUnique(String id, String email) {
        if (StringUtils.isBlank(email)) {
            return;
        }
        Optional<CreedUser> userOptional = consumerRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            return;
        }
        // 如果 id 为空，说明不用比较是否为相同 id 的用户
        if (id == null) {
            throw exception(USER_EMAIL_EXISTS);
        }
        if (userOptional.map(CreedUser::getId).filter(id::equals).isPresent()) {
            throw exception(USER_EMAIL_EXISTS);
        }
    }

    @VisibleForTesting
    public void checkMobileUnique(String id, String mobile) {
        if (StringUtils.isBlank(mobile)) {
            return;
        }
        Optional<CreedUser> userOptional = consumerRepository.findByPhone(mobile);
        if (userOptional.isEmpty()) {
            return;
        }
        // 如果 id 为空，说明不用比较是否为相同 id 的用户
        if (id == null) {
            throw exception(USER_MOBILE_EXISTS);
        }
        if (userOptional.map(CreedUser::getId).filter(id::equals).isPresent()) {
            throw exception(USER_MOBILE_EXISTS);
        }
    }

    /**
     * 校验旧密码
     * @param id          用户 id
     * @param oldPassword 旧密码
     */
    @VisibleForTesting
    public void checkOldPassword(String id, String oldPassword) {
        Optional<CreedUser> userOptional = consumerRepository.findById(id);
        if (userOptional.isEmpty()) {
            throw exception(USER_NOT_EXISTS);
        }
        // Function<String, Boolean> fun = pwd -> isPasswordMatch(oldPassword, pwd);
        if (userOptional.map(CreedUser::getPassword)
                .filter(pwd -> isPasswordMatch(oldPassword, pwd)).isEmpty()) {
            throw exception(USER_PASSWORD_FAILED);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class) // 添加事务，异常则回滚所有导入
    public UserImportRespVO importUsers(List<UserImportExcelVO> importUsers, boolean isUpdateSupport) {
        if (CollectionUtils.isEmpty(importUsers)) {
            throw exception(USER_IMPORT_LIST_IS_EMPTY);
        }
        UserImportRespVO respVO = UserImportRespVO.builder().createUsernames(new ArrayList<>())
                .updateUsernames(new ArrayList<>()).failureUsernames(new LinkedHashMap<>()).build();
        importUsers.forEach(importUser -> {
            // 校验，判断是否有不符合的原因
            try {
                checkCreateOrUpdate(null, null, importUser.getMobile(), importUser.getEmail(),
                        importUser.getDeptId(), null);
            } catch (ServiceException ex) {
                respVO.getFailureUsernames().put(importUser.getUsername(), ex.getMessage());
                return;
            }
            // 判断如果不存在，在进行插入
            Optional<CreedUser> existUserOptional = consumerRepository.findByUsername(importUser.getUsername());
            if (existUserOptional.isEmpty()) {
                consumerRepository.save(UserConvert.INSTANCE.convert(importUser)
                        .setPassword(encodePassword(userInitPassword))); // 设置默认密码
                respVO.getCreateUsernames().add(importUser.getUsername());
                return;
            }
            // 如果存在，判断是否允许更新
            if (!isUpdateSupport) {
                respVO.getFailureUsernames().put(importUser.getUsername(), USER_USERNAME_EXISTS.getMsg());
                return;
            }
            CreedUser creedUser = existUserOptional.get();
            UserConvert.INSTANCE.update(importUser, creedUser);
            consumerRepository.save(creedUser);
            respVO.getUpdateUsernames().add(importUser.getUsername());
        });
        return respVO;
    }

    @Override
    public List<CreedUser> getUsersByStatus(CommonStatusEnum status) {
        return consumerRepository.findByEnabled(status);
    }

    @Override
    public boolean isPasswordMatch(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    /**
     * 对密码进行加密
     *
     * @param password 密码
     * @return 加密后的密码
     */
    private String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

}
