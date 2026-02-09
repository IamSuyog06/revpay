package com.revpay.service;

import com.revpay.dao.MoneyRequestDao;
import com.revpay.dao.impl.MoneyRequestDaoJdbc;
import com.revpay.model.MoneyRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class MoneyRequestService {

    private final MoneyRequestDao requestDao = new MoneyRequestDaoJdbc();
    private final TransactionService transactionService = new TransactionService();


    // ---------------- CREATE REQUEST ----------------
    public long createRequest(long fromUserId,
                              long toUserId,
                              BigDecimal amount,
                              String note) {

        MoneyRequest r = new MoneyRequest();

        r.setRequestUuid(UUID.randomUUID().toString());
        r.setFromUserId(fromUserId);
        r.setToUserId(toUserId);
        r.setAmount(amount);
        r.setCurrency("INR");
        r.setStatus("PENDING");
        r.setNote(note);

        return requestDao.create(r);
    }


    // ---------------- ACCEPT ----------------
    public boolean acceptRequest(long requestId, long actingUserId) {

        Optional<MoneyRequest> opt = requestDao.findById(requestId);

        if (opt.isEmpty()) return false;

        MoneyRequest r = opt.get();

        System.out.println("DEBUG: actingUser=" + actingUserId);
        System.out.println("DEBUG: request.toUser=" + r.getToUserId());
        System.out.println("DEBUG: status=" + r.getStatus());
        // only receiver can accept
        if (r.getToUserId() != actingUserId) return false;

        if (!"PENDING".equals(r.getStatus())) return false;

        // transfer money
        boolean ok = transactionService.sendMoney(
                r.getToUserId(),   // payer
                r.getFromUserId(), // requester
                r.getAmount()
        );

        if (!ok) return false;

        requestDao.updateStatus(requestId, "ACCEPTED");

        return true;
    }


    // ---------------- DECLINE ----------------
    public boolean declineRequest(long requestId, long actingUserId) {

        Optional<MoneyRequest> opt = requestDao.findById(requestId);

        if (opt.isEmpty()) return false;

        MoneyRequest r = opt.get();

        if (r.getToUserId() != actingUserId) return false;

        if (!"PENDING".equals(r.getStatus())) return false;

        return requestDao.updateStatus(requestId, "DECLINED");
    }


    // ---------------- LIST ----------------
    public List<MoneyRequest> getIncoming(long userId) {
        return requestDao.findIncoming(userId);
    }

    public List<MoneyRequest> getOutgoing(long userId) {
        return requestDao.findOutgoing(userId);
    }


}
