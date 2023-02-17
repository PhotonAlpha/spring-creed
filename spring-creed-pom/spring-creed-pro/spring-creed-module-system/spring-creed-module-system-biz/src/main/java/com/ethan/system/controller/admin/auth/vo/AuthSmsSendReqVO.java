package com.ethan.system.controller.admin.auth.vo;

import com.ethan.common.validation.InEnum;
import com.ethan.common.validation.Mobile;
import com.ethan.system.api.constant.sms.SmsSceneEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Tag(name = "管理后台 - 发送手机验证码 Request VO")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthSmsSendReqVO {

    @Schema(name = "手机号", requiredMode = Schema.RequiredMode.REQUIRED, example = "ethan")
    @NotEmpty(message = "手机号不能为空")
    @Mobile
    private String mobile;

    @Schema(name = "短信场景", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "发送场景不能为空")
    @InEnum(SmsSceneEnum.class)
    private Integer scene;

}
