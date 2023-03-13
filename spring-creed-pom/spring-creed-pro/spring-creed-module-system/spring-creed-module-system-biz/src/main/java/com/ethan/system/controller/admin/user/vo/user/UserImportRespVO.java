package com.ethan.system.controller.admin.user.vo.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Schema(name = "管理后台 - 用户导入 Response VO")
@Data
@Builder
public class UserImportRespVO {

    @Schema(name = "创建成功的用户名数组", required = true)
    private List<String> createUsernames;

    @Schema(name = "更新成功的用户名数组", required = true)
    private List<String> updateUsernames;

    @Schema(name = "导入失败的用户集合", requiredMode = Schema.RequiredMode.REQUIRED, description = "key 为用户名，value 为失败原因")
    private Map<String, String> failureUsernames;

}
