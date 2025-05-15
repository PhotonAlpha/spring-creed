package com.ethan.system.controller.admin.auth.vo;

import com.ethan.common.validation.Mobile;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Tag(name = "管理后台 - 短信验证码的登录 Request VO")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthSmsLoginReqVO {

    @Schema(name = "手机号", requiredMode = Schema.RequiredMode.REQUIRED, example = "ethan")
    @NotEmpty(message = "手机号不能为空")
    @Mobile
    private String mobile;

    @Schema(name = "短信验证码", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotEmpty(message = "验证码不能为空")
    private String code;

}
