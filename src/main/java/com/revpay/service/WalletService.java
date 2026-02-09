package com.revpay.service;

import com.revpay.dao.WalletDao;
import com.revpay.dao.impl.WalletDaoJdbc;
import com.revpay.model.Wallet;

import java.math.BigDecimal;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class WalletService {

    private static final Logger logger =
            LogManager.getLogger(WalletService.class);


    private final WalletDao walletDao = new WalletDaoJdbc();


    // ---------------- CREATE WALLET ----------------
    public long createWallet(long userId) {

        Optional<Wallet> existing = walletDao.findByUserId(userId);

        if (existing.isPresent()) {
            return existing.get().getId(); // already exists
        }

        Wallet wallet = new Wallet(userId, "INR");

        return walletDao.create(wallet);
    }


    // ---------------- GET BALANCE ----------------
    public BigDecimal getBalance(long userId) {

        return walletDao.findByUserId(userId)
                .map(Wallet::getBalance)
                .orElse(BigDecimal.ZERO);
    }


    // ---------------- ADD MONEY (TOPUP) ----------------
    public void addMoney(long userId, BigDecimal amount) {

        logger.info("Add money request: user={}, amount={}", userId, amount);
        Wallet wallet = walletDao.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        BigDecimal newBalance = wallet.getBalance().add(amount);

        walletDao.updateBalance(wallet.getId(), newBalance);

        logger.info("Money added successfully: user={}, newBalance={}", userId, newBalance);
    }


    // ---------------- WITHDRAW MONEY ----------------
    public boolean withdraw(long userId, BigDecimal amount) {

        logger.info("Withdraw request: user={}, amount={}", userId, amount);

        Wallet wallet = walletDao.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        if (wallet.getBalance().compareTo(amount) < 0) {
            return false; // insufficient funds
        }

        BigDecimal newBalance = wallet.getBalance().subtract(amount);

        walletDao.updateBalance(wallet.getId(), newBalance);
        logger.info("Withdraw successful: user={}, newBalance={}", userId, newBalance);
        return true;
    }
}
