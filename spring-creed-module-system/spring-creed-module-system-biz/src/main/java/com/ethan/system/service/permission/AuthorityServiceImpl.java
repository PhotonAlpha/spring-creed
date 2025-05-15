package com.ethan.system.service.permission;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.ethan.common.constant.CommonStatusEnum;
import com.ethan.common.pojo.BaseVersioningXDO;
import com.ethan.common.pojo.PageResult;
import com.ethan.common.utils.collection.CollUtils;
import com.ethan.common.utils.json.JacksonUtils;
import com.ethan.security.websecurity.constant.AuthorityTypeEnum;
import com.ethan.system.controller.admin.permission.vo.authority.AuthorityPageReqVO;
import com.ethan.system.controller.admin.permission.vo.authority.AuthoritySaveReqVO;
import com.ethan.system.convert.permission.SystemAuthorityConvert;
import com.ethan.system.dal.entity.permission.SystemAuthorities;
import com.ethan.system.dal.redis.RedisKeyConstants;
import com.ethan.system.dal.repository.permission.SystemAuthoritiesRepository;
import com.google.common.annotations.VisibleForTesting;
import com.mzt.logapi.context.LogRecordContext;
import com.mzt.logapi.service.impl.DiffParseFunction;
import com.mzt.logapi.starter.annotation.LogRecord;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.ethan.common.exception.util.ServiceExceptionUtil.exception;
import static com.ethan.system.api.constant.LogRecordConstants.SYSTEM_AUTHORITY_CREATE_SUB_TYPE;
import static com.ethan.system.api.constant.LogRecordConstants.SYSTEM_AUTHORITY_CREATE_SUCCESS;
import static com.ethan.system.api.constant.LogRecordConstants.SYSTEM_AUTHORITY_TYPE;
import static com.ethan.system.constant.ErrorCodeConstants.AUTHORITY_CAN_NOT_UPDATE_SYSTEM_TYPE_AUTHORITY;
import static com.ethan.system.constant.ErrorCodeConstants.AUTHORITY_IS_DISABLE;
import static com.ethan.system.constant.ErrorCodeConstants.AUTHORITY_NAME_CODE_DUPLICATE;
import static com.ethan.system.constant.ErrorCodeConstants.AUTHORITY_NOT_EXISTS;

/**
 * @author EthanCao ethan.caoq@foxmail.com
 * @description spring-creed-pro
 * @date 10/1/24
 *
 * TODO 添加 权限组 适配在复杂情况下用户的权限分配
 * 注意：此处的权限为数据权限，非菜单权限
 */
@Service
@Slf4j
public class AuthorityServiceImpl implements AuthorityService {
    @Resource
    private SystemAuthoritiesRepository authoritiesRepository;


    @Resource
    @Lazy // 注入自己，所以延迟加载
    private AuthorityService self;
    @Resource
    private PermissionService permissionService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    @LogRecord(type = SYSTEM_AUTHORITY_TYPE, subType = SYSTEM_AUTHORITY_CREATE_SUB_TYPE, bizNo = "{{#_ret}}",
            success = SYSTEM_AUTHORITY_CREATE_SUCCESS)
    // @CacheEvict(value = RedisKeyConstants.MENU_ROLE_ID_LIST,
    //         allEntries = true) // allEntries 清空所有缓存，主要一次更新涉及到的 menuIds 较多，反倒批量会更快
    public Long createAuthority(AuthoritySaveReqVO reqVO, Integer type) {
        log.info("hit createRole:{} type:{}", JacksonUtils.toJsonString(reqVO), type);
        // 1. 校验角色
        validateAuthorityDuplicate(reqVO.getAuthority(), null);

        // 2. 插入到数据库
        var authority = SystemAuthorityConvert.INSTANCE.convert(reqVO);
        authority.setType(AuthorityTypeEnum.findByType(type));
        authoritiesRepository.save(authority);

        // 3. 记录操作日志上下文
        LogRecordContext.putVariable("authority", authority);

        return authority.getId();
    }

    /**
     * 校验角色的唯一字段是否重复
     *
     * 1. 是否存在相同名字的角色
     * 2. 是否存在相同编码的角色
     *
     * @param authority 角色名字
     * @param id 角色编号
     */
    @VisibleForTesting
    void validateAuthorityDuplicate(String authority, Long id) {

        // 1. 非空判断
        if (!StringUtils.hasText(authority)) {
            return;
        }
        // 2. 该 authority 名字被其它角色所使用
        if (Objects.isNull(id)) {
            if (authoritiesRepository.existsByAuthority(authority)) {
                throw exception(AUTHORITY_NAME_CODE_DUPLICATE, authority);
            }
        } else {
            authoritiesRepository.findByAuthority(authority)
                    .map(SystemAuthorities::getId)
                    .filter(id::equals)
                    .orElseThrow(() -> exception(AUTHORITY_NAME_CODE_DUPLICATE, authority));
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SystemAuthorities getOrCreateAuthority(String authority) {
        Optional<SystemAuthorities> authoritiesOptional = authoritiesRepository.findByAuthority(authority);
        if (authoritiesOptional.isEmpty()) {
            SystemAuthorities authorities = new SystemAuthorities();
            authorities.setAuthority(authority);
            authoritiesRepository.save(authorities);
            return authorities;
        } else {
            return authoritiesOptional.get();
        }
    }

    @Override
    public void updateAuthority(AuthoritySaveReqVO reqVO) {
        log.info("hit updateAuthority:{}", JacksonUtils.toJsonString(reqVO));
        // 1.1 校验是否可以更新
        var authority = validateAuthorityForUpdate(reqVO.getId());
        // 1.2 校验角色的唯一字段是否重复
        validateAuthorityDuplicate(reqVO.getAuthority(), reqVO.getId());

        // 2. 更新到数据库
        SystemAuthorityConvert.INSTANCE.update(reqVO, authority);
        authoritiesRepository.save(authority);

        // 3. 记录操作日志上下文
        LogRecordContext.putVariable("authority", authority);
    }

    /**
     * 校验角色是否可以被更新
     *
     * @param id 角色编号
     */
    @VisibleForTesting
    SystemAuthorities validateAuthorityForUpdate(Long id) {
        Optional<SystemAuthorities> authoritiesOptional = authoritiesRepository.findById(id);
        if (authoritiesOptional.isEmpty()) {
            throw exception(AUTHORITY_NOT_EXISTS);
        }
        // 内置角色，不允许修改/删除
        return authoritiesOptional.filter(this::authorityTypePredicate)
                .orElseThrow(() -> exception(AUTHORITY_CAN_NOT_UPDATE_SYSTEM_TYPE_AUTHORITY));
    }

    private boolean authorityTypePredicate(SystemAuthorities authorities) {
        return Optional.ofNullable(authorities).map(SystemAuthorities::getType)
                .filter(AuthorityTypeEnum.SYSTEM::equals).isPresent();
    }

    @Override
    public void deleteAuthority(Long id) {
        log.info("hit deleteAuthority:{}", id);
        // 1. 校验是否可以更新
        var authority = validateAuthorityForUpdate(id);

        // 2.1 标记删除
        authoritiesRepository.deleteById(id);
        // 2.2 删除相关数据
        permissionService.processAuthorityDeleted(id);

        // 3. 记录操作日志上下文
        LogRecordContext.putVariable(DiffParseFunction.OLD_OBJECT, SystemAuthorityConvert.INSTANCE.convert0(authority));
        LogRecordContext.putVariable("authority", authority);
    }


    @Override
    public SystemAuthorities getAuthority(Long id) {
        log.info("hit getAuthority:{}", id);
        return authoritiesRepository.findById(id).orElseThrow(() -> exception(AUTHORITY_NOT_EXISTS));
    }

    @Override
    @Cacheable(value = RedisKeyConstants.AUTHORITY, key = "#id",
            unless = "#result == null")
    public SystemAuthorities getAuthorityFromCache(Long id) {
        log.info("hit getAuthorityFromCache:{}", id);
        return getAuthority(id);
    }

    @Override
    public List<SystemAuthorities> getAuthorityList(Collection<Long> ids) {
        log.info("hit getAuthorityList:{}", ids);
        return authoritiesRepository.findAllById(ids);
    }

    @Override
    public List<SystemAuthorities> getAuthorityListFromCache(Collection<Long> ids) {
        log.info("hit getAuthorityListFromCache:{}", ids);
        if (CollectionUtil.isEmpty(ids)) {
            return Collections.emptyList();
        }
        // 这里采用 for 循环从缓存中获取，主要考虑 Spring CacheManager 无法批量操作的问题
        return CollUtils.convertList(ids, self::getAuthorityFromCache);
    }

    /**
     * 如果该权限未分配任何 菜单或者数据权限 ，显示警告 TODO
     * @param statuses 筛选的状态
     * @return
     */
    @Override
    public List<SystemAuthorities> getAuthorityListByStatus(Collection<Integer> statuses) {
        log.info("hit getRoleListByStatus:{}", statuses);
        List<CommonStatusEnum> statusEnumList = Optional.ofNullable(statuses).orElse(Collections.emptyList())
                .stream().map(CommonStatusEnum::convert).toList();
        return authoritiesRepository.findByEnabledIn(statusEnumList);
    }

    @Override
    public List<SystemAuthorities> getAuthorityList() {
        log.info("hit getAuthorityList");
        return authoritiesRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
    }

    @Override
    public PageResult<SystemAuthorities> getAuthorityPage(AuthorityPageReqVO reqVO) {
        log.info("hit getRolePage:{}", JacksonUtils.toJsonString(reqVO));
        Page<SystemAuthorities> page = authoritiesRepository.findByPage(reqVO);
        return new PageResult<>(page.getContent(), page.getTotalElements());
    }


    @Override
    public void validateAuthorityList(Collection<Long> ids) {
        log.info("hit validateAuthorityList:{}", ids);
        if (CollUtil.isEmpty(ids)) {
            return;
        }
        // 获得角色信息
        var authorities = getAuthorityList(ids);
        // 校验
        authorities.forEach(aut -> {
            CommonStatusEnum status = Optional.ofNullable(aut)
                    .map(BaseVersioningXDO::getEnabled)
                    .orElseThrow(() -> exception(AUTHORITY_NOT_EXISTS));
            if (!CommonStatusEnum.ENABLE.equals(status)) {
                throw exception(AUTHORITY_IS_DISABLE, aut.getAuthority());
            }
        });
    }
}
