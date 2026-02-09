package com.revpay.dao;

import com.revpay.model.User;

import java.util.Optional;

public interface UserDao {

    // create user
    long create(User user);

    // find by id
    Optional<User> findById(long id);

    // find by username
    Optional<User> findByUsername(String username);

    // find by email or phone (for login)
    Optional<User> findByEmailOrPhone(String value);

    // update user
    boolean update(User user);
    // delete user
    boolean delete(long id);

}
