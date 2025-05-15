package com.ethan.system.dal.entity.dept;

import com.ethan.common.pojo.BaseDO;
import com.ethan.system.dal.entity.permission.SystemUsers;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

/**
 * 部门表
 *
 * @author ruoyi
 * 
 */
@Table(name = "creed_system_post_users", indexes = {
        @Index(name = "CSPU_IDX_COMMON", columnList = "post_id,user_id"),
})
@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(exclude = {"users", "posts"})
@NoArgsConstructor
public class SystemPostUsers extends BaseDO {

    /**
     * 主键ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private SystemUsers users;

    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", referencedColumnName = "id")
    private SystemPosts posts;

    public SystemPostUsers(SystemUsers users, SystemPosts posts) {
        this.users = users;
        this.posts = posts;
    }
}
