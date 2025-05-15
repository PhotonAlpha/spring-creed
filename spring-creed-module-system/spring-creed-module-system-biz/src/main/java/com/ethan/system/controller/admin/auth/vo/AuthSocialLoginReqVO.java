package com.ethan.system.controller.admin.auth.vo;

import com.ethan.common.validation.InEnum;
import com.ethan.system.api.constant.social.SocialTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Tag(name = "管理后台 - 社交绑定登录 Request VO，使用 code 授权码 + 账号密码")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthSocialLoginReqVO {

    @Schema(name = "社交平台的类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "10", description = "参见 UserSocialTypeEnum 枚举值")
    @InEnum(SocialTypeEnum.class)
    @NotNull(message = "社交平台的类型不能为空")
    private Integer type;

    @Schema(name = "授权码", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotEmpty(message = "授权码不能为空")
    private String code;

    @Schema(name = "state", requiredMode = Schema.RequiredMode.REQUIRED, example = "9b2ffbc1-7425-4155-9894-9d5c08541d62")
    @NotEmpty(message = "state 不能为空")
    private String state;

}
