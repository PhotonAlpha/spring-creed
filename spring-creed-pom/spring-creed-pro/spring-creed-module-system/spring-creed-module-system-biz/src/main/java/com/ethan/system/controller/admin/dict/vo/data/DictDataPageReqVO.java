package com.ethan.system.controller.admin.dict.vo.data;

import com.ethan.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(name ="管理后台 - 字典类型分页列表 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class DictDataPageReqVO extends PageParam {

    @Schema(name = "字典标签", example = "TES")
    @Size(max = 100, message = "字典标签长度不能超过100个字符")
    private String label;

    @Schema(name = "字典类型", example = "sys_common_sex", description = "模糊匹配")
    @Size(max = 100, message = "字典类型类型长度不能超过100个字符")
    private String dictType;

    @Schema(name = "展示状态", example = "1", description = "参见 CommonStatusEnum 枚举类")
    private Integer status;

}
