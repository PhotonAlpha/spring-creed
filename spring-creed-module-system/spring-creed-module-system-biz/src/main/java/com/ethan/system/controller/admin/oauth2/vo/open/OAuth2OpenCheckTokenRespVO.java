package com.ethan.system.controller.admin.oauth2.vo.open;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Schema(name="管理后台 - 【开放接口】校验令牌 Response VO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OAuth2OpenCheckTokenRespVO {

    @Schema(name = "用户编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "666")
    @JsonProperty("user_id")
    private Long userId;
    @Schema(name = "用户类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "2", description = "参见 UserTypeEnum 枚举")
    @JsonProperty("user_type")
    private Integer userType;
    @Schema(name = "租户编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @JsonProperty("tenant_id")
    private Long tenantId;

    @Schema(name = "客户端编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "car")
    private String clientId;
    @Schema(name = "授权范围", requiredMode = Schema.RequiredMode.REQUIRED, example = "user_info")
    private List<String> scopes;

    @Schema(name = "访问令牌", requiredMode = Schema.RequiredMode.REQUIRED, example = "tudou")
    @JsonProperty("access_token")
    private String accessToken;

    @Schema(name = "过期时间", requiredMode = Schema.RequiredMode.REQUIRED, example = "1593092157", description = "时间戳 / 1000，即单位：秒")
    private Long exp;

}
