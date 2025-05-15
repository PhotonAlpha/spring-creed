package com.ethan.server;

import com.ethan.security.websecurity.constant.AuthorityTypeEnum;
import com.ethan.security.websecurity.constant.RoleTypeEnum;
import com.ethan.system.dal.entity.permission.SystemAuthorities;
import com.ethan.system.dal.entity.permission.SystemGroupRoles;
import com.ethan.system.dal.entity.permission.SystemGroupUsers;
import com.ethan.system.dal.entity.permission.SystemGroups;
import com.ethan.system.dal.entity.permission.SystemRoleAuthorities;
import com.ethan.system.dal.entity.permission.SystemRoles;
import com.ethan.system.dal.entity.permission.SystemUserAuthorities;
import com.ethan.system.dal.entity.permission.SystemUserRoles;
import com.ethan.system.dal.entity.permission.SystemUsers;
import com.ethan.system.dal.repository.permission.SystemAuthoritiesRepository;
import com.ethan.system.dal.repository.permission.SystemGroupRolesRepository;
import com.ethan.system.dal.repository.permission.SystemGroupUsersRepository;
import com.ethan.system.dal.repository.permission.SystemGroupsRepository;
import com.ethan.system.dal.repository.permission.SystemMenuRolesRepository;
import com.ethan.system.dal.repository.permission.SystemMenusRepository;
import com.ethan.system.dal.repository.permission.SystemRoleAuthoritiesRepository;
import com.ethan.system.dal.repository.permission.SystemRolesRepository;
import com.ethan.system.dal.repository.permission.SystemUserAuthoritiesRepository;
import com.ethan.system.dal.repository.permission.SystemUserRolesRepository;
import com.ethan.system.dal.repository.permission.SystemUsersRepository;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

@SpringBootTest(classes = ServerApplication.class)
@Transactional
public class SystemPermissionTest {
    @Resource
    SystemAuthoritiesRepository authoritiesRepository;
    @Resource
    SystemGroupsRepository groupsRepository;
    @Resource
    SystemMenusRepository menusRepository;
    @Resource
    SystemRolesRepository rolesRepository;
    @Resource
    SystemUsersRepository usersRepository;
    @Resource
    SystemGroupRolesRepository groupRolesRepository;
    @Resource
    SystemGroupUsersRepository groupUsersRepository;
    @Resource
    SystemMenuRolesRepository menuRolesRepository;
    @Resource
    SystemRoleAuthoritiesRepository roleAuthoritiesRepository;
    @Resource
    SystemUserAuthoritiesRepository userAuthoritiesRepository;
    @Resource
    SystemUserRolesRepository userRolesRepository;


    @Test
    @Rollback(false)
    void groupUsersSetupTest() {
        // 创建用户组 并关联
        SystemGroups adminGroup = new SystemGroups();
        adminGroup.setGroupName("SYSTEM:ADMIN");
        adminGroup.setRemark("超级系统管理员");

        SystemGroups hrAdminGroup = new SystemGroups();
        hrAdminGroup.setGroupName("SYSTEM:HR");
        hrAdminGroup.setRemark("HR管理员");

        groupsRepository.saveAll(Arrays.asList(adminGroup, hrAdminGroup));

        // 管理员组用户
        SystemUsers systemUsers11 = new SystemUsers();
        systemUsers11.setUsername("ethan");
        systemUsers11.setPassword("{noop}password");
        systemUsers11.setNickname("super hero");
        systemUsers11.setRemark("remarks");
        SystemUsers systemUsers12 = new SystemUsers();
        systemUsers12.setUsername("daming");
        systemUsers12.setPassword("{noop}password");
        systemUsers12.setNickname("xiaoming hero");
        systemUsers12.setRemark("remarks");
        SystemUsers systemUsers13 = new SystemUsers();
        systemUsers13.setUsername("erming");
        systemUsers13.setPassword("{noop}password");
        systemUsers13.setNickname("xiaoming hero");
        systemUsers13.setRemark("remarks");
        // 普通组用户
        SystemUsers systemUsers21 = new SystemUsers();
        systemUsers21.setUsername("xiaomi");
        systemUsers21.setPassword("{noop}password");
        systemUsers21.setNickname("xiaomi hero");
        systemUsers21.setRemark("remarks");

        SystemUsers systemUsers22 = new SystemUsers();
        systemUsers22.setUsername("huawei");
        systemUsers22.setPassword("{noop}password");
        systemUsers22.setNickname("huawei hero");
        systemUsers22.setRemark("remarks");

        SystemUsers systemUsers23 = new SystemUsers();
        systemUsers23.setUsername("vivo");
        systemUsers23.setPassword("{noop}password");
        systemUsers23.setNickname("vivo hero");
        systemUsers23.setRemark("remarks");

        SystemUsers systemUsers24 = new SystemUsers();
        systemUsers24.setUsername("oppo");
        systemUsers24.setPassword("{noop}password");
        systemUsers24.setNickname("oppo hero");
        systemUsers24.setRemark("remarks");

        usersRepository.saveAll(Arrays.asList(systemUsers11, systemUsers12, systemUsers13, systemUsers21, systemUsers22, systemUsers23, systemUsers24));


        // 角色
        SystemRoles systemRoles11 = new SystemRoles();
        systemRoles11.setName("超级管理员");
        systemRoles11.setCode("super_admin");
        systemRoles11.setSort(1);
        systemRoles11.setType(RoleTypeEnum.SYSTEM);

        SystemRoles systemRoles12 = new SystemRoles();
        systemRoles12.setName("普通角色");
        systemRoles12.setCode("common");
        systemRoles12.setSort(2);
        systemRoles12.setType(RoleTypeEnum.SYSTEM);

        SystemRoles systemRoles13 = new SystemRoles();
        systemRoles13.setName("HR 部门");
        systemRoles13.setCode("hr_common");
        systemRoles13.setSort(3);
        systemRoles13.setType(RoleTypeEnum.SYSTEM);

        SystemRoles systemRoles14 = new SystemRoles();
        systemRoles14.setName("HR 管理员");
        systemRoles14.setCode("hr_admin");
        systemRoles14.setSort(4);
        systemRoles14.setType(RoleTypeEnum.CUSTOM);
        rolesRepository.saveAll(Arrays.asList(systemRoles11, systemRoles12, systemRoles13, systemRoles14));

        // 权限
        SystemAuthorities systemAuthorities11 = new SystemAuthorities();
        systemAuthorities11.setAuthority("system:user:query");
        systemAuthorities11.setType(AuthorityTypeEnum.SYSTEM);
        systemAuthorities11.setSort(1);
        SystemAuthorities systemAuthorities12 = new SystemAuthorities();
        systemAuthorities12.setAuthority("system:user:create");
        systemAuthorities12.setType(AuthorityTypeEnum.SYSTEM);
        systemAuthorities12.setSort(1);
        SystemAuthorities systemAuthorities13 = new SystemAuthorities();
        systemAuthorities13.setAuthority("system:user:update");
        systemAuthorities13.setType(AuthorityTypeEnum.SYSTEM);
        systemAuthorities13.setSort(1);
        SystemAuthorities systemAuthorities14 = new SystemAuthorities();
        systemAuthorities14.setAuthority("system:user:delete");
        systemAuthorities14.setType(AuthorityTypeEnum.SYSTEM);
        systemAuthorities14.setSort(1);
        SystemAuthorities systemAuthorities15 = new SystemAuthorities();
        systemAuthorities15.setAuthority("system:user:export");
        systemAuthorities15.setType(AuthorityTypeEnum.SYSTEM);
        systemAuthorities15.setSort(1);
        List<SystemAuthorities> sysUserAuth = Arrays.asList(systemAuthorities11, systemAuthorities12, systemAuthorities13, systemAuthorities14, systemAuthorities15);
        authoritiesRepository.saveAll(sysUserAuth);

        SystemAuthorities systemAuthorities21 = new SystemAuthorities();
        systemAuthorities21.setAuthority("system:role:query");
        systemAuthorities21.setType(AuthorityTypeEnum.SYSTEM);
        systemAuthorities21.setSort(2);
        SystemAuthorities systemAuthorities22 = new SystemAuthorities();
        systemAuthorities22.setAuthority("system:role:create");
        systemAuthorities22.setType(AuthorityTypeEnum.SYSTEM);
        systemAuthorities22.setSort(2);
        SystemAuthorities systemAuthorities23 = new SystemAuthorities();
        systemAuthorities23.setAuthority("system:role:update");
        systemAuthorities23.setType(AuthorityTypeEnum.SYSTEM);
        systemAuthorities23.setSort(2);
        SystemAuthorities systemAuthorities24 = new SystemAuthorities();
        systemAuthorities24.setAuthority("system:role:delete");
        systemAuthorities24.setType(AuthorityTypeEnum.SYSTEM);
        systemAuthorities24.setSort(2);
        SystemAuthorities systemAuthorities25 = new SystemAuthorities();
        systemAuthorities25.setAuthority("system:role:export");
        systemAuthorities25.setType(AuthorityTypeEnum.SYSTEM);
        systemAuthorities25.setSort(2);
        List<SystemAuthorities> sysRoleAuth = Arrays.asList(systemAuthorities21, systemAuthorities22, systemAuthorities23, systemAuthorities24, systemAuthorities25);
        authoritiesRepository.saveAll(sysRoleAuth);

        SystemAuthorities systemAuthorities31 = new SystemAuthorities();
        systemAuthorities31.setAuthority("system:dept:query");
        systemAuthorities31.setType(AuthorityTypeEnum.CUSTOM);
        systemAuthorities31.setSort(3);
        SystemAuthorities systemAuthorities32 = new SystemAuthorities();
        systemAuthorities32.setAuthority("system:dept:create");
        systemAuthorities32.setType(AuthorityTypeEnum.CUSTOM);
        systemAuthorities32.setSort(3);
        SystemAuthorities systemAuthorities33 = new SystemAuthorities();
        systemAuthorities33.setAuthority("system:dept:update");
        systemAuthorities33.setType(AuthorityTypeEnum.CUSTOM);
        systemAuthorities33.setSort(3);
        SystemAuthorities systemAuthorities34 = new SystemAuthorities();
        systemAuthorities34.setAuthority("system:dept:delete");
        systemAuthorities34.setType(AuthorityTypeEnum.CUSTOM);
        systemAuthorities34.setSort(3);
        List<SystemAuthorities> sysDeptAuth = Arrays.asList(systemAuthorities31, systemAuthorities32, systemAuthorities33, systemAuthorities34);
        authoritiesRepository.saveAll(sysDeptAuth);

        // 1.赋予组->用户
        SystemGroupUsers adminGroupUsers = new SystemGroupUsers();
        adminGroupUsers.setUsers(systemUsers11);

        SystemGroupUsers hrGroupUsers = new SystemGroupUsers();
        hrGroupUsers.setUsers(systemUsers12);
        groupUsersRepository.saveAll(Arrays.asList(adminGroupUsers, hrGroupUsers));
        // 2.赋予角色->权限

        //admin role拥有所有授权
        List<SystemRoleAuthorities> adminRoleAuthorities = Stream.of(sysUserAuth, sysRoleAuth, sysDeptAuth)
                .flatMap(Collection::stream).map(authorities -> {
                    SystemRoleAuthorities adminRole = new SystemRoleAuthorities();
                    adminRole.setRoles(systemRoles11);
                    adminRole.setAuthorities(authorities);
                    return adminRole;
                }).toList();
        List<SystemRoleAuthorities> commonRoleAuthorities = Stream.of(
                                                                systemAuthorities11, systemAuthorities12,
                                                                systemAuthorities21, systemAuthorities22,
                                                                systemAuthorities31, systemAuthorities32)
                .map(authorities -> {
                    SystemRoleAuthorities adminRole = new SystemRoleAuthorities();
                    adminRole.setRoles(systemRoles12);
                    adminRole.setAuthorities(authorities);
                    return adminRole;
                }).toList();
        List<SystemRoleAuthorities> commHRRoleAuthorities = sysUserAuth.stream().map(authorities -> {
            SystemRoleAuthorities adminRole = new SystemRoleAuthorities();
            adminRole.setRoles(systemRoles13);
            adminRole.setAuthorities(authorities);
            return adminRole;
        }).toList();
        List<SystemRoleAuthorities> adminHRRoleAuthorities = sysRoleAuth.stream().map(authorities -> {
            SystemRoleAuthorities adminRole = new SystemRoleAuthorities();
            adminRole.setRoles(systemRoles14);
            adminRole.setAuthorities(authorities);
            return adminRole;
        }).toList();
        roleAuthoritiesRepository.saveAll(adminRoleAuthorities);
        roleAuthoritiesRepository.saveAll(commonRoleAuthorities);
        roleAuthoritiesRepository.saveAll(commHRRoleAuthorities);
        roleAuthoritiesRepository.saveAll(adminHRRoleAuthorities);

        // 3.赋予组->角色
        SystemGroupRoles adminGroupRoles = new SystemGroupRoles();
        adminGroupRoles.setGroups(adminGroup);
        adminGroupRoles.setRoles(systemRoles11);

        SystemGroupRoles hrGroupRoles1 = new SystemGroupRoles();
        hrGroupRoles1.setGroups(hrAdminGroup);
        hrGroupRoles1.setRoles(systemRoles13);

        SystemGroupRoles hrGroupRoles2 = new SystemGroupRoles();
        hrGroupRoles2.setGroups(hrAdminGroup);
        hrGroupRoles2.setRoles(systemRoles14);
        groupRolesRepository.saveAll(Arrays.asList(adminGroupRoles, hrGroupRoles1, hrGroupRoles2));
        // 4.赋予用户->角色
        SystemUserRoles xiaomiRoles = new SystemUserRoles();
        xiaomiRoles.setUsers(systemUsers21);
        xiaomiRoles.setRoles(systemRoles13);

        SystemUserRoles huaweiRoles = new SystemUserRoles();
        huaweiRoles.setUsers(systemUsers22);
        huaweiRoles.setRoles(systemRoles14);

        SystemUserRoles vivoRoles = new SystemUserRoles();
        vivoRoles.setUsers(systemUsers23);
        vivoRoles.setRoles(systemRoles12);

        userRolesRepository.saveAll(Arrays.asList(xiaomiRoles, huaweiRoles, vivoRoles));
        // 5.赋予用户->权限
        SystemUserAuthorities oppoAuthorities1 = new SystemUserAuthorities();
        oppoAuthorities1.setUsers(systemUsers24);
        oppoAuthorities1.setAuthorities(systemAuthorities11);
        SystemUserAuthorities oppoAuthorities2 = new SystemUserAuthorities();
        oppoAuthorities2.setUsers(systemUsers24);
        oppoAuthorities2.setAuthorities(systemAuthorities31);
        userAuthoritiesRepository.saveAll(Arrays.asList(oppoAuthorities1, oppoAuthorities2));
    }
    @Test
    @Rollback(false)
    void groupUsersUpdateTest() {
        // 更新用户组 做增删改
        SystemGroups systemGroups = new SystemGroups();
        systemGroups.setGroupName("SYSTEM:ADMIN");
        systemGroups.setRemark("系统管理员");

        SystemGroups systemGroups1 = new SystemGroups();
        systemGroups1.setGroupName("SYSTEM:HR");
        systemGroups1.setRemark("HR管理员");
        // 管理员组用户
        SystemUsers systemUsers11 = new SystemUsers();
        systemUsers11.setUsername("ethan");
        systemUsers11.setPassword("{noop}password");
        systemUsers11.setNickname("super hero");
        systemUsers11.setRemark("remarks");
        SystemUsers systemUsers12 = new SystemUsers();
        systemUsers12.setUsername("xiaoming");
        systemUsers12.setPassword("{noop}password");
        systemUsers12.setNickname("xiaoming hero");
        systemUsers12.setRemark("remarks");
        // 普通组用户
        SystemUsers systemUsers21 = new SystemUsers();
        systemUsers21.setUsername("xiaomi");
        systemUsers21.setPassword("{noop}password");
        systemUsers21.setNickname("xiaomi hero");
        systemUsers21.setRemark("remarks");

        SystemUsers systemUsers22 = new SystemUsers();
        systemUsers22.setUsername("huawei");
        systemUsers22.setPassword("{noop}password");
        systemUsers22.setNickname("huawei hero");
        systemUsers22.setRemark("remarks");

        SystemUsers systemUsers23 = new SystemUsers();
        systemUsers23.setUsername("vivo");
        systemUsers23.setPassword("{noop}password");
        systemUsers23.setNickname("vivo hero");
        systemUsers23.setRemark("remarks");

        SystemUsers systemUsers24 = new SystemUsers();
        systemUsers24.setUsername("oppo");
        systemUsers24.setPassword("{noop}password");
        systemUsers24.setNickname("oppo hero");
        systemUsers24.setRemark("remarks");

        // 角色
        SystemRoles systemRoles11 = new SystemRoles();
        systemRoles11.setName("超级管理员");
        systemRoles11.setCode("super_admin");
        systemRoles11.setSort(1);
        systemRoles11.setType(RoleTypeEnum.SYSTEM);

        SystemRoles systemRoles12 = new SystemRoles();
        systemRoles12.setName("普通角色");
        systemRoles12.setCode("common");
        systemRoles12.setSort(2);
        systemRoles12.setType(RoleTypeEnum.SYSTEM);

        SystemRoles systemRoles13 = new SystemRoles();
        systemRoles13.setName("HR 部门");
        systemRoles13.setCode("hr_common");
        systemRoles13.setSort(3);
        systemRoles13.setType(RoleTypeEnum.SYSTEM);

        SystemRoles systemRoles14 = new SystemRoles();
        systemRoles14.setName("HR 管理员");
        systemRoles14.setCode("hr_admin");
        systemRoles14.setSort(4);
        systemRoles14.setType(RoleTypeEnum.CUSTOM);


        // 权限
        SystemAuthorities systemAuthorities11 = new SystemAuthorities();
        systemAuthorities11.setAuthority("system:user:query");
        systemAuthorities11.setType(AuthorityTypeEnum.SYSTEM);
        systemAuthorities11.setSort(1);
        SystemAuthorities systemAuthorities12 = new SystemAuthorities();
        systemAuthorities12.setAuthority("system:user:create");
        systemAuthorities12.setType(AuthorityTypeEnum.SYSTEM);
        systemAuthorities12.setSort(1);
        SystemAuthorities systemAuthorities13 = new SystemAuthorities();
        systemAuthorities13.setAuthority("system:user:update");
        systemAuthorities13.setType(AuthorityTypeEnum.SYSTEM);
        systemAuthorities13.setSort(1);
        SystemAuthorities systemAuthorities14 = new SystemAuthorities();
        systemAuthorities14.setAuthority("system:user:delete");
        systemAuthorities14.setType(AuthorityTypeEnum.SYSTEM);
        systemAuthorities14.setSort(1);
        SystemAuthorities systemAuthorities15 = new SystemAuthorities();
        systemAuthorities15.setAuthority("system:user:export");
        systemAuthorities15.setType(AuthorityTypeEnum.SYSTEM);
        systemAuthorities15.setSort(1);

        SystemAuthorities systemAuthorities21 = new SystemAuthorities();
        systemAuthorities21.setAuthority("system:role:query");
        systemAuthorities21.setType(AuthorityTypeEnum.SYSTEM);
        systemAuthorities21.setSort(2);
        SystemAuthorities systemAuthorities22 = new SystemAuthorities();
        systemAuthorities22.setAuthority("system:role:create");
        systemAuthorities22.setType(AuthorityTypeEnum.SYSTEM);
        systemAuthorities22.setSort(2);
        SystemAuthorities systemAuthorities23 = new SystemAuthorities();
        systemAuthorities23.setAuthority("system:role:update");
        systemAuthorities23.setType(AuthorityTypeEnum.SYSTEM);
        systemAuthorities23.setSort(2);
        SystemAuthorities systemAuthorities24 = new SystemAuthorities();
        systemAuthorities24.setAuthority("system:role:delete");
        systemAuthorities24.setType(AuthorityTypeEnum.SYSTEM);
        systemAuthorities24.setSort(2);
        SystemAuthorities systemAuthorities25 = new SystemAuthorities();
        systemAuthorities25.setAuthority("system:role:export");
        systemAuthorities25.setType(AuthorityTypeEnum.SYSTEM);
        systemAuthorities25.setSort(2);

        SystemAuthorities systemAuthorities31 = new SystemAuthorities();
        systemAuthorities31.setAuthority("system:dept:query");
        systemAuthorities31.setType(AuthorityTypeEnum.CUSTOM);
        systemAuthorities31.setSort(3);
        SystemAuthorities systemAuthorities32 = new SystemAuthorities();
        systemAuthorities32.setAuthority("system:dept:create");
        systemAuthorities32.setType(AuthorityTypeEnum.CUSTOM);
        systemAuthorities32.setSort(3);
        SystemAuthorities systemAuthorities33 = new SystemAuthorities();
        systemAuthorities33.setAuthority("system:dept:update");
        systemAuthorities33.setType(AuthorityTypeEnum.CUSTOM);
        systemAuthorities33.setSort(3);
        SystemAuthorities systemAuthorities34 = new SystemAuthorities();
        systemAuthorities34.setAuthority("system:dept:delete");
        systemAuthorities34.setType(AuthorityTypeEnum.CUSTOM);
        systemAuthorities34.setSort(3);



    }
}
