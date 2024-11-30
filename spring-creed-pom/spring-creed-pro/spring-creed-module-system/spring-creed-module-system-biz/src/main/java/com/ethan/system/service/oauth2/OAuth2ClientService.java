package com.ethan.system.service.oauth2;

import com.ethan.common.pojo.PageResult;
import com.ethan.system.controller.admin.oauth2.vo.client.OAuth2ClientCreateReqVO;
import com.ethan.system.controller.admin.oauth2.vo.client.OAuth2ClientPageReqVO;
import com.ethan.system.controller.admin.oauth2.vo.client.OAuth2ClientUpdateReqVO;
import com.ethan.system.dal.entity.oauth2.CreedOAuth2RegisteredClient;
import jakarta.validation.Valid;

import java.util.Collection;

/**
 * OAuth2.0 Client Service 接口
 *
 * 从功能上，和 JdbcClientDetailsService 的功能，提供客户端的操作
 *
 * 
 */
public interface OAuth2ClientService {

    /**
     * 初始化 OAuth2Client 的本地缓存
     */
    void initLocalCache();

    /**
     * 创建 OAuth2 客户端
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    String createOAuth2Client(@Valid OAuth2ClientCreateReqVO createReqVO);

    /**
     * 更新 OAuth2 客户端
     *
     * @param updateReqVO 更新信息
     */
    void updateOAuth2Client(@Valid OAuth2ClientUpdateReqVO updateReqVO);

    /**
     * 删除 OAuth2 客户端
     *
     * @param id 编号
     */
    void deleteOAuth2Client(String id);

    /**
     * 获得 OAuth2 客户端
     *
     * @param id 编号
     * @return OAuth2 客户端
     */
    CreedOAuth2RegisteredClient getOAuth2Client(String id);

    /**
     * 获得 OAuth2 客户端分页
     *
     * @param pageReqVO 分页查询
     * @return OAuth2 客户端分页
     */
    PageResult<CreedOAuth2RegisteredClient> getOAuth2ClientPage(OAuth2ClientPageReqVO pageReqVO);

    /**
     * 从缓存中，校验客户端是否合法
     *
     * @return 客户端
     */
    default CreedOAuth2RegisteredClient validOAuthClientFromCache(String clientId) {
        return validOAuthClientFromCache(clientId, null, null, null, null);
    }

    /**
     * 从缓存中，校验客户端是否合法
     *
     * 非空时，进行校验
     *
     * @param clientId 客户端编号
     * @param clientSecret 客户端密钥
     * @param authorizedGrantType 授权方式
     * @param scopes 授权范围
     * @param redirectUri 重定向地址
     * @return 客户端
     */
    CreedOAuth2RegisteredClient validOAuthClientFromCache(String clientId, String clientSecret,
                                             String authorizedGrantType, Collection<String> scopes, String redirectUri);

}
