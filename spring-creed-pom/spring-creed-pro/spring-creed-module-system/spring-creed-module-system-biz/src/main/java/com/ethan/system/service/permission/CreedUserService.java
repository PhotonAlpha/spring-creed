/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */

package com.ethan.system.service.permission;

import com.ethan.common.pojo.BaseXDO;
import com.ethan.common.utils.collection.CollUtils;
import com.ethan.security.websecurity.entity.CreedAuthorities;
import com.ethan.security.websecurity.entity.CreedUser;
import com.ethan.security.websecurity.repository.CreedAuthorityRepository;
import com.ethan.security.websecurity.repository.CreedUserRepository;
import com.ethan.system.dal.repository.permission.RoleMenuRepository;
import com.ethan.system.mq.producer.permission.PermissionProducer;
import com.google.common.annotations.VisibleForTesting;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.CollectionUtils;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.ethan.common.utils.collection.CollUtils.convertMap;
import static com.ethan.common.utils.collection.CollUtils.convertSet;

@Service
@Slf4j
public class CreedUserService {
    @Resource
    private CreedUserRepository creedUserRepository;
    @Resource
    private CreedAuthorityRepository authorityRepository;
    @Resource
    private PermissionProducer permissionProducer;
    @Resource
    private RoleMenuRepository roleMenuRepository;

    @VisibleForTesting
    @Transactional
    public Pair<ZonedDateTime, Map<String, Set<String>>> initUserRoleLocalCache(ZonedDateTime userRoleMaxUpdateTime) {
        // 获取用户与角色的关联列表，如果有更新
        List<CreedUser> users = loadUserRoleIfUpdate(userRoleMaxUpdateTime);
        if (CollectionUtils.isEmpty(users)) {
            return null;
        }
        // 初始化 userRoleCache 缓存
        Map<String, Set<String>> userRoleMapping = convertMap(users, CreedUser::getId, a -> a.getAuthorities().stream().map(CreedAuthorities::getId).collect(Collectors.toSet()));
        ZonedDateTime maxValue = ZonedDateTime.ofInstant(CollUtils.getMaxValue(users, BaseXDO::getUpdateTime), ZoneId.systemDefault());
        log.info("[initUserRoleLocalCache][初始化用户与角色的关联数量为 {}]", users.size());
        return Pair.of(maxValue, userRoleMapping);
    }

    /**
     * 如果用户与角色的关联发生变化，从数据库中获取最新的全量用户与角色的关联。
     * 如果未发生变化，则返回空
     *
     * @param maxUpdateTime 当前角色与菜单的关联的最大更新时间
     * @return 角色与菜单的关联列表
     */
    private List<CreedUser> loadUserRoleIfUpdate(ZonedDateTime maxUpdateTime) {
        // 第一步，判断是否要更新。
        if (maxUpdateTime == null) { // 如果更新时间为空，说明 DB 一定有新数据
            log.info("[loadUserRoleIfUpdate][首次加载全量用户与角色的关联]");
        } else { // 判断数据库中是否有更新的用户与角色的关联
            if (creedUserRepository.countByUpdateTimeGreaterThan(maxUpdateTime) == 0) {
                return null;
            }
            log.info("[loadUserRoleIfUpdate][增量加载全量用户与角色的关联]");
        }
        // 第二步，如果有更新，则从数据库加载所有用户与角色的关联
        return creedUserRepository.findAll();
    }


    @Transactional
    public Set<String> getUserRoleIdListByUserId(String userId) {
        return convertSet(creedUserRepository.findById(userId).map(CreedUser::getAuthorities).orElse(Collections.emptySet()),
                CreedAuthorities::getId);
    }

    @Transactional
    public Set<String> getUserRoleIdListByRoleIds(Collection<String> roleIds) {
        return convertSet(creedUserRepository.findByAuthoritiesIdIn(roleIds),
                CreedUser::getId);
    }


    @Transactional(rollbackFor = Exception.class)
    public void assignUserRole(String userId, Set<String> roleIds) {
        // 获得角色拥有角色编号
        // Set<String> existingRoles = creedUserRepository.findById(userId).map(CreedUser::getAuthorities).orElse(Collections.emptySet())
        //         .stream().map(CreedAuthorities::getId).collect(Collectors.toSet());
        // Map<String, CreedAuthorities> dbConsumers = convertMap(authorityRepository.findAllById(roleIds), CreedAuthorities::getId);
        Set<CreedAuthorities> creedAuthorities = new HashSet<>(authorityRepository.findAllById(roleIds));
        creedUserRepository.findById(userId).ifPresent(u -> {
            u.setAuthorities(creedAuthorities);
            creedUserRepository.save(u);
        });


/*         // 计算新增和删除的角色编号
        // Collection<String> createRoleIds = CollUtils.subtract(roleIds, existingRoles);
        // Collection<String> deleteRoleIds = CollUtils.subtract(existingRoles, roleIds);
        // 执行新增和删除。对于已经授权的角色，不用做任何处理
        if (!CollectionUtils.isEmpty(createRoleIds)) {
            consumerAuthorityRepository.saveAll(CollUtils.convertList(createRoleIds, roleId -> {
                if (dbConsumers.containsKey(roleId)) {
                    CreedConsumerAuthorities entity = new CreedConsumerAuthorities();
                    entity.setConsumer(dbAuthorities.get(roleId).getConsumer());
                    entity.setAuthorities(dbConsumers.get(roleId));
                    return entity;
                } else {
                    return null;
                }
            }));
        }
        if (!CollectionUtils.isEmpty(deleteRoleIds)) {
            consumerAuthorityRepository.deleteByConsumerIdAndAuthoritiesIdIn(userId, deleteRoleIds);
        } */
        // 发送刷新消息. 注意，需要事务提交后，在进行发送刷新消息。不然 db 还未提交，结果缓存先刷新了
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {

            @Override
            public void afterCommit() {
                permissionProducer.sendUserRoleRefreshMessage();
            }

        });
    }

    @Transactional(rollbackFor = Exception.class)
    public void processRoleDeleted(String roleId) {
        // 标记删除 UserRole
        // consumerAuthorityRepository.deleteByAuthoritiesId(roleId);
        authorityRepository.deleteById(roleId);
        // 标记删除 RoleMenu
        roleMenuRepository.deleteByRoleId(roleId);
        // 发送刷新消息. 注意，需要事务提交后，在进行发送刷新消息。不然 db 还未提交，结果缓存先刷新了
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {

            @Override
            public void afterCommit() {
                permissionProducer.sendRoleMenuRefreshMessage();
                permissionProducer.sendUserRoleRefreshMessage();
            }

        });
    }

    @Transactional(rollbackFor = Exception.class)
    public void processUserDeleted(String userId) {
        creedUserRepository.deleteById(userId);
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {

            @Override
            public void afterCommit() {
                permissionProducer.sendUserRoleRefreshMessage();
            }

        });
    }
}
