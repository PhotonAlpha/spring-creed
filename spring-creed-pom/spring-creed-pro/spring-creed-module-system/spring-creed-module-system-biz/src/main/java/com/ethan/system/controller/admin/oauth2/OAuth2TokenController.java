package com.ethan.system.controller.admin.oauth2;

import com.ethan.common.common.R;
import com.ethan.common.pojo.PageResult;
import com.ethan.security.oauth2.entity.CreedOAuth2AuthorizedClient;
import com.ethan.system.constant.logger.LoginLogTypeEnum;
import com.ethan.system.controller.admin.oauth2.vo.token.OAuth2AccessTokenPageReqVO;
import com.ethan.system.controller.admin.oauth2.vo.token.OAuth2AccessTokenRespVO;
import com.ethan.system.convert.auth.OAuth2TokenConvert;
import com.ethan.system.service.auth.AdminAuthService;
import com.ethan.system.service.oauth2.OAuth2TokenService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.ethan.common.common.R.success;


@Tag(name = "管理后台 - OAuth2.0 令牌")
@RestController
@RequestMapping("/system/oauth2-token")
public class OAuth2TokenController {

    @Resource
    private OAuth2TokenService oauth2TokenService;
    @Resource
    private AdminAuthService authService;

    @GetMapping("/page")
    @Schema(name = "获得访问令牌分页", description = "只返回有效期内的")
    @PreAuthorize("@ss.hasPermission('system:oauth2-token:page')")
    public R<PageResult<OAuth2AccessTokenRespVO>> getAccessTokenPage(@Valid OAuth2AccessTokenPageReqVO reqVO) {
        PageResult<CreedOAuth2AuthorizedClient> pageResult = oauth2TokenService.getAccessTokenPage(reqVO);
        return success(OAuth2TokenConvert.INSTANCE.convert(pageResult));
    }

    @DeleteMapping("/delete")
    @Schema(name = "删除访问令牌")
    @Parameter(name = "accessToken", description = "访问令牌", required = true, schema = @Schema(implementation = String.class), example = "tudou")
    @PreAuthorize("@ss.hasPermission('system:oauth2-token:delete')")
    public R<Boolean> deleteAccessToken(@RequestParam("accessToken") String accessToken) {
        authService.logout(accessToken, LoginLogTypeEnum.LOGOUT_DELETE.getType());
        return success(true);
    }

}
