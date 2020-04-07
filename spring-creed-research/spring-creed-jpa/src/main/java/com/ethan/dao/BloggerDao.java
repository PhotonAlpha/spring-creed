/**
 * describe: copy right by @author
 *
 * @author xxx
 * @date 2020/04/02
 */
package com.ethan.dao;

import com.ethan.entity.BloggerDO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BloggerDao extends JpaRepository<BloggerDO, Long> {

  @Query(" select b from BloggerDO b join b.roles r where b.name = ?1 or b.phone = ?1 or b.email =?1 ")
  BloggerDO loadUserByUsername(String username);
}
