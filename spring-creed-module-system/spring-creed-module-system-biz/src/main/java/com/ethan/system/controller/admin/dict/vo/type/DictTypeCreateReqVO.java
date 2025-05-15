package com.ethan.system.controller.admin.dict.vo.type;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(name ="管理后台 - 字典类型创建 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class DictTypeCreateReqVO extends DictTypeBaseVO {

    @Schema(name = "字典类型", required = true, example = "sys_common_sex")
    @NotNull(message = "字典类型不能为空")
    @Size(max = 100, message = "字典类型类型长度不能超过100个字符")
    private String type;

}
