package com.revpay.service;

import com.revpay.dao.UserSecurityDao;
import com.revpay.dao.impl.UserSecurityDaoJdbc;
import com.revpay.util.SecurityUtils;

public class PinService {

    private final UserSecurityDao dao = new UserSecurityDaoJdbc();


    // set or change pin
    public void setPin(long userId, String pin) {

        String hash = SecurityUtils.hash(pin);

        dao.savePin(userId, hash);
    }


    // verify pin
    public boolean verify(long userId, String pin) {

        return dao.getPinHash(userId)
                .map(hash -> SecurityUtils.verify(pin, hash))
                .orElse(false);
    }


    public boolean hasPin(long userId) {
        return dao.getPinHash(userId).isPresent();
    }
}
