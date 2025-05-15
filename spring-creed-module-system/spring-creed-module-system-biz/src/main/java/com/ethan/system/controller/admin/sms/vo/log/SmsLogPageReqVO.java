package com.ethan.system.controller.admin.sms.vo.log;

import com.ethan.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

import static com.ethan.common.utils.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(name="管理后台 - 短信日志分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class SmsLogPageReqVO extends PageParam {

    @Schema(name = "短信渠道编号", example = "10")
    private Long channelId;

    @Schema(name = "模板编号", example = "20")
    private Long templateId;

    @Schema(name = "手机号", example = "15601691300")
    private String mobile;

    @Schema(name = "发送状态", example = "1",  description = "参见 SmsSendStatusEnum 枚举类")
    private Integer sendStatus;

    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    @Schema(name = "发送时间")
    private Date[] sendTime;

    @Schema(name = "接收状态", example = "0", description = "参见 SmsReceiveStatusEnum 枚举类")
    private Integer receiveStatus;

    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    @Schema(name = "接收时间")
    private Date[] receiveTime;

}
