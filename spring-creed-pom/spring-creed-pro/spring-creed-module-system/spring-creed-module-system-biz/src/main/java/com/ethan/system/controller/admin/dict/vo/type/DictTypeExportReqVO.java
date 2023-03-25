package com.ethan.system.controller.admin.dict.vo.type;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

import static com.ethan.common.utils.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(name ="管理后台 - 字典类型分页列表 Request VO")
@Data
public class DictTypeExportReqVO {

    @Schema(name = "字典类型名称", example = "TES", description = "模糊匹配")
    private String name;

    @Schema(name = "字典类型", example = "sys_common_sex", description = "模糊匹配")
    private String type;

    @Schema(name = "展示状态", example = "1", description = "参见 CommonStatusEnum 枚举类")
    private Integer status;

    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    @Schema(name = "创建时间")
    private Date createTime;

}
