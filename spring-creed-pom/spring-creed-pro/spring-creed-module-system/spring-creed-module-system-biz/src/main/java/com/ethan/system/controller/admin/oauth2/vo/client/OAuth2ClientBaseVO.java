package com.ethan.system.controller.admin.oauth2.vo.client;

import cn.hutool.core.util.StrUtil;
import com.ethan.common.utils.json.JacksonUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
* OAuth2 客户端 Base VO，提供给添加、修改、详细的子 VO 使用
* 如果子 VO 存在差异的字段，请不要添加到这里，影响 Swagger 文档生成
*/
@Data
public class OAuth2ClientBaseVO {

    @Schema(name = "客户端编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "tudou")
    @NotNull(message = "客户端编号不能为空")
    private String clientId;

    @Schema(name = "客户端密钥", requiredMode = Schema.RequiredMode.REQUIRED, example = "fan")
    @NotNull(message = "客户端密钥不能为空")
    private String secret;

    @Schema(name = "应用名", requiredMode = Schema.RequiredMode.REQUIRED, example = "土豆")
    @NotNull(message = "应用名不能为空")
    private String name;

    @Schema(name = "应用图标", requiredMode = Schema.RequiredMode.REQUIRED, example = "https://www.iocoder.cn/xx.png")
    @NotNull(message = "应用图标不能为空")
    @URL(message = "应用图标的地址不正确")
    private String logo;

    @Schema(name = "应用描述", example = "我是一个应用")
    private String description;

    @Schema(name = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1", description = "参见 CommonStatusEnum 枚举")
    @NotNull(message = "状态不能为空")
    private Integer status;

    @Schema(name = "访问令牌的有效期", requiredMode = Schema.RequiredMode.REQUIRED, example = "8640")
    @NotNull(message = "访问令牌的有效期不能为空")
    private Integer accessTokenValiditySeconds;

    @Schema(name = "刷新令牌的有效期", requiredMode = Schema.RequiredMode.REQUIRED, example = "8640000")
    @NotNull(message = "刷新令牌的有效期不能为空")
    private Integer refreshTokenValiditySeconds;

    @Schema(name = "可重定向的 URI 地址", requiredMode = Schema.RequiredMode.REQUIRED, example = "https://www.iocoder.cn")
    @NotNull(message = "可重定向的 URI 地址不能为空")
    private List<@NotEmpty(message = "重定向的 URI 不能为空")
        @URL(message = "重定向的 URI 格式不正确") String> redirectUris;

    @Schema(name = "授权类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "password", description = "参见 OAuth2GrantTypeEnum 枚举")
    @NotNull(message = "授权类型不能为空")
    private List<String> authorizedGrantTypes;

    @Schema(name = "授权范围", example = "user_info")
    private List<String> scopes;

    @Schema(name = "自动通过的授权范围", example = "user_info")
    private List<String> autoApproveScopes;

    @Schema(name = "权限", example = "system:user:query")
    private List<String> authorities;

    @Schema(name = "资源", example = "1024")
    private List<String> resourceIds;

    @Schema(name = "附加信息", example = "{yunai: true}")
    private String additionalInformation;

    @AssertTrue(message = "附加信息必须是 JSON 格式")
    public boolean isAdditionalInformationJson() {
        return StrUtil.isEmpty(additionalInformation) || JacksonUtils.isJson(additionalInformation);
    }

}
