package com.ethan.system.controller.admin.oauth2.vo.client;

import com.ethan.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Schema(name="管理后台 - OAuth2 客户端分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class OAuth2ClientPageReqVO extends PageParam {

    @Schema(name = "应用名", example = "土豆", description = "模糊匹配")
    private String name;

    @Schema(name = "状态", example = "1", description = "参见 CommonStatusEnum 枚举")
    private Integer status;

}
