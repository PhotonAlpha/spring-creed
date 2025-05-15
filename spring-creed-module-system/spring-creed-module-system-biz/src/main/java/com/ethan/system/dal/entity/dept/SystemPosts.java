package com.ethan.system.dal.entity.dept;

import com.ethan.common.constant.CommonStatusEnum;
import com.ethan.common.pojo.BaseDO;
import com.ethan.common.pojo.BaseXDO;
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
 * 岗位表
 *
 * @author ruoyi
 */
@Table(name = "creed_system_posts", indexes = {
        @Index(name = "CSP_IDX_COMMON", columnList = "code,name")
})
@Entity
@DynamicUpdate
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(exclude = {"postUsers"})
public class SystemPosts extends BaseXDO {

    /**
     * 岗位序号
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * 岗位名称
     */
    private String name;
    /**
     * 岗位编码
     */
    private String code;
    /**
     * 岗位排序
     */
    private Integer sort;
    /**
     * 状态 enabled
     *
     * 枚举 {@link CommonStatusEnum}
     */

    /**
     * 备注
     */
    private String remark;

    @OneToMany(mappedBy = "posts")
    private List<SystemPostUsers> postUsers;
}
