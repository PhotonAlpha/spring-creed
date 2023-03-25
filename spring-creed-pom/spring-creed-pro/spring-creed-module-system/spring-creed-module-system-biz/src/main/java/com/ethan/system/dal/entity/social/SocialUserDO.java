package com.ethan.system.dal.entity.social;

import com.ethan.common.pojo.BaseDO;
import com.ethan.system.api.constant.social.SocialTypeEnum;
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
import lombok.experimental.Accessors;

/**
 * 社交（三方）用户
 *
 * @author weir
 */
@Table(name = "system_social_user")
@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class SocialUserDO extends BaseDO {

    /**
     * 自增主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * 社交平台的类型
     * <p>
     * 枚举 {@link SocialTypeEnum}
     */
    private Integer type;

    /**
     * 社交 openid
     */
    private String openid;
    /**
     * 社交 token
     */
    private String token;
    /**
     * 原始 Token 数据，一般是 JSON 格式
     */
    private String rawTokenInfo;

    /**
     * 用户昵称
     */
    private String nickname;
    /**
     * 用户头像
     */
    private String avatar;
    /**
     * 原始用户数据，一般是 JSON 格式
     */
    private String rawUserInfo;

    /**
     * 最后一次的认证 code
     */
    private String code;
    /**
     * 最后一次的认证 state
     */
    private String state;

}


