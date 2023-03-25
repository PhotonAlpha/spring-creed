package com.ethan.system.controller.admin.sms.vo.log;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;
import java.util.Map;

@Schema(name="管理后台 - 短信日志 Response VO")
@Data
public class SmsLogRespVO {

    @Schema(name = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    @Schema(name = "短信渠道编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "10")
    private Long channelId;

    @Schema(name = "短信渠道编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "ALIYUN")
    private String channelCode;

    @Schema(name = "模板编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "20")
    private Long templateId;

    @Schema(name = "模板编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "test-01")
    private String templateCode;

    @Schema(name = "短信类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer templateType;

    @Schema(name = "短信内容", requiredMode = Schema.RequiredMode.REQUIRED, example = "你好，你的验证码是 1024")
    private String templateContent;

    @Schema(name = "短信参数", requiredMode = Schema.RequiredMode.REQUIRED, example = "name,code")
    private Map<String, Object> templateParams;

    @Schema(name = "短信 API 的模板编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "SMS_207945135")
    private String apiTemplateId;

    @Schema(name = "手机号", requiredMode = Schema.RequiredMode.REQUIRED, example = "15601691300")
    private String mobile;

    @Schema(name = "用户编号", example = "10")
    private Long userId;

    @Schema(name = "用户类型", example = "1")
    private Integer userType;

    @Schema(name = "发送状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer sendStatus;

    @Schema(name = "发送时间")
    private Date sendTime;

    @Schema(name = "发送结果的编码", example = "0")
    private Integer sendCode;

    @Schema(name = "发送结果的提示", example = "成功")
    private String sendMsg;

    @Schema(name = "短信 API 发送结果的编码", example = "SUCCESS")
    private String apiSendCode;

    @Schema(name = "短信 API 发送失败的提示", example = "成功")
    private String apiSendMsg;

    @Schema(name = "短信 API 发送返回的唯一请求 ID", example = "3837C6D3-B96F-428C-BBB2-86135D4B5B99")
    private String apiRequestId;

    @Schema(name = "短信 API 发送返回的序号", example = "62923244790")
    private String apiSerialNo;

    @Schema(name = "接收状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
    private Integer receiveStatus;

    @Schema(name = "接收时间")
    private Date receiveTime;

    @Schema(name = "API 接收结果的编码", example = "DELIVRD")
    private String apiReceiveCode;

    @Schema(name = "API 接收结果的说明", example = "用户接收成功")
    private String apiReceiveMsg;

    @Schema(name = "创建时间", required = true)
    private Date createTime;

}
