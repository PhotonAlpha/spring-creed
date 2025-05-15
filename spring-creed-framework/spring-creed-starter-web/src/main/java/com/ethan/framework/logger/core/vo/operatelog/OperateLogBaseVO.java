package com.ethan.framework.logger.core.vo.operatelog;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.Date;
import java.util.Map;

/**
 * 操作日志 Base VO，提供给添加、修改、详细的子 VO 使用
 * 如果子 VO 存在差异的字段，请不要添加到这里，影响 Swagger 文档生成
 */
@Data
public class OperateLogBaseVO {

    @Schema(name = "链路追踪编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "89aca178-a370-411c-ae02-3f0d672be4ab")
    @NotEmpty(message = "链路追踪编号不能为空")
    private String traceId;

    @Schema(name = "用户编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotNull(message = "用户编号不能为空")
    private Long userId;

    @Schema(name = "操作模块", requiredMode = Schema.RequiredMode.REQUIRED, example = "订单")
    @NotEmpty(message = "操作模块不能为空")
    private String module;

    @Schema(name = "操作名", requiredMode = Schema.RequiredMode.REQUIRED, example = "创建订单")
    @NotEmpty(message = "操作名")
    private String name;

    @Schema(name = "操作分类", requiredMode = Schema.RequiredMode.REQUIRED, example = "1", description = "参见 OperateLogTypeEnum 枚举类")
    @NotNull(message = "操作分类不能为空")
    private Integer type;

    @Schema(name = "操作明细", example = "修改编号为 1 的用户信息，将性别从男改成女，将姓名从TEST改成源码。")
    private String content;

    @Schema(name = "拓展字段", example = "{'orderId': 1}")
    private Map<String, Object> exts;

    @Schema(name = "请求方法名", requiredMode = Schema.RequiredMode.REQUIRED, example = "GET")
    @NotEmpty(message = "请求方法名不能为空")
    private String requestMethod;

    @Schema(name = "请求地址", requiredMode = Schema.RequiredMode.REQUIRED, example = "/xxx/yyy")
    @NotEmpty(message = "请求地址不能为空")
    private String requestUrl;

    @Schema(name = "用户 IP", requiredMode = Schema.RequiredMode.REQUIRED, example = "127.0.0.1")
    @NotEmpty(message = "用户 IP 不能为空")
    private String userIp;

    @Schema(name = "浏览器 UserAgent", requiredMode = Schema.RequiredMode.REQUIRED, example = "Mozilla/5.0")
    @NotEmpty(message = "浏览器 UserAgent 不能为空")
    private String userAgent;

    @Schema(name = "Java 方法名", requiredMode = Schema.RequiredMode.REQUIRED, example = "cn.iocoder.yudao.adminserver.UserController.save(...)")
    @NotEmpty(message = "Java 方法名不能为空")
    private String javaMethod;

    @Schema(name = "Java 方法的参数")
    private String javaMethodArgs;

    @Schema(name = "开始时间", required = true)
    @NotNull(message = "开始时间不能为空")
    private Date startTime;

    @Schema(name = "执行时长，单位：毫秒", required = true)
    @NotNull(message = "执行时长不能为空")
    private Integer duration;

    @Schema(name = "结果码", required = true)
    @NotNull(message = "结果码不能为空")
    private Integer resultCode;

    @Schema(name = "结果提示")
    private String resultMsg;

    @Schema(name = "结果数据")
    private String resultData;

}
