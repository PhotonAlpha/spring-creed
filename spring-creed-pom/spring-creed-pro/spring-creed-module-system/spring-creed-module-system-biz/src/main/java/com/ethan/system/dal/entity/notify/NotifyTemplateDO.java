package com.ethan.system.dal.entity.notify;

import com.ethan.common.constant.CommonStatusEnum;
import com.ethan.common.converter.ListJacksonConverter;
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

import java.util.List;

/**
 * 站内信模版 DO
 *
 * @author xrcoder
 */
@Table(name = "system_notify_template")
@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class NotifyTemplateDO extends BaseDO {

    /**
     * ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * 模版名称
     */
    private String name;
    /**
     * 模版编码
     */
    private String code;
    /**
     * 模版类型
     *
     * 对应 system_notify_template_type 字典
     */
    private Integer type;
    /**
     * 发送人名称
     */
    private String nickname;
    /**
     * 模版内容
     */
    private String content;
    /**
     * 参数数组
     */
    @Convert(converter = ListJacksonConverter.class)
    private List<String> params;
    /**
     * 状态
     *
     * 枚举 {@link CommonStatusEnum}
     */
    private Integer status;
    /**
     * 备注
     */
    private String remark;

}
