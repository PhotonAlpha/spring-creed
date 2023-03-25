package com.ethan.system.controller.admin.sms.vo.log;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

import static com.ethan.common.utils.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(name = "管理后台 - 短信日志 Excel 导出 Request VO", description = "参数和 SmsLogPageReqVO 是一致的")
@Data
public class SmsLogExportReqVO {

    @Schema(name = "短信渠道编号", example = "10")
    private Long channelId;

    @Schema(name = "模板编号", example = "20")
    private Long templateId;

    @Schema(name = "手机号", example = "15601691300")
    private String mobile;

    @Schema(name = "发送状态", example = "1")
    private Integer sendStatus;

    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    @Schema(name = "开始发送时间")
    private Date[] sendTime;

    @Schema(name = "接收状态", example = "0")
    private Integer receiveStatus;

    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    @Schema(name = "开始接收时间")
    private Date[] receiveTime;

}
