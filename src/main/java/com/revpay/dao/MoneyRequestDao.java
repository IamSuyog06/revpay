package com.revpay.dao;

import com.revpay.model.MoneyRequest;

import java.util.List;
import java.util.Optional;

public interface MoneyRequestDao {

    // create new request
    long create(MoneyRequest request);

    // requests where I must pay (incoming)
    List<MoneyRequest> findIncoming(long userId);

    // requests I created (outgoing)
    List<MoneyRequest> findOutgoing(long userId);

    // find by id
    Optional<MoneyRequest> findById(long id);

    // update status (ACCEPTED / DECLINED / CANCELLED)
    boolean updateStatus(long id, String status);
}
