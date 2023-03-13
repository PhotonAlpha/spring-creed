package com.ethan.system.controller.admin.tenant.vo.tenant;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import java.util.Date;

/**
* 租户 Base VO，提供给添加、修改、详细的子 VO 使用
* 如果子 VO 存在差异的字段，请不要添加到这里，影响 Swagger 文档生成
*/
@Data
public class TenantBaseVO {

    @Schema(name = "租户名", requiredMode = Schema.RequiredMode.REQUIRED, example = "芋道")
    @NotNull(message = "租户名不能为空")
    private String name;

    @Schema(name = "联系人", requiredMode = Schema.RequiredMode.REQUIRED, example = "芋艿")
    @NotNull(message = "联系人不能为空")
    private String contactName;

    @Schema(name = "联系手机", example = "15601691300")
    private String contactMobile;

    @Schema(name = "租户状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "租户状态")
    private Integer status;

    @Schema(name = "绑定域名", example = "https://www.iocoder.cn")
    @URL(message = "绑定域名的地址非 URL 格式")
    private String domain;

    @Schema(name = "租户套餐编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotNull(message = "租户套餐编号不能为空")
    private Long packageId;

    @Schema(name = "过期时间", required = true)
    @NotNull(message = "过期时间不能为空")
    private Date expireTime;

    @Schema(name = "账号数量", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotNull(message = "账号数量不能为空")
    private Integer accountCount;

}
