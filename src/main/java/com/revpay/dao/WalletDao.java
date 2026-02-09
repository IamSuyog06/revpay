package com.revpay.dao;

import com.revpay.model.Wallet;

import java.math.BigDecimal;
import java.util.Optional;

public interface WalletDao {

    // create wallet for user
    long create(Wallet wallet);

    // find wallet by user id (1 user = 1 wallet in your schema)
    Optional<Wallet> findByUserId(long userId);

    // find by wallet id
    Optional<Wallet> findById(long id);

    // update balance
    boolean updateBalance(long walletId, BigDecimal newBalance);
}
