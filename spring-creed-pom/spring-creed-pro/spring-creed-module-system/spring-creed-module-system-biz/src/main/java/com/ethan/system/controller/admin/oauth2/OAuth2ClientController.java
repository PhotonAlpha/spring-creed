package com.ethan.system.controller.admin.oauth2;

import com.ethan.common.common.R;
import com.ethan.common.pojo.PageResult;
import com.ethan.system.controller.admin.dict.vo.data.DictDataBaseVO;
import com.ethan.system.controller.admin.oauth2.vo.client.OAuth2ClientPageReqVO;
import com.ethan.system.controller.admin.oauth2.vo.client.OAuth2ClientRespVO;
import com.ethan.system.controller.admin.oauth2.vo.client.OAuth2ClientSaveReqVO;
import com.ethan.system.convert.auth.OAuth2ClientConvert;
import com.ethan.system.service.oauth2.OAuth2ClientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.core.AuthenticationMethod;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

import static com.ethan.common.common.R.success;

@Tag(name = "管理后台 - OAuth2 客户端")
@RestController
@RequestMapping("/system/oauth2-client")
@Validated
public class OAuth2ClientController {
    @Resource
    private OAuth2ClientService oAuth2ClientService;
    @PostMapping("/create")
    @Operation(summary = "创建 OAuth2 客户端")
    @PreAuthorize("@ss.hasPermission('system:oauth2-client:create')")
    public R<String> createOAuth2Client(@Valid @RequestBody OAuth2ClientSaveReqVO createReqVO) {
        return success(oAuth2ClientService.createOAuth2Client(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新 OAuth2 客户端")
    @PreAuthorize("@ss.hasPermission('system:oauth2-client:update')")
    public R<Boolean> updateOAuth2Client(@Valid @RequestBody OAuth2ClientSaveReqVO updateReqVO) {
        oAuth2ClientService.updateOAuth2Client(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除 OAuth2 客户端")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('system:oauth2-client:delete')")
    public R<Boolean> deleteOAuth2Client(@RequestParam("id") String id) {
        oAuth2ClientService.deleteOAuth2Client(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得 OAuth2 客户端")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('system:oauth2-client:query')")
    public R<OAuth2ClientRespVO> getOAuth2Client(@RequestParam("id") String id) {
        var client = oAuth2ClientService.getOAuth2Client(id);
        return success(OAuth2ClientConvert.INSTANCE.convert(client));
    }

    @GetMapping("/page")
    @Operation(summary = "获得 OAuth2 客户端分页")
    @PreAuthorize("@ss.hasPermission('system:oauth2-client:query')")
    public R<PageResult<OAuth2ClientRespVO>> getOAuth2ClientPage(@Valid OAuth2ClientPageReqVO pageVO) {
        var pageResult = oAuth2ClientService.getOAuth2ClientPage(pageVO);
        return success(OAuth2ClientConvert.INSTANCE.convertPage(pageResult));
    }

    @GetMapping("/list-authentication-methods")
    @Operation(summary = "获得 OAuth2 方法")
    public R<List<DictDataBaseVO>> listAuthenticationMethods() {
        var result = new ArrayList<DictDataBaseVO>();
        result.add(new DictDataBaseVO("CLIENT_SECRET_BASIC", ClientAuthenticationMethod.CLIENT_SECRET_BASIC.getValue()));
        result.add(new DictDataBaseVO("CLIENT_SECRET_POST", ClientAuthenticationMethod.CLIENT_SECRET_POST.getValue()));
        result.add(new DictDataBaseVO("CLIENT_SECRET_JWT", ClientAuthenticationMethod.CLIENT_SECRET_JWT.getValue()));
        result.add(new DictDataBaseVO("PRIVATE_KEY_JWT", ClientAuthenticationMethod.PRIVATE_KEY_JWT.getValue()));
        result.add(new DictDataBaseVO("NONE", ClientAuthenticationMethod.NONE.getValue()));
        result.add(new DictDataBaseVO("TLS_CLIENT_AUTH", ClientAuthenticationMethod.TLS_CLIENT_AUTH.getValue()));
        result.add(new DictDataBaseVO("SELF_SIGNED_TLS_CLIENT_AUTH", ClientAuthenticationMethod.SELF_SIGNED_TLS_CLIENT_AUTH.getValue()));
        return success(result);
    }
    @GetMapping("/list-grant-types")
    @Operation(summary = "获得 OAuth2 类型")
    public R<List<DictDataBaseVO>> listGrantTypes() {
        var result = new ArrayList<DictDataBaseVO>();
        result.add(new DictDataBaseVO("AUTHORIZATION_CODE", AuthorizationGrantType.AUTHORIZATION_CODE.getValue()));
        result.add(new DictDataBaseVO("REFRESH_TOKEN", AuthorizationGrantType.REFRESH_TOKEN.getValue()));
        result.add(new DictDataBaseVO("CLIENT_CREDENTIALS", AuthorizationGrantType.CLIENT_CREDENTIALS.getValue()));
        result.add(new DictDataBaseVO("JWT_BEARER", AuthorizationGrantType.JWT_BEARER.getValue()));
        result.add(new DictDataBaseVO("DEVICE_CODE", AuthorizationGrantType.DEVICE_CODE.getValue()));
        result.add(new DictDataBaseVO("TOKEN_EXCHANGE", AuthorizationGrantType.TOKEN_EXCHANGE.getValue()));
        return success(result);
    }

}
