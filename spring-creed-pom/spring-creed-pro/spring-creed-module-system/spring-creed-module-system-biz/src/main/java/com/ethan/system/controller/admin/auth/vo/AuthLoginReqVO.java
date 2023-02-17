package com.ethan.system.controller.admin.auth.vo;

import com.ethan.common.validation.InEnum;
import com.ethan.system.api.constant.social.SocialTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Length;

@Tag(name = "管理后台 - 账号密码登录 Request VO", description = "如果登录并绑定社交用户，需要传递 social 开头的参数")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthLoginReqVO {

    @Schema(name = "账号", requiredMode = Schema.RequiredMode.REQUIRED, example = "ethan")
    @NotEmpty(message = "登录账号不能为空")
    @Length(min = 4, max = 16, message = "账号长度为 4-16 位")
    @Pattern(regexp = "^[A-Za-z0-9]+$", message = "账号格式为数字以及字母")
    private String username;

    @Schema(name = "密码", requiredMode = Schema.RequiredMode.REQUIRED, example = "buzhidao")
    @NotEmpty(message = "密码不能为空")
    @Length(min = 4, max = 16, message = "密码长度为 4-16 位")
    private String password;

    // ========== 图片验证码相关 ==========

    @Schema(name = "验证码", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024", description = "验证码开启时，需要传递")
    @NotEmpty(message = "验证码不能为空", groups = CodeEnableGroup.class)
    private String code;

    @Schema(name = "验证码的唯一标识", requiredMode = Schema.RequiredMode.REQUIRED, example = "9b2ffbc1-7425-4155-9894-9d5c08541d62", description = "验证码开启时，需要传递")
    @NotEmpty(message = "唯一标识不能为空", groups = CodeEnableGroup.class)
    private String uuid;

    // ========== 绑定社交登录时，需要传递如下参数 ==========

    @Schema(name = "社交平台的类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "10", description = "参见 SysUserSocialTypeEnum 枚举值")
    @InEnum(SocialTypeEnum.class)
    private Integer socialType;

    @Schema(name = "授权码", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private String socialCode;

    @Schema(name = "state", requiredMode = Schema.RequiredMode.REQUIRED, example = "9b2ffbc1-7425-4155-9894-9d5c08541d62")
    private String socialState;

    /**
     * 开启验证码的 Group
     */
    public interface CodeEnableGroup {}

    @AssertTrue(message = "授权码不能为空")
    public boolean isSocialCodeValid() {
        return socialType == null || StringUtils.isNotEmpty(socialCode);
    }

    @AssertTrue(message = "授权 state 不能为空")
    public boolean isSocialState() {
        return socialType == null || StringUtils.isNotEmpty(socialState);
    }

}
