package com.revpay.service;

import com.revpay.dao.UserDao;
import com.revpay.dao.impl.UserDaoJdbc;
import com.revpay.model.User;
import com.revpay.util.SecurityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

public class AuthService {

    private final UserDao userDao = new UserDaoJdbc();
    private static final Logger logger =
            LogManager.getLogger(AuthService.class);


    // ---------------- REGISTER ----------------
    public long register(String username,
                         String fullName,
                         String email,
                         String phone,
                         String password,
                         String accountType) {
        logger.info("Register request received for username={}, email={}", username, email);
        // hash password
        String hash = SecurityUtils.hash(password);

        User user = new User(
                username,
                fullName,
                email,
                phone,
                hash,
                accountType
        );

        logger.info("User registered successfully. email={}, username={}", email, username);
        return userDao.create(user);

    }


    // ---------------- LOGIN ----------------
    public Optional<User> login(String emailOrPhone, String password) {

        Optional<User> userOpt = userDao.findByEmailOrPhone(emailOrPhone);

        if (userOpt.isEmpty()) {
            return Optional.empty();
        }

        User user = userOpt.get();

        boolean ok = SecurityUtils.verify(password, user.getPasswordHash());

        if (!ok) {
            return Optional.empty();
        }

        return Optional.of(user);
    }
}
