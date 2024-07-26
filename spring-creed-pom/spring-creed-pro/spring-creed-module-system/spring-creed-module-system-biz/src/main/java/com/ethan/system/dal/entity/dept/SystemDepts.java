package com.ethan.system.dal.entity.dept;

import com.ethan.common.pojo.BaseXDO;
import com.ethan.system.dal.entity.user.AdminUserDO;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.DynamicUpdate;

import java.util.List;

/**
 * 部门表
 *
 * @author ruoyi
 * 
 */
@Table(name = "creed_system_depts", indexes = {
        @Index(name = "CSD_IDX_COMMON", columnList = "name"),
        @Index(name = "CSD_IDX_PID", columnList = "parentId")
})
@Entity
@DynamicUpdate
@Data
@EqualsAndHashCode(callSuper = false)
@ToString(exclude = {"deptUsers"})
public class SystemDepts extends BaseXDO {

    public static final Long PARENT_ID_ROOT = 0L;
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
     * 部门状态 enabled
     *
     * 枚举 {@link com.ethan.common.constant.CommonStatusEnum}
     */

    @OneToMany(mappedBy = "depts")
    private List<SystemDeptUsers> deptUsers;
}
