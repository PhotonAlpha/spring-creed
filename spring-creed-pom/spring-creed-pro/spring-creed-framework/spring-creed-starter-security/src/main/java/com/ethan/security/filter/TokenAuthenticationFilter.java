package com.ethan.security.filter;

import com.ethan.common.common.R;
import com.ethan.common.exception.ServiceException;
import com.ethan.common.utils.servlet.ServletUtils;
import com.ethan.security.config.SecurityProperties;
import com.ethan.security.oauth2.OAuth2TokenApi;
import com.ethan.security.oauth2.dto.OAuth2AccessTokenCheckRespDTO;
import com.ethan.security.userdetails.LoginUser;
import com.ethan.security.utils.SecurityFrameworkUtils;
import com.ethan.security.utils.WebFrameworkUtils;
import com.nimbusds.oauth2.sdk.token.BearerAccessToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class TokenAuthenticationFilter extends OncePerRequestFilter {
    private final OAuth2TokenApi oauth2TokenApi;
    private final SecurityProperties securityProperties;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        // String token = SecurityFrameworkUtils.obtainAuthorization(request, HttpHeaders.AUTHORIZATION);
        // if (StringUtils.hasText(token)) {
        //     Integer userType = WebFrameworkUtils.getLoginUserType(request);
        //     try {
        //         // 1.1 基于 token 构建登录用户
        //         LoginUser loginUser = buildLoginUserByToken(token, userType);
        //         // 1.2 模拟 Login 功能，方便日常开发调试
        //         if (loginUser == null) {
        //             loginUser = mockLoginUser(request, token, userType);
        //         }
        //
        //         // 2. 设置当前用户
        //         if (loginUser != null) {
        //             SecurityFrameworkUtils.setLoginUser(loginUser, request);
        //         }
        //     } catch (Throwable ex) {
        //         // R<?> result = globalExceptionHandler.allExceptionHandler(request, ex);
        //         ServletUtils.writeJSON(response, ex.getMessage());
        //         return;
        //     }
        // }

        // 继续过滤链
        chain.doFilter(request, response);
    }

    // private LoginUser buildLoginUserByToken(String token, Integer userType) {
    //     try {
    //         OAuth2AccessTokenCheckRespDTO accessToken = oauth2TokenApi.checkAccessToken(token);
    //         if (accessToken == null) {
    //             return null;
    //         }
    //         // 用户类型不匹配，无权限
    //         if (ObjectUtils.nullSafeEquals(accessToken.getUserType(), userType)) {
    //             throw new AccessDeniedException("错误的用户类型");
    //         }
    //         // 构建登录用户
    //         return new LoginUser().setId(accessToken.getUserId()).setUserType(accessToken.getUserType())
    //                 .setTenantId(accessToken.getTenantId()).setScopes(accessToken.getScopes());
    //     } catch (ServiceException serviceException) {
    //         // 校验 Token 不通过时，考虑到一些接口是无需登录的，所以直接返回 null 即可
    //         return null;
    //     }
    // }
    //
    // /**
    //  * 模拟登录用户，方便日常开发调试
    //  *
    //  * 注意，在线上环境下，一定要关闭该功能！！！
    //  *
    //  * @param request 请求
    //  * @param token 模拟的 token，格式为 {@link SecurityProperties#getMockSecret()} + 用户编号
    //  * @param userType 用户类型
    //  * @return 模拟的 LoginUser
    //  */
    // private LoginUser mockLoginUser(HttpServletRequest request, String token, Integer userType) {
    //     if (!securityProperties.getMockEnable()) {
    //         return null;
    //     }
    //     // 必须以 mockSecret 开头
    //     if (!token.startsWith(securityProperties.getMockSecret())) {
    //         return null;
    //     }
    //     // 构建模拟用户
    //     Long userId = Long.valueOf(token.substring(securityProperties.getMockSecret().length()));
    //     return new LoginUser().setId(userId).setUserType(userType)
    //             .setTenantId(WebFrameworkUtils.getTenantId(request));
    // }
}
