package com.ethan.system.controller.admin.dict.vo.data;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(name ="管理后台 - 字典数据更新 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class DictDataUpdateReqVO extends DictDataBaseVO {

    @Schema(name = "字典数据编号", required = true, example = "1024")
    @NotNull(message = "字典数据编号不能为空")
    private Long id;

}
