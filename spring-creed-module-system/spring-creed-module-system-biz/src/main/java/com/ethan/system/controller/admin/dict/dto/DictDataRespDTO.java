package com.ethan.system.controller.admin.dict.dto;

import com.ethan.common.constant.CommonStatusEnum;
import lombok.Data;

/**
 * 字典数据 Response DTO
 *
 */
@Data
public class DictDataRespDTO {

    /**
     * 字典标签
     */
    private String label;
    /**
     * 字典值
     */
    private String value;
    /**
     * 字典类型
     */
    private String dictType;
    /**
     * 状态
     *
     * 枚举 {@link CommonStatusEnum}
     */
    private Integer status;

}
