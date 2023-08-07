package com.ethan.system.dal.entity.notice;

import com.ethan.common.constant.CommonStatusEnum;
import com.ethan.common.pojo.BaseDO;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 通知公告表
 *
 * @author ruoyi
 */
@Table(name = "system_notice")
@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class NoticeDO extends BaseDO {

    /**
     * 公告ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * 公告标题
     */
    private String title;
    /**
     * 公告类型
     *
     * 枚举 {@link NoticeTypeEnum}
     */
    private Integer type;
    /**
     * 公告内容
     */
    private String content;
    /**
     * 公告状态
     *
     * 枚举 {@link CommonStatusEnum}
     */
    private Integer status;

}
