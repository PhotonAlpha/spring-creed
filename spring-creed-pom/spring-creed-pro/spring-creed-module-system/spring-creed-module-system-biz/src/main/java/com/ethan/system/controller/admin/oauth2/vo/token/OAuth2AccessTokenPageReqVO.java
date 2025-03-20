package com.ethan.system.controller.admin.oauth2.vo.token;

import com.ethan.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(name = "管理后台 - 访问令牌分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class OAuth2AccessTokenPageReqVO extends PageParam {

    @Schema(name = "用户名", requiredMode = Schema.RequiredMode.REQUIRED, example = "666")
    private String userName;

    @Schema(name = "用户类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "2", description = "参见 UserTypeEnum 枚举")
    private Integer userType;

    @Schema(name = "客户端编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    private String clientId;

}
