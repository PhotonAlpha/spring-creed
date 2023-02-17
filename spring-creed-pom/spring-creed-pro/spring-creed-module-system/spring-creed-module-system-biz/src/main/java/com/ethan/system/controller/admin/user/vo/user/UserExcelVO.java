package com.ethan.system.controller.admin.user.vo.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * 用户 Excel 导出 VO
 */
@Data
public class UserExcelVO {

    @Schema(name = "用户编号")
    private Long id;

    @Schema(name = "用户名称")
    private String username;

    @Schema(name = "用户昵称")
    private String nickname;

    @Schema(name = "用户邮箱")
    private String email;

    @Schema(name = "手机号码")
    private String mobile;

    @Schema(name = "用户性别")
    // @Schema(name = "用户性别", converter = DictConvert.class)
    // @DictFormat(DictTypeConstants.USER_SEX)
    private Integer sex;

    @Schema(name = "帐号状态")
    // @Schema(name = "帐号状态", converter = DictConvert.class)
    // @DictFormat(DictTypeConstants.COMMON_STATUS)
    private Integer status;

    @Schema(name = "最后登录IP")
    private String loginIp;

    @Schema(name = "最后登录时间")
    private Date loginDate;

    @Schema(name = "部门名称")
    private String deptName;

    @Schema(name = "部门负责人")
    private String deptLeaderNickname;

}
