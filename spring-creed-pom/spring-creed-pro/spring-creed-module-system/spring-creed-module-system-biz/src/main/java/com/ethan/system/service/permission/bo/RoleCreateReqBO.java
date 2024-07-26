package com.ethan.system.service.permission.bo;

import com.ethan.security.websecurity.constant.RoleTypeEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 角色创建 Request BO
 */
@Data
public class RoleCreateReqBO {

    /**
     * 租户编号
     */
    @NotNull(message = "租户编号不能为空")
    private Long tenantId;

    /**
     * 角色名称
     */
    @NotBlank(message = "角色名称不能为空")
    @Size(max = 30, message = "角色名称长度不能超过30个字符")
    private String name;

    /**
     * 角色标志
     */
    @NotBlank(message = "角色标志不能为空")
    @Size(max = 100, message = "角色标志长度不能超过100个字符")
    private String code;

    /**
     * 显示顺序
     */
    @NotNull(message = "显示顺序不能为空")
    private Integer sort;

    /**
     * 角色类型
     */
    @NotNull(message = "角色类型不能为空")
    private RoleTypeEnum type;

}
