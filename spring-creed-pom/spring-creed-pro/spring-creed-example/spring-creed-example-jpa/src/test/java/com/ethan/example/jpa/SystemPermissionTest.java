package com.ethan.example.jpa;


import com.ethan.example.jpa.constant.RoleTypeEnum;
import com.ethan.example.jpa.dal.permission.SystemAuthorities;
import com.ethan.example.jpa.dal.permission.SystemGroupRoles;
import com.ethan.example.jpa.dal.permission.SystemGroupUsers;
import com.ethan.example.jpa.dal.permission.SystemGroups;
import com.ethan.example.jpa.dal.permission.SystemRoleAuthorities;
import com.ethan.example.jpa.dal.permission.SystemRoles;
import com.ethan.example.jpa.dal.permission.SystemUserAuthorities;
import com.ethan.example.jpa.dal.permission.SystemUserRoles;
import com.ethan.example.jpa.dal.permission.SystemUsers;
import com.ethan.example.jpa.repository.permission.SystemAuthoritiesRepository;
import com.ethan.example.jpa.repository.permission.SystemGroupRolesRepository;
import com.ethan.example.jpa.repository.permission.SystemGroupUsersRepository;
import com.ethan.example.jpa.repository.permission.SystemGroupsRepository;
import com.ethan.example.jpa.repository.permission.SystemMenuRolesRepository;
import com.ethan.example.jpa.repository.permission.SystemMenusRepository;
import com.ethan.example.jpa.repository.permission.SystemRoleAuthoritiesRepository;
import com.ethan.example.jpa.repository.permission.SystemRolesRepository;
import com.ethan.example.jpa.repository.permission.SystemUserAuthoritiesRepository;
import com.ethan.example.jpa.repository.permission.SystemUserRolesRepository;
import com.ethan.example.jpa.repository.permission.SystemUsersRepository;
import jakarta.annotation.Resource;
import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SpringBootTest(classes = JpaExampleApplication.class)
@Transactional
@Slf4j
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
        systemAuthorities11.setType(RoleTypeEnum.SYSTEM);
        systemAuthorities11.setSort(1);
        SystemAuthorities systemAuthorities12 = new SystemAuthorities();
        systemAuthorities12.setAuthority("system:user:create");
        systemAuthorities12.setType(RoleTypeEnum.SYSTEM);
        systemAuthorities12.setSort(1);
        SystemAuthorities systemAuthorities13 = new SystemAuthorities();
        systemAuthorities13.setAuthority("system:user:update");
        systemAuthorities13.setType(RoleTypeEnum.SYSTEM);
        systemAuthorities13.setSort(1);
        SystemAuthorities systemAuthorities14 = new SystemAuthorities();
        systemAuthorities14.setAuthority("system:user:delete");
        systemAuthorities14.setType(RoleTypeEnum.SYSTEM);
        systemAuthorities14.setSort(1);
        SystemAuthorities systemAuthorities15 = new SystemAuthorities();
        systemAuthorities15.setAuthority("system:user:export");
        systemAuthorities15.setType(RoleTypeEnum.SYSTEM);
        systemAuthorities15.setSort(1);
        List<SystemAuthorities> sysUserAuth = Arrays.asList(systemAuthorities11, systemAuthorities12, systemAuthorities13, systemAuthorities14, systemAuthorities15);
        authoritiesRepository.saveAll(sysUserAuth);

        SystemAuthorities systemAuthorities21 = new SystemAuthorities();
        systemAuthorities21.setAuthority("system:role:query");
        systemAuthorities21.setType(RoleTypeEnum.SYSTEM);
        systemAuthorities21.setSort(2);
        SystemAuthorities systemAuthorities22 = new SystemAuthorities();
        systemAuthorities22.setAuthority("system:role:create");
        systemAuthorities22.setType(RoleTypeEnum.SYSTEM);
        systemAuthorities22.setSort(2);
        SystemAuthorities systemAuthorities23 = new SystemAuthorities();
        systemAuthorities23.setAuthority("system:role:update");
        systemAuthorities23.setType(RoleTypeEnum.SYSTEM);
        systemAuthorities23.setSort(2);
        SystemAuthorities systemAuthorities24 = new SystemAuthorities();
        systemAuthorities24.setAuthority("system:role:delete");
        systemAuthorities24.setType(RoleTypeEnum.SYSTEM);
        systemAuthorities24.setSort(2);
        SystemAuthorities systemAuthorities25 = new SystemAuthorities();
        systemAuthorities25.setAuthority("system:role:export");
        systemAuthorities25.setType(RoleTypeEnum.SYSTEM);
        systemAuthorities25.setSort(2);
        List<SystemAuthorities> sysRoleAuth = Arrays.asList(systemAuthorities21, systemAuthorities22, systemAuthorities23, systemAuthorities24, systemAuthorities25);
        authoritiesRepository.saveAll(sysRoleAuth);

        SystemAuthorities systemAuthorities31 = new SystemAuthorities();
        systemAuthorities31.setAuthority("system:dept:query");
        systemAuthorities31.setType(RoleTypeEnum.CUSTOM);
        systemAuthorities31.setSort(3);
        SystemAuthorities systemAuthorities32 = new SystemAuthorities();
        systemAuthorities32.setAuthority("system:dept:create");
        systemAuthorities32.setType(RoleTypeEnum.CUSTOM);
        systemAuthorities32.setSort(3);
        SystemAuthorities systemAuthorities33 = new SystemAuthorities();
        systemAuthorities33.setAuthority("system:dept:update");
        systemAuthorities33.setType(RoleTypeEnum.CUSTOM);
        systemAuthorities33.setSort(3);
        SystemAuthorities systemAuthorities34 = new SystemAuthorities();
        systemAuthorities34.setAuthority("system:dept:delete");
        systemAuthorities34.setType(RoleTypeEnum.CUSTOM);
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
    void customQueryTest() {
        // 自定义查询测试
        Specification<SystemRoleAuthorities> specification = (root, query, cb) -> {
            List<Predicate> predicateList = new ArrayList<>();
            predicateList.add(cb.equal(root.get("roles").get("id"), 1));
            query.orderBy(cb.desc(root.get("id")));
            return cb.and(predicateList.toArray(new Predicate[0]));
        };
        List<SystemRoleAuthorities> authoritiesList = roleAuthoritiesRepository.findAll(specification);
        System.out.println("authoritiesList.size():"+authoritiesList.size());
    }

    @Test
    @Rollback(false)
    void groupUsersUpdateTest() {
        // 级联查询/新增用户测试
        Optional<SystemUsers> adminUser = usersRepository.findById(4L);
        System.out.println(adminUser.isPresent());


        adminUser.ifPresent(usr -> {
            List<SystemGroupUsers> groupUsers = usr.getGroupUsers();
            System.out.println("------" + groupUsers.size());
            List<SystemUserRoles> userRoles = usr.getUserRoles();
            System.out.println("------" + userRoles.size());
            for (SystemUserRoles userRole : userRoles) {
                SystemRoles roles = userRole.getRoles();
                System.out.println("roles:" + roles.getName());
                for (SystemRoleAuthorities roleAuthority : roles.getRoleAuthorities()) {
                    System.out.println("Authorities:" + roleAuthority.getAuthorities());
                }
            }
            List<SystemUserAuthorities> userAuthorities = usr.getUserAuthorities();
            System.out.println("------" + userAuthorities.size());
        });
        SystemUsers systemUsers13 = new SystemUsers();
        systemUsers13.setUsername("samming");
        systemUsers13.setPassword("{noop}password");
        systemUsers13.setNickname("xiaoming hero");
        systemUsers13.setRemark("remarks");
        usersRepository.save(systemUsers13);
        System.out.println("----");
        usersRepository.deleteById(systemUsers13.getId());
    }

    /**
     * 给用户加权限方式：
     *      先选用户，然后添加权限。该方式可以给用户添加任意角色或是权限。
     *      先选择角色，然后关联用户。该方式只可给用户添加角色，不能单独添加权限。
     *      菜单与权限单独关联
     */
    @Test
    @Rollback(false)
    void delinkAndLinkTest() {
        // 解除关系 以及 新增关系 测试
        // 修改 角色与权限关系，即认为是 移除 新增，不存在更新
        // 1. hr_admin 移除 system:role:export
        // 2. hr_admin 新增 system:dept:query system:dept:create system:dept:update system:dept:delete
        Specification<SystemRoles> specification = (root, query, cb) -> cb.equal(root.get("code"), "hr_admin");
        Optional<SystemRoles> hrAdminRole = rolesRepository.findOne(specification);

        hrAdminRole.ifPresent(this::consumerSystemRoles);
        log.info("修改 角色与权限关系，即认为是 移除 新增，不存在更新");
        // 修改 用户与角色关系，即认为是 移除 新增，不存在更新\
        // 1. erming 添加进 HR管理员组， 即拥有 hr_admin 相关权限
        Specification<SystemUsers> userSpecification = (root, query, cb) -> cb.equal(root.get("username"), "erming");
        Optional<SystemUsers> systemUsersOptional = usersRepository.findOne(userSpecification); // 3

        Specification<SystemGroups> groupSpecification = (root, query, cb) -> cb.equal(root.get("groupName"), "SYSTEM:HR");
        Optional<SystemGroups> systemGroupsOptional = groupsRepository.findOne(groupSpecification); // 1

        systemUsersOptional.flatMap(usr -> systemGroupsOptional.map(grp -> new SystemGroupUsers(grp, usr)))
                .ifPresentOrElse(groupUsersRepository::save, () -> log.info("Users or Groups not existing"));
        log.info("erming 添加进 HR管理员组， 即拥有 hr_admin 相关权限");
        // 2. erming 单独添加 system:menu:query 权限
        Specification<SystemAuthorities> authSpecification = (root, query, cb) -> cb.equal(root.get("authority"), "system:menu:query");
        Optional<SystemAuthorities> authorities = authoritiesRepository.findOne(authSpecification);

        SystemAuthorities systemAuthorities11 = new SystemAuthorities();
        systemAuthorities11.setAuthority("system:menu:query");
        systemAuthorities11.setType(RoleTypeEnum.SYSTEM);
        systemAuthorities11.setSort(3);
        if (authorities.isEmpty()) {
            authoritiesRepository.save(systemAuthorities11);
        }

        systemUsersOptional.map(usr -> new SystemUserAuthorities(usr, authorities.orElse(systemAuthorities11)))
                .ifPresentOrElse(userAuthoritiesRepository::save, () -> log.info("Users not existing or systemAuthorities save failed"));
        log.info("erming 单独添加 system:menu:query 权限");


    }


    private void consumerSystemRoles(SystemRoles role) {
        List<SystemRoleAuthorities> roleAuthorities = role.getRoleAuthorities();
        List<String> existingRoles = roleAuthorities.stream().map(SystemRoleAuthorities::getAuthorities)
                .map(SystemAuthorities::getAuthority).toList();
        log.info("existingRoles:{}", existingRoles);

        Specification<SystemAuthorities> specification = (root, query, cb) -> root.get("authority").in(Arrays.asList("system:role:export", "system:dept:query", "system:dept:create", "system:dept:update", "system:dept:delete"));
        List<SystemAuthorities> authorities = authoritiesRepository.findAll(specification);
        Map<String, SystemAuthorities> authMapping = authorities.stream().collect(Collectors.toMap(SystemAuthorities::getAuthority, Function.identity()));

        List<SystemRoleAuthorities> pendingDel = roleAuthorities.stream().filter(ra -> "system:role:export".equals(ra.getAuthorities().getAuthority())).toList();
        log.info("pendingDel size:{}", pendingDel.size());
        roleAuthoritiesRepository.deleteAll(pendingDel);
        List<SystemRoleAuthorities> pendingAdd = Stream.of("system:dept:query", "system:dept:create", "system:dept:update", "system:dept:delete")
                .map(str -> new SystemRoleAuthorities(role, authMapping.get(str))).toList();
        log.info("pendingAdd size:{}", pendingAdd.size());
        roleAuthoritiesRepository.saveAll(pendingAdd);

    }

    @Test
    @Rollback(false)
    void delinkAndLinkRollbackTest() {
        // 解除关系 以及 新增关系 测试
        // 修改 角色与权限关系，即认为是 移除 新增，不存在更新
        // 1. hr_admin 移除 system:role:export
        // 2. hr_admin 新增 system:dept:query system:dept:create system:dept:update system:dept:delete
        Specification<SystemRoles> specification = (root, query, cb) -> cb.equal(root.get("code"), "hr_admin");
        Optional<SystemRoles> hrAdminRole = rolesRepository.findOne(specification);

        hrAdminRole.ifPresent(this::consumerSystemRolesRollback);
        log.info("修改 角色与权限关系，即认为是 移除 新增，不存在更新");
        // 修改 用户与角色关系，即认为是 移除 新增，不存在更新\
        // 1. erming 移除进 HR管理员组， 即拥有 hr_admin 相关权限
        String tarUername = "erming";
        Specification<SystemUsers> userSpecification = (root, query, cb) -> cb.equal(root.get("username"), tarUername);
        Optional<SystemUsers> systemUsersOptional = usersRepository.findOne(userSpecification); // 3

        Specification<SystemGroups> groupSpecification = (root, query, cb) -> cb.equal(root.get("groupName"), "SYSTEM:HR");
        Optional<SystemGroups> systemGroupsOptional = groupsRepository.findOne(groupSpecification); // 1

        systemGroupsOptional.map(SystemGroups::getGroupUsers).orElse(Collections.emptyList())
                        .stream()
                        .filter(usr -> usr.getUsers().getUsername().equals(tarUername))
                        .forEach(gu -> {
                            log.info("deleting {}", tarUername);
                            groupUsersRepository.delete(gu);
                        });


        log.info("erming 单独移除 HR管理员组， 即拥有 hr_admin 相关权限");
        // 2. erming 单独移除 system:menu:query 权限
        systemUsersOptional.map(SystemUsers::getUserAuthorities).orElse(Collections.emptyList())
                .stream()
                .filter(ua -> ua.getAuthorities().getAuthority().equals("system:menu:query"))
                .forEach(ua -> {
                    log.info("deleting {}", ua.getAuthorities().getAuthority());
                    userAuthoritiesRepository.delete(ua);
                });
        log.info("erming 单独移除 system:menu:query 权限");

    }

    private void consumerSystemRolesRollback(SystemRoles role) {
        List<SystemRoleAuthorities> roleAuthorities = role.getRoleAuthorities();
        List<String> existingRoles = roleAuthorities.stream().map(SystemRoleAuthorities::getAuthorities)
                .map(SystemAuthorities::getAuthority).toList();
        log.info("existingRoles:{}", existingRoles);

        Specification<SystemAuthorities> specification = (root, query, cb) -> root.get("authority").in(Arrays.asList("system:role:export", "system:dept:query", "system:dept:create", "system:dept:update", "system:dept:delete"));
        List<SystemAuthorities> authorities = authoritiesRepository.findAll(specification);
        Map<String, SystemAuthorities> authMapping = authorities.stream().collect(Collectors.toMap(SystemAuthorities::getAuthority, Function.identity()));

        List<SystemRoleAuthorities> pendingAdd = roleAuthorities.stream().filter(ra -> "system:role:export".equals(ra.getAuthorities().getAuthority())).toList();
            log.info("system:role:export existing:{}", pendingAdd.size());
        if (pendingAdd.isEmpty()) {
            roleAuthoritiesRepository.save(new SystemRoleAuthorities(role, authMapping.get("system:role:export")));
        }

        List<String> pendingDelAuth = Arrays.asList("system:dept:query", "system:dept:create", "system:dept:update", "system:dept:delete");
        List<SystemRoleAuthorities> pendingDel = roleAuthorities.stream().filter(ra -> pendingDelAuth.contains(ra.getAuthorities().getAuthority())).toList();
        log.info("pendingDel size:{}", pendingDel.size());
        roleAuthoritiesRepository.deleteAll(pendingDel);
    }

    @Test
    void postListTest() {
        String tarUername = "erming";
        Specification<SystemUsers> userSpecification = (root, query, cb) -> cb.equal(root.get("username"), tarUername);
        // 3. 遍历 erming 所拥有的所有权限，转为 List<String>
        Optional<SystemUsers> systemUsersOptional = usersRepository.findOne(userSpecification);
        log.info("重新查询 更新用户权限相关数据");
        List<String> groupRoleAuths = systemUsersOptional.map(SystemUsers::getGroupUsers)
                .orElse(Collections.emptyList())
                .stream().map(SystemGroupUsers::getGroups)
                .map(SystemGroups::getGroupRoles)
                .flatMap(Collection::stream)
                .map(SystemGroupRoles::getRoles)
                .map(SystemRoles::getRoleAuthorities)
                .flatMap(Collection::stream)
                .map(SystemRoleAuthorities::getAuthorities)
                .map(SystemAuthorities::getAuthority).toList();
        log.info("::::::groupRoleAuths:{}", groupRoleAuths);

        List<String> roleAuths = systemUsersOptional.map(SystemUsers::getUserRoles)
                .orElse(Collections.emptyList())
                .stream().map(SystemUserRoles::getRoles)
                .map(SystemRoles::getRoleAuthorities)
                .flatMap(Collection::stream)
                .map(SystemRoleAuthorities::getAuthorities)
                .map(SystemAuthorities::getAuthority).toList();
        log.info("::::::roleAuths:{}", roleAuths);
        List<String> userAuths = systemUsersOptional.map(SystemUsers::getUserAuthorities)
                .orElse(Collections.emptyList())
                .stream().map(SystemUserAuthorities::getAuthorities)
                .map(SystemAuthorities::getAuthority).toList();
        log.info("::::::userAuths:{}", userAuths);


        Specification<SystemGroups> groupSpecification = (root, query, cb) -> cb.equal(root.get("groupName"), "SYSTEM:HR");
        Optional<SystemGroups> systemGroupsOptional = groupsRepository.findOne(groupSpecification);
        List<String> groupAuths = systemGroupsOptional.map(SystemGroups::getGroupRoles).orElse(Collections.emptyList())
                .stream().map(SystemGroupRoles::getRoles)
                .map(SystemRoles::getRoleAuthorities)
                .flatMap(Collection::stream)
                .map(SystemRoleAuthorities::getAuthorities)
                .map(SystemAuthorities::getAuthority).toList();
        log.info("::::::SYSTEM:HR:{}", groupAuths);


        Specification<SystemRoles> specification = (root, query, cb) -> cb.equal(root.get("code"), "hr_admin");
        Optional<SystemRoles> hrAdminRole = rolesRepository.findOne(specification);
        List<String> hrAdminAuths = hrAdminRole.map(SystemRoles::getRoleAuthorities).orElse(Collections.emptyList())
                .stream().map(SystemRoleAuthorities::getAuthorities)
                .map(SystemAuthorities::getAuthority).toList();
        log.info("::::::hr_admin:{}", hrAdminAuths);
    }
}
