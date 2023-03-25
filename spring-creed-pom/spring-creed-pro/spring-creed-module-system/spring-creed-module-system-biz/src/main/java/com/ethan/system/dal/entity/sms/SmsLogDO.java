package com.ethan.system.dal.entity.sms;

import com.ethan.common.constant.UserTypeEnum;
import com.ethan.common.converter.MapJacksonConverter;
import com.ethan.common.pojo.BaseDO;
import com.ethan.system.api.constant.sms.SmsReceiveStatusEnum;
import com.ethan.system.api.constant.sms.SmsSendStatusEnum;
import com.ethan.system.controller.admin.sms.vo.enums.SmsFrameworkErrorCodeConstants;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;
import java.util.Map;

/**
 * 短信日志 DO
 *
 * @author zzf
 * @since 2021-01-25
 */
@Table(name = "system_sms_log")
@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SmsLogDO extends BaseDO {

    /**
     * 自增编号
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ========= 渠道相关字段 =========

    /**
     * 短信渠道编号
     *
     * 关联 {@link SmsChannelDO#getId()}
     */
    private Long channelId;
    /**
     * 短信渠道编码
     *
     * 冗余 {@link SmsChannelDO#getCode()}
     */
    private String channelCode;

    // ========= 模板相关字段 =========

    /**
     * 模板编号
     *
     * 关联 {@link SmsTemplateDO#getId()}
     */
    private Long templateId;
    /**
     * 模板编码
     *
     * 冗余 {@link SmsTemplateDO#getCode()}
     */
    private String templateCode;
    /**
     * 短信类型
     *
     * 冗余 {@link SmsTemplateDO#getType()}
     */
    private Integer templateType;
    /**
     * 基于 {@link SmsTemplateDO#getContent()} 格式化后的内容
     */
    private String templateContent;
    /**
     * 基于 {@link SmsTemplateDO#getParams()} 输入后的参数
     */
    @Convert(converter = MapJacksonConverter.class)
    private Map<String, Object> templateParams;
    /**
     * 短信 API 的模板编号
     *
     * 冗余 {@link SmsTemplateDO#getApiTemplateId()}
     */
    private String apiTemplateId;

    // ========= 手机相关字段 =========

    /**
     * 手机号
     */
    private String mobile;
    /**
     * 用户编号
     */
    private Long userId;
    /**
     * 用户类型
     *
     * 枚举 {@link UserTypeEnum}
     */
    private Integer userType;

    // ========= 发送相关字段 =========

    /**
     * 发送状态
     *
     * 枚举 {@link SmsSendStatusEnum}
     */
    private Integer sendStatus;
    /**
     * 发送时间
     */
    private Date sendTime;
    /**
     * 发送结果的编码
     *
     * 枚举 {@link SmsFrameworkErrorCodeConstants}
     */
    private Integer sendCode;
    /**
     * 发送结果的提示
     *
     * 一般情况下，使用 {@link SmsFrameworkErrorCodeConstants}
     * 异常情况下，通过格式化 Exception 的提示存储
     */
    private String sendMsg;
    /**
     * 短信 API 发送结果的编码
     *
     * 由于第三方的错误码可能是字符串，所以使用 String 类型
     */
    private String apiSendCode;
    /**
     * 短信 API 发送失败的提示
     */
    private String apiSendMsg;
    /**
     * 短信 API 发送返回的唯一请求 ID
     *
     * 用于和短信 API 进行定位于排错
     */
    private String apiRequestId;
    /**
     * 短信 API 发送返回的序号
     *
     * 用于和短信 API 平台的发送记录关联
     */
    private String apiSerialNo;

    // ========= 接收相关字段 =========

    /**
     * 接收状态
     *
     * 枚举 {@link SmsReceiveStatusEnum}
     */
    private Integer receiveStatus;
    /**
     * 接收时间
     */
    private Date receiveTime;
    /**
     * 短信 API 接收结果的编码
     */
    private String apiReceiveCode;
    /**
     * 短信 API 接收结果的提示
     */
    private String apiReceiveMsg;

}
