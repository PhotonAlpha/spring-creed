package com.ethan.system.service.permission;


import com.ethan.common.pojo.PageResult;
import com.ethan.system.controller.admin.permission.vo.authority.AuthorityPageReqVO;
import com.ethan.system.controller.admin.permission.vo.authority.AuthoritySaveReqVO;
import com.ethan.system.dal.entity.permission.SystemAuthorities;
import jakarta.validation.Valid;
import org.springframework.lang.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * 权限 Service 接口
 *
 * 
 */
public interface AuthorityService {
    /**
     * 创建权限
     *
     * @param reqVO 创建权限信息
     * @param type 权限类型
     * @return 权限编号
     */
    Long createAuthority(@Valid AuthoritySaveReqVO reqVO, Integer type);

    /**
     * 更新权限
     *
     * @param reqVO 更新权限信息
     */
    void updateAuthority(@Valid AuthoritySaveReqVO reqVO);

    /**
     * 删除权限
     *
     * @param id 权限编号
     */
    void deleteAuthority(Long id);

    SystemAuthorities getOrCreateAuthority(String authority);

    /**
     * 获得权限
     *
     * @param id 权限编号
     * @return 权限
     */
    SystemAuthorities getAuthority(Long id);

    /**
     * 获得权限，从缓存中
     *
     * @param id 权限编号
     * @return 权限
     */
    SystemAuthorities getAuthorityFromCache(Long id);

    /**
     * 获得权限列表
     *
     * @param ids 权限编号数组
     * @return 权限列表
     */
    List<SystemAuthorities> getAuthorityList(@Nullable Collection<Long> ids);

    /**
     * 获得权限数组，从缓存中
     *
     * @param ids 权限编号数组
     * @return 权限数组
     */
    List<SystemAuthorities> getAuthorityListFromCache(Collection<Long> ids);

    /**
     * 获得权限列表
     *
     * @param statuses 筛选的状态
     * @return 权限列表
     */
    List<SystemAuthorities> getAuthorityListByStatus(Collection<Integer> statuses);

    /**
     * 获得所有权限列表
     * @return 权限列表
     */
    List<SystemAuthorities> getAuthorityList();

    /**
     * 获得权限分页
     *
     * @param reqVO 权限分页查询
     * @return 权限分页结果
     */
    PageResult<SystemAuthorities> getAuthorityPage(AuthorityPageReqVO reqVO);


    /**
     * 校验权限们是否有效。如下情况，视为无效：
     * 1. 权限编号不存在
     * 2. 权限被禁用
     *
     * @param ids 权限编号数组
     */
    void validateAuthorityList(Collection<Long> ids);

}
