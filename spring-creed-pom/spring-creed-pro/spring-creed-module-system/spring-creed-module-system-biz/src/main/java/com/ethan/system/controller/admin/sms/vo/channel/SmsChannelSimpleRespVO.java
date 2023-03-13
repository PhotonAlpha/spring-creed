package com.ethan.system.controller.admin.sms.vo.channel;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(name="管理后台 - 短信渠道精简 Response VO")
@Data
public class SmsChannelSimpleRespVO {

    @Schema(name = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotNull(message = "编号不能为空")
    private Long id;

    @Schema(name = "短信签名", requiredMode = Schema.RequiredMode.REQUIRED, example = "TEST")
    @NotNull(message = "短信签名不能为空")
    private String signature;

    @Schema(name = "渠道编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "YUN_PIAN", description = "参见 SmsChannelEnum 枚举类")
    private String code;

}
