package com.ethan.system.controller.admin.sms.vo.channel;

import com.ethan.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

import static com.ethan.common.utils.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(name="管理后台 - 短信渠道分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class SmsChannelPageReqVO extends PageParam {

    @Schema(name = "任务状态", example = "1")
    private Integer status;

    @Schema(name = "短信签名", example = "TEST", description = "模糊匹配")
    private String signature;

    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    @Schema(name = "创建时间")
    private Date[] createTime;

}
