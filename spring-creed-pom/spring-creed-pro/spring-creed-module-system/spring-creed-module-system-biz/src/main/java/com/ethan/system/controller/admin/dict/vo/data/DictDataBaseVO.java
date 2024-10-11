package com.ethan.system.controller.admin.dict.vo.data;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 字典数据 Base VO，提供给添加、修改、详细的子 VO 使用
 * 如果子 VO 存在差异的字段，请不要添加到这里，影响 Swagger 文档生成
 */
@Data
@NoArgsConstructor
public class DictDataBaseVO {

    @Schema(name = "显示顺序不能为空", required = true, example = "1024")
    @NotNull(message = "显示顺序不能为空")
    private Integer sort;

    @Schema(name = "字典标签", required = true, example = "TES")
    @NotBlank(message = "字典标签不能为空")
    @Size(max = 100, message = "字典标签长度不能超过100个字符")
    private String label;

    @Schema(name = "字典值", required = true, example = "iocoder")
    @NotBlank(message = "字典键值不能为空")
    @Size(max = 100, message = "字典键值长度不能超过100个字符")
    private String value;

    @Schema(name = "字典类型", required = true, example = "sys_common_sex")
    @NotBlank(message = "字典类型不能为空")
    @Size(max = 100, message = "字典类型长度不能超过100个字符")
    private String dictType;

    @Schema(name = "状态", required = true, example = "1", description = "见 CommonStatusEnum 枚举")
    @NotNull(message = "状态不能为空")
//    @InEnum(value = CommonStatusEnum.class, message = "修改状态必须是 {value}")
    private Integer status;

    @Schema(name = "颜色类型", example = "default", description = "default、primary、success、info、warning、danger")
    private String colorType;
    @Schema(name = "css 样式", example = "btn-visible")
    private String cssClass;

    @Schema(name = "备注", example = "我是一个角色")
    private String remark;

    public DictDataBaseVO(String label, String value) {
        this.label = label;
        this.value = value;
    }
}
