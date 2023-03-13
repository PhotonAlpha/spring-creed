package com.ethan.system.controller.admin.tenant.vo.tenant;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;


/**
 * 租户 Excel VO
 *
 * 
 */
@Data
public class TenantExcelVO {

    @Schema(name = "租户编号")
    private Long id;

    @Schema(name = "租户名")
    private String name;

    @Schema(name = "联系人")
    private String contactName;

    @Schema(name = "联系手机")
    private String contactMobile;

    @Schema(name = "状态")
    // @Schema(name = "状态", implementation = DictConvert.class)
    // @DictFormat(DictTypeConstants.COMMON_STATUS)
    private Integer status;

    @Schema(name = "创建时间")
    private Date createTime;

}
