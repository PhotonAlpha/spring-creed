package com.ethan.framework.logger.core.vo.accesslog;

import com.ethan.common.utils.date.DateUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;


/**
* API 访问日志 Base VO，提供给添加、修改、详细的子 VO 使用
* 如果子 VO 存在差异的字段，请不要添加到这里，影响 Swagger 文档生成
*/
@Data
public class ApiAccessLogBaseVO {

    @Schema(name = "链路追踪编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "66600cb6-7852-11eb-9439-0242ac130002")
    @NotNull(message = "链路追踪编号不能为空")
    private String traceId;

    @Schema(name = "用户编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "666")
    @NotNull(message = "用户编号不能为空")
    private Long userId;

    @Schema(name = "用户类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "2", description = "参见 UserTypeEnum 枚举")
    @NotNull(message = "用户类型不能为空")
    private Integer userType;

    @Schema(name = "应用名", requiredMode = Schema.RequiredMode.REQUIRED, example = "dashboard")
    @NotNull(message = "应用名不能为空")
    private String applicationName;

    @Schema(name = "请求方法名", requiredMode = Schema.RequiredMode.REQUIRED, example = "GET")
    @NotNull(message = "请求方法名不能为空")
    private String requestMethod;

    @Schema(name = "请求地址", requiredMode = Schema.RequiredMode.REQUIRED, example = "/xxx/yyy")
    @NotNull(message = "请求地址不能为空")
    private String requestUrl;

    @Schema(name = "请求参数")
    private String requestParams;

    @Schema(name = "用户 IP", requiredMode = Schema.RequiredMode.REQUIRED, example = "127.0.0.1")
    @NotNull(message = "用户 IP不能为空")
    private String userIp;

    @Schema(name = "浏览器 UA", requiredMode = Schema.RequiredMode.REQUIRED, example = "Mozilla/5.0")
    @NotNull(message = "浏览器 UA不能为空")
    private String userAgent;

    @Schema(name = "开始请求时间", required = true)
    @NotNull(message = "开始请求时间不能为空")
    @DateTimeFormat(pattern = DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private Date beginTime;

    @Schema(name = "结束请求时间", required = true)
    @NotNull(message = "结束请求时间不能为空")
    @DateTimeFormat(pattern = DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private Date endTime;

    @Schema(name = "执行时长", requiredMode = Schema.RequiredMode.REQUIRED, example = "100")
    @NotNull(message = "执行时长不能为空")
    private Integer duration;

    @Schema(name = "结果码", requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
    @NotNull(message = "结果码不能为空")
    private Integer resultCode;

    @Schema(name = "结果提示", example = "TEST，牛逼！")
    private String resultMsg;

}
