package com.ethan.system.controller.admin.tenant.vo.packages;

import com.ethan.common.pojo.PageParam;
import com.ethan.common.utils.date.DateUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;


@Tag(name = "管理后台 - 租户套餐分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class TenantPackagePageReqVO extends PageParam {

    @Schema(name = "套餐名", example = "VIP")
    private String name;

    @Schema(name = "状态", example = "1")
    private Integer status;

    @Schema(name = "备注", example = "好")
    private String remark;

    @DateTimeFormat(pattern = DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    @Schema(name = "创建时间")
    private Date[] createTime;
}
