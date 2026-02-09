package com.revpay.service;

import com.revpay.dao.TransactionDao;
import com.revpay.dao.impl.TransactionDaoJdbc;
import com.revpay.model.Transaction;
import com.revpay.dao.UserDao;
import com.revpay.dao.impl.UserDaoJdbc;

import java.math.BigDecimal;
import java.util.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.UUID;

public class TransactionService {

    private static final Logger logger =
            LogManager.getLogger(TransactionService.class);


    private final WalletService walletService = new WalletService();
    private final TransactionDao transactionDao = new TransactionDaoJdbc();
    private final UserDao userDao = new UserDaoJdbc();

    // ---------------- SEND MONEY ----------------
    public boolean sendMoney(long fromUserId, long toUserId, BigDecimal amount) {

        logger.info("Transfer started: from={} to={} amount={}",
                fromUserId, toUserId, amount);

        // 1. withdraw from sender
        boolean ok = walletService.withdraw(fromUserId, amount);

        if (!ok) {
            return false; // insufficient funds
        }

        // 2. add to receiver
        walletService.addMoney(toUserId, amount);

        // 3. record transaction
        Transaction txn = new Transaction();

        txn.setTxnUuid(UUID.randomUUID().toString());
        txn.setFromUserId(fromUserId);
        txn.setToUserId(toUserId);
        txn.setType("SEND");
        txn.setStatus("COMPLETED");
        txn.setAmount(amount);
        txn.setCurrency("INR");
        txn.setNote("Money transfer");

        transactionDao.create(txn);
        logger.info("Transfer successful: from={} to={} amount={}",
                fromUserId, toUserId, amount);

        return true;
    }

    // ---------------- TOPUP RECORD ----------------
    public void recordTopup(long userId, BigDecimal amount) {

        Transaction txn = new Transaction();

        txn.setTxnUuid(java.util.UUID.randomUUID().toString());
        txn.setFromUserId(null);
        txn.setToUserId(userId);

        txn.setType("TOPUP");
        txn.setStatus("COMPLETED");
        txn.setAmount(amount);
        txn.setCurrency("INR");
        txn.setNote("Wallet top-up");

        transactionDao.create(txn);
    }


    // ---------------- HISTORY ----------------
    public List<Transaction> getHistory(long userId) {

        var txns = transactionDao.findByUserId(userId);

        for (Transaction t : txns) {

            // FROM user
            if (t.getFromUserId() != null) {
                userDao.findById(t.getFromUserId())
                        .ifPresent(u -> t.setFromName(u.getFullName()));
            }

            // TO user
            if (t.getToUserId() != null) {
                userDao.findById(t.getToUserId())
                        .ifPresent(u -> t.setToName(u.getFullName()));
            }
        }

        return txns;
    }




}
