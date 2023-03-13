package com.ethan.system.controller.admin.captcha.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Schema(name = "管理后台 - 验证码图片 Response VO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class CaptchaImageRespVO {

    @Schema(name = "是否开启", requiredMode = Schema.RequiredMode.REQUIRED, example = "true", description = "如果为 false，则关闭验证码功能")
    private Boolean enable;

    @Schema(name = "uuid", example = "1b3b7d00-83a8-4638-9e37-d67011855968",
            description = "enable = true 时，非空！通过该 uuid 作为该验证码的标识")
    private String uuid;

    @Schema(name = "图片", description = "enable = true 时，非空！验证码的图片内容，使用 Base64 编码")
    private String img;

}
