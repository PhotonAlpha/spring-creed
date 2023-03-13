package com.ethan.system.controller.admin.user.vo.profile;

import com.ethan.system.controller.admin.user.vo.user.UserBaseVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;


@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "管理后台 - 用户个人中心信息 Response VO")
public class UserProfileRespVO extends UserBaseVO {

    @Schema(name = "用户编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(name = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1", description = "参见 CommonStatusEnum 枚举类")
    private Integer status;

    @Schema(name = "最后登录 IP", requiredMode = Schema.RequiredMode.REQUIRED, example = "192.168.1.1")
    private String loginIp;

    @Schema(name = "最后登录时间", requiredMode = Schema.RequiredMode.REQUIRED, example = "时间戳格式")
    private Date loginDate;

    @Schema(name = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED, example = "时间戳格式")
    private Date createTime;

    /**
     * 所属角色
     */
    private List<Role> roles;

    /**
     * 所在部门
     */
    private Dept dept;

    /**
     * 所属岗位数组
     */
    private List<Post> posts;
    /**
     * 社交用户数组
     */
    private List<SocialUser> socialUsers;

    @Schema(name = "角色")
    @Data
    public static class Role {

        @Schema(name = "角色编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
        private Long id;

        @Schema(name = "角色名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "普通角色")
        private String name;

    }

    @Schema(name = "部门")
    @Data
    public static class Dept {

        @Schema(name = "部门编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
        private Long id;

        @Schema(name = "部门名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "研发部")
        private String name;

    }

    @Schema(name = "岗位")
    @Data
    public static class Post {

        @Schema(name = "岗位编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
        private Long id;

        @Schema(name = "岗位名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "开发")
        private String name;

    }

    @Schema(name = "社交用户")
    @Data
    public static class SocialUser {

        @Schema(name = "社交平台的类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "10", description = "参见 SocialTypeEnum 枚举类")
        private Integer type;

        @Schema(name = "社交用户的 openid", requiredMode = Schema.RequiredMode.REQUIRED, example = "IPRmJ0wvBptiPIlGEZiPewGwiEiE")
        private String openid;

    }

}
