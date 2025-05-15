package com.ethan.system.dal.entity.sms;

import com.ethan.common.constant.CommonStatusEnum;
import com.ethan.common.pojo.BaseDO;
import com.ethan.system.controller.admin.sms.vo.enums.SmsChannelEnum;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 短信渠道 DO
 *
 * @author zzf
 * @since 2021-01-25
 */
@Table(name = "system_sms_channel")
@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class SmsChannelDO extends BaseDO {

    /**
     * 渠道编号
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * 短信签名
     */
    private String signature;
    /**
     * 渠道编码
     *
     * 枚举 {@link SmsChannelEnum}
     */
    private String code;
    /**
     * 启用状态
     *
     * 枚举 {@link CommonStatusEnum}
     */
    private Integer status;
    /**
     * 备注
     */
    private String remark;
    /**
     * 短信 API 的账号
     */
    private String apiKey;
    /**
     * 短信 API 的密钥
     */
    private String apiSecret;
    /**
     * 短信发送回调 URL
     */
    private String callbackUrl;

}
