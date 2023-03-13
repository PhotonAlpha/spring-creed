package com.ethan.system.controller.admin.oauth2.vo.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Schema(name="管理后台 - OAuth2 获得用户基本信息 Response VO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OAuth2UserInfoRespVO {

    @Schema(name = "用户编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(name = "用户昵称", requiredMode = Schema.RequiredMode.REQUIRED, example = "芋艿")
    private String username;

    @Schema(name = "用户昵称", requiredMode = Schema.RequiredMode.REQUIRED, example = "芋道")
    private String nickname;

    @Schema(name = "用户邮箱", example = "yudao@iocoder.cn")
    private String email;
    @Schema(name = "手机号码", example = "15601691300")
    private String mobile;

    @Schema(name = "用户性别", example = "1", description = "参见 SexEnum 枚举类")
    private Integer sex;

    @Schema(name = "用户头像", example = "https://www.iocoder.cn/xxx.png")
    private String avatar;

    /**
     * 所在部门
     */
    private Dept dept;

    /**
     * 所属岗位数组
     */
    private List<Post> posts;

    @Schema(name="部门")
    @Data
    public static class Dept {

        @Schema(name = "部门编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
        private Long id;

        @Schema(name = "部门名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "研发部")
        private String name;

    }

    @Schema(name="岗位")
    @Data
    public static class Post {

        @Schema(name = "岗位编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
        private Long id;

        @Schema(name = "岗位名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "开发")
        private String name;

    }

}
