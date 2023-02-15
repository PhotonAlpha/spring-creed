package com.ethan.web.logger.core.vo.accesslog;

import com.ethan.common.pojo.PageParam;
import com.ethan.common.utils.date.DateUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Tag(name = "管理后台 - API 错误日志分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ApiErrorLogPageReqVO extends PageParam {

    @Schema(name = "用户编号", example = "666")
    private Long userId;

    @Schema(name = "用户类型", example = "1")
    private Integer userType;

    @Schema(name = "应用名", example = "dashboard")
    private String applicationName;

    @Schema(name = "请求地址", example = "/xx/yy")
    private String requestUrl;

    @DateTimeFormat(pattern = DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    @Schema(name = "异常发生时间")
    private Date[] exceptionTime;

    @Schema(name = "处理状态", example = "0")
    private Integer processStatus;

}
