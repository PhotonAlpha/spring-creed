package com.ethan.system.controller.admin.sms.vo.channel;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Date;

@Schema(name="管理后台 - 短信渠道 Response VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class SmsChannelRespVO extends SmsChannelBaseVO {

    @Schema(name = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    @Schema(name = "渠道编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "YUN_PIAN", description = "参见 SmsChannelEnum 枚举类")
    private String code;

    @Schema(name = "创建时间", required = true)
    private Date createTime;

}
