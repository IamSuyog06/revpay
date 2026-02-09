package com.revpay.dao;

import com.revpay.model.Loan;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface LoanDao {

    // apply loan
    long create(Loan loan);

    // list all loans for business
    List<Loan> findByBusinessUser(long businessUserId);

    // find single
    Optional<Loan> findById(long id);

    // approve
    void approve(long id, BigDecimal approvedAmount);

    // update outstanding
    void updateOutstanding(long id, BigDecimal amount);

    // update status
    void updateStatus(long id, String status);
}
