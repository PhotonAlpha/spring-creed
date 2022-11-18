/**
 * describe: copy right by @author
 *
 * @author xxx
 * @date 2020/04/02
 */
package com.ethan.dao;

import com.ethan.entity.CommentDO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import java.util.Optional;

public interface CommentDao extends JpaRepository<CommentDO, Long> {
    Optional<CommentDO> findByBlogId(Long blogId);

    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from CommentDO a where  a.commentId = :blogId")
    @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value = "5000")})
    Optional<CommentDO> findByBlogIdWithPessimisticLock(Long blogId);

}
