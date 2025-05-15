package com.ethan.system.dal.entity.sms;

import com.ethan.common.constant.CommonStatusEnum;
import com.ethan.common.converter.ListJacksonConverter;
import com.ethan.common.pojo.BaseDO;
import com.ethan.system.api.constant.sms.SmsTemplateTypeEnum;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

/**
 * 短信模板 DO
 *
 * @author zzf
 * @since 2021-01-25
 */
@Table(name = "system_sms_template")
@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class SmsTemplateDO extends BaseDO {

    /**
     * 自增编号
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ========= 模板相关字段 =========

    /**
     * 短信类型
     *
     * 枚举 {@link SmsTemplateTypeEnum}
     */
    private Integer type;
    /**
     * 启用状态
     *
     * 枚举 {@link CommonStatusEnum}
     */
    private Integer status;
    /**
     * 模板编码，保证唯一
     */
    private String code;
    /**
     * 模板名称
     */
    private String name;
    /**
     * 模板内容
     *
     * 内容的参数，使用 {} 包括，例如说 {name}
     */
    private String content;
    /**
     * 参数数组(自动根据内容生成)
     */
    // @TableField(typeHandler = JacksonTypeHandler.class)
    @Convert(converter = ListJacksonConverter.class)
    private List<String> params;
    /**
     * 备注
     */
    private String remark;
    /**
     * 短信 API 的模板编号
     */
    private String apiTemplateId;

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

}
