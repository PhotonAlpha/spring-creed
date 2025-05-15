package com.ethan.system.controller.admin.sms.vo.template;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Date;
import java.util.List;

@Schema(name="管理后台 - 短信模板 Response VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class SmsTemplateRespVO extends SmsTemplateBaseVO {

    @Schema(name = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    @Schema(name = "短信渠道编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "ALIYUN")
    private String channelCode;

    @Schema(name = "参数数组", example = "name,code")
    private List<String> params;

    @Schema(name = "创建时间", required = true)
    private Date createTime;

}
