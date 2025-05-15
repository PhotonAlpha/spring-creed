package com.ethan.system.dal.entity.social;

import com.ethan.common.constant.UserTypeEnum;
import com.ethan.common.pojo.BaseDO;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 社交用户的绑定
 * 即 {@link SocialUserDO} 与 UserDO 的关联表
 *
 * 
 */
@Table(name = "system_social_user_bind")
@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SocialUserBindDO extends BaseDO {

    /**
     * 编号
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * 关联的用户编号
     * <p>
     * 关联 UserDO 的编号
     */
    private String userId;
    /**
     * 用户类型
     * <p>
     * 枚举 {@link UserTypeEnum}
     */
    private Integer userType;

    /**
     * 社交平台的用户编号
     * <p>
     * 关联 {@link SocialUserDO#getId()}
     */
    private Long socialUserId;
    /**
     * 社交平台的类型
     * <p>
     * 冗余 {@link SocialUserDO#getType()}
     */
    private Integer socialType;

}
