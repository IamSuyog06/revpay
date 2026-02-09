package com.revpay.service;

import com.revpay.dao.LoanDao;
import com.revpay.dao.impl.LoanDaoJdbc;
import com.revpay.model.Loan;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class LoanService {

    private final LoanDao dao = new LoanDaoJdbc();
    private final WalletService walletService = new WalletService();
    private final TransactionService transactionService = new TransactionService();


    // ---------------- APPLY ----------------
    public long apply(long businessUserId, BigDecimal amount) {

        Loan l = new Loan();

        l.setBusinessUserId(businessUserId);
        l.setApplicationUuid(UUID.randomUUID().toString());

        l.setRequestedAmount(amount);
        l.setApprovedAmount(BigDecimal.ZERO);
        l.setOutstandingAmount(BigDecimal.ZERO);

        l.setStatus("APPLIED");

        return dao.create(l);
    }


    // ---------------- LIST ----------------
    public List<Loan> list(long businessUserId) {
        return dao.findByBusinessUser(businessUserId);
    }


    // ---------------- APPROVE ----------------
    public boolean approve(long loanId) {

        Optional<Loan> opt = dao.findById(loanId);
        if (opt.isEmpty()) return false;

        Loan l = opt.get();

        if (!"APPLIED".equals(l.getStatus()))
            return false;

        BigDecimal approved = l.getRequestedAmount();

        // update DB
        dao.approve(loanId, approved);

        // credit wallet
        walletService.addMoney(l.getBusinessUserId(), approved);

        // record transaction
        transactionService.recordTopup(l.getBusinessUserId(), approved);

        return true;
    }


    // ---------------- REPAY ----------------
    public boolean repay(long loanId, long userId, BigDecimal amount) {

        Optional<Loan> opt = dao.findById(loanId);
        if (opt.isEmpty()) return false;

        Loan l = opt.get();

        if (!"APPROVED".equals(l.getStatus()))
            return false;

        BigDecimal newOutstanding = l.getOutstandingAmount().subtract(amount);

        if (newOutstanding.compareTo(BigDecimal.ZERO) < 0)
            return false;

        // deduct wallet
        walletService.withdraw(userId, amount);

        dao.updateOutstanding(loanId, newOutstanding);

        if (newOutstanding.compareTo(BigDecimal.ZERO) == 0) {
            dao.updateStatus(loanId, "REPAID");
        }

        return true;
    }
}
