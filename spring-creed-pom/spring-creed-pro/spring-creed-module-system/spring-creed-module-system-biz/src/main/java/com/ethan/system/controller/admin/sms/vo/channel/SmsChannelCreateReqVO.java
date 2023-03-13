package com.ethan.system.controller.admin.sms.vo.channel;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Schema(name="管理后台 - 短信渠道创建 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class SmsChannelCreateReqVO extends SmsChannelBaseVO {

    @Schema(name = "渠道编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "YUN_PIAN", description = "参见 SmsChannelEnum 枚举类")
    @NotNull(message = "渠道编码不能为空")
    private String code;

}
