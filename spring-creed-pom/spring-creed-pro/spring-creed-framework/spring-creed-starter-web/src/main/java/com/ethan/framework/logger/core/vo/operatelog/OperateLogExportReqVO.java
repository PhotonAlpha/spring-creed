package com.ethan.framework.logger.core.vo.operatelog;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

import static com.ethan.common.utils.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;


@Schema(name = "管理后台 - 操作日志分页列表 Request VO")
@Data
public class OperateLogExportReqVO {

    @Schema(name = "操作模块", example = "订单", description = "模拟匹配")
    private String module;

    @Schema(name = "用户昵称", example = "芋道", description = "模拟匹配")
    private String userNickname;

    @Schema(name = "操作分类", example = "1", description = "参见 OperateLogTypeEnum 枚举类")
    private Integer type;

    @Schema(name = "操作状态", example = "true")
    private Boolean success;

    @Schema(name = "开始时间", example = "[2022-07-01 00:00:00,2022-07-01 23:59:59]")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private Date startTime;

}
