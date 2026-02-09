package com.revpay.service;

import com.revpay.dao.PaymentMethodDao;
import com.revpay.dao.impl.PaymentMethodDaoJdbc;
import com.revpay.model.PaymentMethod;

import java.math.BigDecimal;
import java.util.List;

public class PaymentMethodService {

    private final PaymentMethodDao dao = new PaymentMethodDaoJdbc();
    private final WalletService walletService = new WalletService();
    private final TransactionService transactionService = new TransactionService();


    // ---------------- ADD CARD ----------------
    public long addCard(long userId, String provider, String cardNumber) {

        PaymentMethod m = new PaymentMethod();

        m.setUserId(userId);
        m.setType("CARD");
        m.setProvider(provider);

        // store only last 4 for display
        String last4 = cardNumber.substring(cardNumber.length() - 4);
        m.setLast4(last4);

        m.setDefault(true);
        m.setActive(true);

        return dao.create(m);
    }


    // ---------------- LIST METHODS ----------------
    public List<PaymentMethod> list(long userId) {
        return dao.findByUserId(userId);
    }


    // ---------------- TOPUP ----------------
    public boolean topup(long userId, long methodId, BigDecimal amount) {

        // check method exists
        PaymentMethod m = dao.findById(methodId).orElse(null);

        if (m == null || m.getUserId() != userId) {
            return false;
        }

        // add money to wallet
        walletService.addMoney(userId, amount);

        // record transaction
        transactionService.recordTopup(userId, amount);

        return true;
    }
}
