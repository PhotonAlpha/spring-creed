package com.ethan.system.dal.entity.dept;

import com.ethan.common.pojo.BaseDO;
import com.ethan.system.dal.entity.user.AdminUserDO;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 部门表
 *
 * @author ruoyi
 * @author 芋道源码
 */
@Table(name = "system_dept")
@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class DeptDO extends BaseDO {

    /**
     * 部门ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * 部门名称
     */
    private String name;
    /**
     * 父部门ID
     *
     * 关联 {@link #id}
     */
    private Long parentId;
    /**
     * 显示顺序
     */
    private Integer sort;
    /**
     * 负责人
     *
     * 关联 {@link AdminUserDO#getId()}
     */
    private Long leaderUserId;
    /**
     * 联系电话
     */
    private String phone;
    /**
     * 邮箱
     */
    private String email;
    /**
     * 部门状态
     *
     * 枚举 {@link com.ethan.common.constant.CommonStatusEnum}
     */
    private Integer status;

}
