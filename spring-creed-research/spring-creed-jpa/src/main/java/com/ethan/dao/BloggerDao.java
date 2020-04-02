/**
 * describe: copy right by @author
 *
 * @author xxx
 * @date 2020/04/02
 */
package com.ethan.dao;

import com.ethan.entity.BloggerDO;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BloggerDao extends JpaRepository<BloggerDO, Long> {

}
