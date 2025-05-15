package com.ethan.reactive.dal.entity.permission;

import com.ethan.reactive.dal.pojo.TenantBaseDO;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色和菜单关联
 *
 * @author ruoyi
 */
@Table(name = "system_role_menu")
@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class RoleMenuDO extends TenantBaseDO {

    /**
     * 自增主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * 角色ID
     */
    private String roleId;
    /**
     * 菜单ID
     */
    private Long menuId;

}
