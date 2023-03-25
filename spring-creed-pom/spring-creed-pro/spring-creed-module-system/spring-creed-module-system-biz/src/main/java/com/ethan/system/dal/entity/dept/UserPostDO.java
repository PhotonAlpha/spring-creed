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
import lombok.experimental.Accessors;

/**
 * 用户和岗位关联
 *
 * @author ruoyi
 */
@Table(name = "system_user_post")
@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class UserPostDO extends BaseDO {

    /**
     * 自增主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * 用户 ID
     *
     * 关联 {@link AdminUserDO#getId()}
     */
    private Long userId;
    /**
     * 角色 ID
     *
     * 关联 {@link PostDO#getId()}
     */
    private Long postId;

}
