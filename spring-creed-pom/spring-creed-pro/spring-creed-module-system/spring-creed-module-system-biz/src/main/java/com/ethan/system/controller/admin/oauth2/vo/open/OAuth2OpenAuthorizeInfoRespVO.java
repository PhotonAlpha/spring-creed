package com.ethan.system.controller.admin.oauth2.vo.open;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

@Schema(name="管理后台 - 授权页的信息 Response VO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OAuth2OpenAuthorizeInfoRespVO {

    /**
     * 客户端
     */
    private Client client;

    @Schema(name = "scope 的选中信息", requiredMode = Schema.RequiredMode.REQUIRED, description = "使用 List 保证有序性，Key 是 scope，Value 为是否选中")
    private List<Pair<String, Boolean>> scopes;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Client {

        @Schema(name = "应用名", requiredMode = Schema.RequiredMode.REQUIRED, example = "土豆")
        private String name;

        @Schema(name = "应用图标", requiredMode = Schema.RequiredMode.REQUIRED, example = "https://www.iocoder.cn/xx.png")
        private String logo;

    }

}
