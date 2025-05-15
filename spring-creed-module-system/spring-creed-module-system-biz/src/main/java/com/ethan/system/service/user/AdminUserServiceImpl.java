package com.ethan.system.service.user;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.ethan.common.constant.CommonStatusEnum;
import com.ethan.common.exception.ServiceException;
import com.ethan.common.pojo.PageResult;
import com.ethan.common.utils.collection.CollUtils;
import com.ethan.infra.api.config.ConfigApi;
import com.ethan.system.controller.admin.user.vo.profile.UserProfileUpdatePasswordReqVO;
import com.ethan.system.controller.admin.user.vo.profile.UserProfileUpdateReqVO;
import com.ethan.system.controller.admin.user.vo.user.UserImportExcelVO;
import com.ethan.system.controller.admin.user.vo.user.UserImportRespVO;
import com.ethan.system.controller.admin.user.vo.user.UserPageReqVO;
import com.ethan.system.controller.admin.user.vo.user.UserSaveReqVO;
import com.ethan.system.convert.user.UserConvert;
import com.ethan.system.dal.entity.dept.SystemDeptUsers;
import com.ethan.system.dal.entity.dept.SystemDepts;
import com.ethan.system.dal.entity.dept.SystemPostUsers;
import com.ethan.system.dal.entity.dept.SystemPosts;
import com.ethan.system.dal.entity.permission.SystemUsers;
import com.ethan.system.dal.repository.dept.SystemDeptUsersRepository;
import com.ethan.system.dal.repository.dept.SystemPostUsersRepository;
import com.ethan.system.dal.repository.permission.SystemUsersRepository;
import com.ethan.system.service.dept.DeptService;
import com.ethan.system.service.dept.PostService;
import com.ethan.system.service.file.FileService;
import com.ethan.system.service.permission.PermissionService;
import com.ethan.system.service.tenant.TenantService;
import com.google.common.annotations.VisibleForTesting;
import com.mzt.logapi.context.LogRecordContext;
import com.mzt.logapi.service.impl.DiffParseFunction;
import com.mzt.logapi.starter.annotation.LogRecord;
import jakarta.annotation.Resource;
import jakarta.persistence.NamedEntityGraph;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.InputStream;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.ethan.common.exception.util.ServiceExceptionUtil.exception;
import static com.ethan.common.utils.collection.CollUtils.combine;
import static com.ethan.common.utils.collection.CollUtils.convertSet;
import static com.ethan.system.api.constant.LogRecordConstants.SYSTEM_USER_CREATE_SUB_TYPE;
import static com.ethan.system.api.constant.LogRecordConstants.SYSTEM_USER_CREATE_SUCCESS;
import static com.ethan.system.api.constant.LogRecordConstants.SYSTEM_USER_DELETE_SUB_TYPE;
import static com.ethan.system.api.constant.LogRecordConstants.SYSTEM_USER_DELETE_SUCCESS;
import static com.ethan.system.api.constant.LogRecordConstants.SYSTEM_USER_TYPE;
import static com.ethan.system.api.constant.LogRecordConstants.SYSTEM_USER_UPDATE_PASSWORD_SUB_TYPE;
import static com.ethan.system.api.constant.LogRecordConstants.SYSTEM_USER_UPDATE_PASSWORD_SUCCESS;
import static com.ethan.system.api.constant.LogRecordConstants.SYSTEM_USER_UPDATE_SUB_TYPE;
import static com.ethan.system.api.constant.LogRecordConstants.SYSTEM_USER_UPDATE_SUCCESS;
import static com.ethan.system.constant.ErrorCodeConstants.USER_EMAIL_EXISTS;
import static com.ethan.system.constant.ErrorCodeConstants.USER_IMPORT_INIT_PASSWORD;
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

    static final String USER_INIT_PASSWORD_KEY = "system.user.init-password";

    @Resource
    private SystemUsersRepository usersRepository;
    @Resource
    private SystemDeptUsersRepository deptUsersRepository;
    @Resource
    private SystemPostUsersRepository postUsersRepository;

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
    private SystemPostUsersRepository userPostRepository;

    @Resource
    private FileService fileService;
    @Resource
    private ConfigApi configApi;

    @Override
    @Transactional(rollbackFor = Exception.class)
    @LogRecord(type = SYSTEM_USER_TYPE, subType = SYSTEM_USER_CREATE_SUB_TYPE, bizNo = "{{#user.id}}",
            success = SYSTEM_USER_CREATE_SUCCESS)
    public Long createUser(UserSaveReqVO reqVO) {
        // 校验账户配合
        /* tenantService.handleTenantInfo(tenant -> {
            long count = adminUserRepository.count();
            if (count >= tenant.getAccountCount()) {
                throw exception(USER_COUNT_MAX, tenant.getAccountCount());
            }
        }); */
        // 校验正确性
        validateUserForCreateOrUpdate(null, reqVO.getUsername(), reqVO.getPhone(), reqVO.getEmail(),
                reqVO.getDeptId(), reqVO.getPostIds());
        // 2.1 插入用户
        SystemUsers user = UserConvert.INSTANCE.convert(reqVO);
        user.setPassword(encodePassword(reqVO.getPassword())); // 加密密码
        usersRepository.save(user);
        // 2.2 插入关联部门
        if (Objects.nonNull(reqVO.getDeptId())) {
            SystemDepts dept = deptService.getDept(reqVO.getDeptId());
            deptUsersRepository.save(new SystemDeptUsers(user, dept));

        }
        // 2.3 插入关联岗位
        if (!CollectionUtils.isEmpty(reqVO.getPostIds())) {
            List<SystemPosts> postList = postService.getPostList(reqVO.getPostIds());
            List<SystemPostUsers> userPosts = postList.stream().filter(Objects::nonNull)
                    .map(post -> new SystemPostUsers(user, post)).toList();
            userPostRepository.saveAll(userPosts);
        }

        // 3. 记录操作日志上下文
        LogRecordContext.putVariable("user", user);
        return user.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @LogRecord(type = SYSTEM_USER_TYPE, subType = SYSTEM_USER_UPDATE_SUB_TYPE, bizNo = "{{#updateReqVO.id}}",
            success = SYSTEM_USER_UPDATE_SUCCESS)
    public void updateUser(UserSaveReqVO updateReqVO) {
        // 1. 校验正确性
        SystemUsers users = validateUserForCreateOrUpdate(updateReqVO.getId(), updateReqVO.getUsername(),
                updateReqVO.getPhone(), updateReqVO.getEmail(), updateReqVO.getDeptId(), updateReqVO.getPostIds());

        // 2.1 更新用户
        UserConvert.INSTANCE.update(updateReqVO, users);
        usersRepository.save(users);
        // 2.2 更新岗位
        updateUserPost(updateReqVO, users);
        // 2.3 更新部门
        updateUserDept(updateReqVO, users);

        // 3. 记录操作日志上下文
        LogRecordContext.putVariable(DiffParseFunction.OLD_OBJECT, UserConvert.INSTANCE.convert2UserSaveReq(users));
        LogRecordContext.putVariable("user", users);
    }

    private void updateUserDept(UserSaveReqVO reqVO, SystemUsers users) {
        log.info("updateUserDept:{}", users.getId());
        List<SystemDeptUsers> systemDeptUsers = Optional.ofNullable(users.getDeptUsers()).orElse(Collections.emptyList());
        Map<Long, SystemDeptUsers> dbDeptIdMapping = CollUtils.convertMap(systemDeptUsers, CollUtils.combine(SystemDeptUsers::getDepts, SystemDepts::getId));

        if (Objects.isNull(reqVO.getDeptId())) {
            //无部门更新
            return;
        }
        // 计算新增和删除的岗位编号
        Set<Long> deptIds = Collections.singleton(reqVO.getDeptId());
        Collection<Long> createPostIds = CollUtil.subtract(deptIds, dbDeptIdMapping.keySet());
        Collection<Long> deletePostIds = CollUtil.subtract(dbDeptIdMapping.keySet(), deptIds);
        // 执行新增和删除。对于已经授权的岗位，不用做任何处理
        if (!CollectionUtils.isEmpty(createPostIds)) {
            log.info("createPostIds:{}", createPostIds);
            deptUsersRepository.saveAll(CollUtils.convertList(Optional.of(createPostIds).map(deptService::getDeptList).orElse(Collections.emptyList()),
                    dept -> new SystemDeptUsers(users, dept)));
        }
        if (!CollectionUtils.isEmpty(deletePostIds)) {
            log.info("deletePostIds:{}", deletePostIds);
            deptUsersRepository.deleteAll(CollUtils.convertList(deletePostIds, dbDeptIdMapping::get));
        }
    }

    private void updateUserPost(UserSaveReqVO reqVO, SystemUsers users) {
        log.info("updateUserPost:{}", users.getId());

        List<SystemPostUsers> systemPostUsers = Optional.ofNullable(users.getPostUsers()).orElse(Collections.emptyList());
        // .stream().collect(Collectors.toMap())
        Map<Long, SystemPostUsers> dbPostIdMapping = CollUtils.convertMap(systemPostUsers, CollUtils.combine(SystemPostUsers::getPosts, SystemPosts::getId));

        // Long userId = reqVO.getId();
        // Set<Long> dbPostIds = convertSet(userPostMapper.selectListByUserId(userId), UserPostDO::getPostId);
        // 计算新增和删除的岗位编号
        Set<Long> postIds = CollUtil.emptyIfNull(reqVO.getPostIds());
        Collection<Long> createPostIds = CollUtil.subtract(postIds, dbPostIdMapping.keySet());
        Collection<Long> deletePostIds = CollUtil.subtract(dbPostIdMapping.keySet(), postIds);
        // 执行新增和删除。对于已经授权的岗位，不用做任何处理
        if (!CollectionUtil.isEmpty(createPostIds)) {
            log.info("createPostIds:{}", createPostIds);
            userPostRepository.saveAll(CollUtils.convertList(Optional.of(createPostIds).map(postService::getPostList).orElse(Collections.emptyList()),
                    post -> new SystemPostUsers(users, post)));
        }
        if (!CollectionUtil.isEmpty(deletePostIds)) {
            log.info("deletePostIds:{}", deletePostIds);
            userPostRepository.deleteAll(CollUtils.convertList(deletePostIds, dbPostIdMapping::get));
        }
    }

    @Override
    public void updateUserLogin(Long id, String loginIp) {
        usersRepository.findById(id).ifPresent(c -> {
            c.setLoginIp(loginIp);
            c.setLoginDate(Instant.now());
            usersRepository.save(c);
        });
    }

    @Override
    public void updateUserProfile(Long id, UserProfileUpdateReqVO reqVO) {
        // 校验正确性
        SystemUsers systemUsers = validateUserExists(id);
        validateEmailUnique(id, reqVO.getEmail());
        validateMobileUnique(id, reqVO.getMobile());
        // 执行更新
        UserConvert.INSTANCE.update(reqVO, systemUsers);
        usersRepository.save(systemUsers);
    }

    @Override
    public String updateUserAvatar(Long id, InputStream avatarFile) throws Exception {
        SystemUsers systemUsers = validateUserExists(id);
        // 存储文件
        String avatar = "NA";// TODO fileService.createFile(IoUtil.readBytes(avatarFile));
        // 更新路径
        systemUsers.setAvatar(avatar);
        usersRepository.save(systemUsers);
        return avatar;
    }

    @Override
    public void updateUserPassword(Long id, UserProfileUpdatePasswordReqVO reqVO) {
        // 校验旧密码密码
        validateOldPassword(id, reqVO.getOldPassword());
        // 执行更新
        updateUserPassword(id, reqVO.getNewPassword());
    }

    @Override
    @LogRecord(type = SYSTEM_USER_TYPE, subType = SYSTEM_USER_UPDATE_PASSWORD_SUB_TYPE, bizNo = "{{#id}}",
            success = SYSTEM_USER_UPDATE_PASSWORD_SUCCESS)
    public void updateUserPassword(Long id, String password) {
        // 1. 校验用户存在
        SystemUsers user = validateUserExists(id);

        // 2. 更新密码
        user.setPassword(encodePassword(password)); // 加密密码
        usersRepository.save(user);

        // 3. 记录操作日志上下文
        LogRecordContext.putVariable("user", user);
        LogRecordContext.putVariable("newPassword", user.getPassword());
    }

    @Override
    public void updateUserStatus(Long id, Integer status) {
        // 校验用户存在
        SystemUsers systemUsers = validateUserExists(id);
        // 更新状态
        systemUsers.setEnabled(CommonStatusEnum.convert(status));
        usersRepository.save(systemUsers);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @LogRecord(type = SYSTEM_USER_TYPE, subType = SYSTEM_USER_DELETE_SUB_TYPE, bizNo = "{{#id}}",
            success = SYSTEM_USER_DELETE_SUCCESS)
    public void deleteUser(Long id) {
        log.info("deleteUser:{}", id);
        // 2.1 校验用户存在
        SystemUsers user = validateUserExists(id);

        // 2.2 删除用户关联数据
        permissionService.processUserDeleted(id);
        // 2.2 删除用户岗位
        var deptUsers = user.getDeptUsers();
        log.info("deptUsers size:{}", deptUsers.size());
        deptUsersRepository.deleteAll(deptUsers);
        // 2.3 删除用户部门
        var postUsers = user.getPostUsers();
        log.info("postUsers size:{}", postUsers.size());
        postUsersRepository.deleteAll(postUsers);
        // 2.4 删除用户
        usersRepository.delete(user);

        // 3. 记录操作日志上下文
        LogRecordContext.putVariable("user", user);
    }

    @Override
    public SystemUsers getUserByUsername(String username) {
        return usersRepository.findByUsername(username).orElse(null);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return usersRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("username:%s not found!".formatted(username)));
    }

    @Override
    public SystemUsers getUserByMobile(String mobile) {
        return usersRepository.findByPhone(mobile).orElse(null);
    }

    @Override
    public PageResult<SystemUsers> getUserPage(UserPageReqVO reqVO) {
        // Page<SystemUsers> page = usersRepository.findByCondition(reqVO, getDeptCondition(reqVO.getDeptId()));
        //TODO 临时禁用dept的check
        Page<SystemUsers> page = usersRepository.findByCondition(reqVO, null);
        return new PageResult(page.getContent(), page.getTotalElements());
    }

    @Override
    public SystemUsers getUser(Long id) {
        Assert.notNull(id, "User Id can not be null");
        return usersRepository.findById(id).orElse(null);
    }

    @Override
    public List<SystemUsers> getUserListByDeptIds(Collection<Long> deptIds) {
        if (CollectionUtils.isEmpty(deptIds)) {
            return Collections.emptyList();
        }
        Page<SystemUsers> page = usersRepository.findByCondition(new UserPageReqVO(), new HashSet<>(deptIds));
        return page.getContent();
    }

    @Override
    public List<SystemUsers> getUserListByPostIds(Collection<Long> postIds) {
        if (CollectionUtils.isEmpty(postIds)) {
            return Collections.emptyList();
        }
        Set<Long> userIds = postService.getPostList(postIds)
                .stream().map(SystemPosts::getPostUsers)
                .flatMap(Collection::stream)
                .map(combine(SystemPostUsers::getUsers, SystemUsers::getId))
                .collect(Collectors.toSet());
        if (CollectionUtils.isEmpty(userIds)) {
            return Collections.emptyList();
        }
        return usersRepository.findAllById(userIds);
    }

    @Override
    public List<SystemUsers> getUserList(Collection<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Collections.emptyList();
        }
        return usersRepository.findAllById(ids);
    }

    @Override
    public void validateUserList(Collection<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return;
        }
        // 获得用户信息
        List<SystemUsers> users = usersRepository.findAllById(ids);
        Map<Long, SystemUsers> userMap = CollUtils.convertMap(users, SystemUsers::getId);
        // 校验
        ids.forEach(id -> {
            SystemUsers user = userMap.get(id);
            if (user == null) {
                throw exception(USER_NOT_EXISTS);
            }
            if (!CommonStatusEnum.ENABLE.equals(user.getEnabled())) {
                throw exception(USER_IS_DISABLE, user.getNickname());
            }
        });
    }

    @Override
    public List<SystemUsers> getUserListByNickname(String nickname) {
        return usersRepository.findByNickname(nickname);
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
        Set<Long> deptIds = convertSet(deptService.getChildDeptList(deptId), SystemDepts::getId);
        deptIds.add(deptId); // 包括自身
        return deptIds;
    }

    private SystemUsers validateUserForCreateOrUpdate(Long id, String username, String mobile, String email,
                                                      Long deptId, Set<Long> postIds)  {
        // 校验用户存在
        SystemUsers systemUsers = validateUserExists(id);
        // 校验用户名唯一
        validateUsernameUnique(id, username);
        // 校验手机号唯一
        validateMobileUnique(id, mobile);
        // 校验邮箱唯一
        validateEmailUnique(id, email);
        // 校验部门处于开启状态
        deptService.validateDeptList(Collections.singleton(deptId));
        // 校验岗位处于开启状态
        postService.validatePostList(postIds);
        return systemUsers;
    }

    @VisibleForTesting
    SystemUsers validateUserExists(Long id) {
        if (id == null) {
            return null;
        }
        return usersRepository.findById(id).orElseThrow(() -> exception(USER_NOT_EXISTS));
    }

    @VisibleForTesting
    void validateUsernameUnique(Long id, String username) {
        if (!StringUtils.hasText(username)) {
            return;
        }
        Optional<SystemUsers> userOptional = usersRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            return;
        }
        // 如果 id 为空，说明不用比较是否为相同 id 的用户
        if (id == null) {
            throw exception(USER_USERNAME_EXISTS);
        }

        if (userOptional.map(SystemUsers::getId).filter(id::equals).isEmpty()) {
            throw exception(USER_USERNAME_EXISTS);
        }
    }

    @VisibleForTesting
    void validateEmailUnique(Long id, String email) {
        if (!StringUtils.hasText(email)) {
            return;
        }
        Optional<SystemUsers> userOptional = usersRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            return;
        }
        // 如果 id 为空，说明不用比较是否为相同 id 的用户
        if (id == null) {
            throw exception(USER_EMAIL_EXISTS);
        }
        if (userOptional.map(SystemUsers::getId).filter(id::equals).isEmpty()) {
            throw exception(USER_EMAIL_EXISTS);
        }
    }

    @VisibleForTesting
    void validateMobileUnique(Long id, String phoneNo) {
        if (!StringUtils.hasText(phoneNo)) {
            return;
        }
        Optional<SystemUsers> userOptional = usersRepository.findByPhone(phoneNo);
        if (userOptional.isEmpty()) {
            return;
        }
        // 如果 id 为空，说明不用比较是否为相同 id 的用户
        if (id == null) {
            throw exception(USER_MOBILE_EXISTS);
        }
        if (userOptional.map(SystemUsers::getId).filter(id::equals).isEmpty()) {
            throw exception(USER_MOBILE_EXISTS);
        }
    }

    /**
     * 校验旧密码
     * @param id          用户 id
     * @param oldPassword 旧密码
     */
    @VisibleForTesting
    void validateOldPassword(Long id, String oldPassword) {
        Optional<SystemUsers> userOptional = usersRepository.findById(id);
        if (userOptional.isEmpty()) {
            throw exception(USER_NOT_EXISTS);
        }
        if (userOptional.map(SystemUsers::getPassword)
                .filter(pwd -> isPasswordMatch(oldPassword, pwd)).isEmpty()) {
            throw exception(USER_PASSWORD_FAILED);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class) // 添加事务，异常则回滚所有导入
    public UserImportRespVO importUserList(List<UserImportExcelVO> importUsers, boolean isUpdateSupport) {
        // 1.1 参数校验
        if (CollUtil.isEmpty(importUsers)) {
            throw exception(USER_IMPORT_LIST_IS_EMPTY);
        }
        // 1.2 初始化密码不能为空
        String initPassword = configApi.getConfigValueByKey(USER_INIT_PASSWORD_KEY);
        if (!StringUtils.hasText(initPassword)) {
            throw exception(USER_IMPORT_INIT_PASSWORD);
        }

        // 2. 遍历，逐个创建 or 更新
        UserImportRespVO respVO = UserImportRespVO.builder().createUsernames(new ArrayList<>())
                .updateUsernames(new ArrayList<>()).failureUsernames(new LinkedHashMap<>()).build();
        importUsers.forEach(importUser -> {
            // 校验，判断是否有不符合的原因
            try {
                validateUserForCreateOrUpdate(null, null, importUser.getMobile(), importUser.getEmail(),
                        importUser.getDeptId(), null);
            } catch (ServiceException ex) {
                respVO.getFailureUsernames().put(importUser.getUsername(), ex.getMessage());
                return;
            }
            // 判断如果不存在，在进行插入
            Optional<SystemUsers> existUser = usersRepository.findByUsername(importUser.getUsername());
            if (existUser.isEmpty()) {
                usersRepository.save(UserConvert.INSTANCE.convert(importUser)
                        .setPassword(encodePassword(initPassword))); // 设置默认密码及空岗位编号数组
                respVO.getCreateUsernames().add(importUser.getUsername());
                return;
            }
            // 如果存在，判断是否允许更新
            if (!isUpdateSupport) {
                respVO.getFailureUsernames().put(importUser.getUsername(), USER_USERNAME_EXISTS.getMsg());
                return;
            }
            existUser.ifPresent(usr -> {
                UserConvert.INSTANCE.update(importUser, existUser.get());
                usersRepository.save(usr);
            });
            respVO.getUpdateUsernames().add(importUser.getUsername());
        });
        return respVO;
    }

    @Override
    public List<SystemUsers> getUserListByStatus(Integer status) {
        return usersRepository.findByEnabled(CommonStatusEnum.convert(status));
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


    @Override
    public void afterPropertiesSet() throws Exception {
        //TODO
    }

    @Override
    public void setMessageSource(MessageSource messageSource) {
        //TODO
    }

    @Override
    public List<String> findAllGroups() {
        //TODO
        return null;
    }

    @Override
    public List<String> findUsersInGroup(String groupName) {
        //TODO

        return null;
    }

    @Override
    public void createGroup(String groupName, List<GrantedAuthority> authorities) {
        //TODO
    }

    @Override
    public void deleteGroup(String groupName) {
        //TODO
    }

    @Override
    public void renameGroup(String oldName, String newName) {
        //TODO
    }

    @Override
    public void addUserToGroup(String username, String group) {
        //TODO
    }

    @Override
    public void removeUserFromGroup(String username, String groupName) {
        //TODO
    }

    @Override
    public List<GrantedAuthority> findGroupAuthorities(String groupName) {
        //TODO
        return null;
    }

    @Override
    public void addGroupAuthority(String groupName, GrantedAuthority authority) {
        //TODO
    }

    @Override
    public void removeGroupAuthority(String groupName, GrantedAuthority authority) {
        //TODO
    }

    @Override
    public void createUser(UserDetails user) {
        //TODO
    }

    @Override
    public void updateUser(UserDetails user) {
        //TODO
    }

    @Override
    public void deleteUser(String username) {
        //TODO
    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {
        //TODO
    }

    @Override
    public boolean userExists(String username) {
        //TODO
        return false;
    }
}
