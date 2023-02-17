package com.ethan.system.controller.admin.user.vo.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;

@Schema(name = "管理后台 - 用户信息 Response VO")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UserRespVO extends UserBaseVO {

    @Schema(name = "用户编号", required = true, example = "1")
    private Long id;

    @Schema(name = "状态", required = true, example = "1", description = "参见 CommonStatusEnum 枚举类")
    private Integer status;

    @Schema(name = "最后登录 IP", required = true, example = "192.168.1.1")
    private String loginIp;

    @Schema(name = "最后登录时间", required = true, example = "时间戳格式")
    private Date loginDate;

    @Schema(name = "创建时间", required = true, example = "时间戳格式")
    private Date createTime;

}
