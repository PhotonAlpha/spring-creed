package com.ethan.system.controller.admin.dict.vo.type;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(name ="管理后台 - 字典类型更新 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class DictTypeUpdateReqVO extends DictTypeBaseVO {

    @Schema(name = "字典类型编号", required = true, example = "1024")
    @NotNull(message = "字典类型编号不能为空")
    private Long id;

}
