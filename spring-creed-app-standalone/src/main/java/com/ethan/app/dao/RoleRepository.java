package com.ethan.app.dao;


import com.ethan.app.model.Authoritys;

public interface RoleRepository extends BaseRepository<Authoritys> {
  Authoritys findByName(String roleName);
}
