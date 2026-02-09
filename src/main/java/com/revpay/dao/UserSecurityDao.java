package com.revpay.dao;

import java.util.Optional;

public interface UserSecurityDao {

    // save or update pin hash
    void savePin(long userId, String pinHash);

    // get stored hash
    Optional<String> getPinHash(long userId);


}
