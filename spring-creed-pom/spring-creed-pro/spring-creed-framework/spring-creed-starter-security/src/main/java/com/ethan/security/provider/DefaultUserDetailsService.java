package com.ethan.security.provider;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.UserDetailsManager;

public class DefaultUserDetailsService implements UserDetailsManager {
    // private final JdbcTemplate jdbcTemplate;

    // public JdbcTokenStore(DataSource dataSource) {
    //     Assert.notNull(dataSource, "DataSource required");
    //     this.jdbcTemplate = new JdbcTemplate(dataSource);
    // }

    @Override
    public void createUser(UserDetails user) {

    }

    @Override
    public void updateUser(UserDetails user) {

    }

    @Override
    public void deleteUser(String username) {

    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {

    }

    @Override
    public boolean userExists(String username) {
        return false;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return null;
    }
}
