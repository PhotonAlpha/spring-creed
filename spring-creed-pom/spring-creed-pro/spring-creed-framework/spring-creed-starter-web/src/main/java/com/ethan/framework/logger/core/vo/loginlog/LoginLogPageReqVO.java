package com.ethan.framework.logger.core.vo.loginlog;

import com.ethan.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

import static com.ethan.common.utils.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(name = "管理后台 - 登录日志分页列表 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class LoginLogPageReqVO extends PageParam {

    @Schema(name = "用户 IP", example = "127.0.0.1", description = "模拟匹配")
    private String userIp;

    @Schema(name = "用户账号", example = "芋道", description = "模拟匹配")
    private String username;

    @Schema(name = "操作状态", example = "true")
    private Boolean status;

    @Schema(name = "登录时间", example = "[2022-07-01 00:00:00,2022-07-01 23:59:59]")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private Date[] createTime;

}
