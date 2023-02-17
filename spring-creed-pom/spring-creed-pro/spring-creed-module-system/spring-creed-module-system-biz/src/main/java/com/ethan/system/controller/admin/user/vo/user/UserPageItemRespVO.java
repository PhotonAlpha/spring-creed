package com.ethan.system.controller.admin.user.vo.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Schema(name =  "管理后台 - 用户分页时的信息 Response VO", description = "相比用户基本信息来说，会多部门信息")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UserPageItemRespVO extends UserRespVO {

    /**
     * 所在部门
     */
    private Dept dept;

    @Schema(name = "部门")
    @Data
    public static class Dept {

        @Schema(name = "部门编号", required = true, example = "1")
        private Long id;

        @Schema(name = "部门名称", required = true, example = "研发部")
        private String name;

    }

}
