package com.ethan.system.controller.admin.dict.vo.data;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(name ="管理后台 - 数据字典精简 Response VO")
@Data
public class DictDataSimpleRespVO {

    @Schema(name = "字典类型", required = true, example = "gender")
    private String dictType;

    @Schema(name = "字典键值", required = true, example = "1")
    private String value;

    @Schema(name = "字典标签", required = true, example = "男")
    private String label;

    @Schema(name = "颜色类型", example = "default", description = "default、primary、success、info、warning、danger")
    private String colorType;
    @Schema(name = "css 样式", example = "btn-visible")
    private String cssClass;

}
