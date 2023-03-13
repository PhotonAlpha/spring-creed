package com.ethan.system.controller.admin.dict.vo.type;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 字典类型 Base VO，提供给添加、修改、详细的子 VO 使用
 * 如果子 VO 存在差异的字段，请不要添加到这里，影响 Swagger 文档生成
 */
@Data
public class DictTypeBaseVO {

    @Schema(name = "字典名称", required = true, example = "性别")
    @NotBlank(message = "字典名称不能为空")
    @Size(max = 100, message = "字典类型名称长度不能超过100个字符")
    private String name;

    @Schema(name = "状态", required = true, example = "1", description = "参见 CommonStatusEnum 枚举类")
    @NotNull(message = "状态不能为空")
    private Integer status;

    @Schema(name = "备注", example = "快乐的备注")
    private String remark;

}
