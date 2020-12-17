package com.ethan.datasource.dao.user;

import com.ethan.datasource.model.user.UserDO;
import org.springframework.data.repository.CrudRepository;

public interface UserDao extends CrudRepository<UserDO, Long> {
}
