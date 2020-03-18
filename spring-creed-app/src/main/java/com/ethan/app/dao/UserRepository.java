package com.ethan.app.dao;


import com.ethan.app.model.Users;

public interface UserRepository extends BaseRepository<Users> {

    Users findByUsername(String username);
}
