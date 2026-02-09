package com.revpay.dao;

import com.revpay.model.Transaction;

import java.util.List;

public interface TransactionDao {

    // insert new transaction record
    long create(Transaction txn);

    // get all transactions of a user
    List<Transaction> findByUserId(long userId);
}