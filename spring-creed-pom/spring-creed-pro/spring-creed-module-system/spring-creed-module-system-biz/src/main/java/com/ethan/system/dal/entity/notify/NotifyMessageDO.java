package com.ethan.system.dal.entity.notify;

import com.ethan.common.constant.UserTypeEnum;
import com.ethan.common.converter.MapJacksonConverter;
import com.ethan.common.pojo.BaseDO;
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
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 站内信 DO
 *
 * @author xrcoder
 */
@Table(name = "system_notify_message")
@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class NotifyMessageDO extends BaseDO {

    /**
     * 站内信编号，自增
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * 用户编号
     *
     * 关联 MemberUserDO 的 id 字段、或者 AdminUserDO 的 id 字段
     */
    private Long userId;
    /**
     * 用户类型
     *
     * 枚举 {@link UserTypeEnum}
     */
    private Integer userType;

    // ========= 模板相关字段 =========

    /**
     * 模版编号
     *
     * 关联 {@link NotifyTemplateDO#getId()}
     */
    private Long templateId;
    /**
     * 模版编码
     *
     * 关联 {@link NotifyTemplateDO#getCode()}
     */
    private String templateCode;
    /**
     * 模版类型
     *
     * 冗余 {@link NotifyTemplateDO#getType()}
     */
    private Integer templateType;
    /**
     * 模版发送人名称
     *
     * 冗余 {@link NotifyTemplateDO#getNickname()}
     */
    private String templateNickname;
    /**
     * 模版内容
     *
     * 基于 {@link NotifyTemplateDO#getContent()} 格式化后的内容
     */
    private String templateContent;
    /**
     * 模版参数
     *
     * 基于 {@link NotifyTemplateDO#getParams()} 输入后的参数
     */
    @Convert(converter = MapJacksonConverter.class)
    private Map<String, Object> templateParams;

    // ========= 读取相关字段 =========

    /**
     * 是否已读
     */
    private Boolean readStatus;
    /**
     * 阅读时间
     */
    private LocalDateTime readTime;

}
