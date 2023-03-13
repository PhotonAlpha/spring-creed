package com.ethan.system.controller.admin.sms.vo.template;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;

@Schema(name="管理后台 - 短信模板的发送 Request VO")
@Data
public class SmsTemplateSendReqVO {

    @Schema(name = "手机号", requiredMode = Schema.RequiredMode.REQUIRED, example = "15601691300")
    @NotNull(message = "手机号不能为空")
    private String mobile;

    @Schema(name = "模板编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "test_01")
    @NotNull(message = "模板编码不能为空")
    private String templateCode;

    @Schema(name = "模板参数")
    private Map<String, Object> templateParams;

}
