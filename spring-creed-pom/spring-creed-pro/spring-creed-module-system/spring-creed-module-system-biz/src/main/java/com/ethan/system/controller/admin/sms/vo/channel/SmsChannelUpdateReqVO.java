package com.ethan.system.controller.admin.sms.vo.channel;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Schema(name="管理后台 - 短信渠道更新 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class SmsChannelUpdateReqVO extends SmsChannelBaseVO {

    @Schema(name = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotNull(message = "编号不能为空")
    private Long id;

}
