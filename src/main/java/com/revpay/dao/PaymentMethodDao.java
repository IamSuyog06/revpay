package com.revpay.dao;

import com.revpay.model.PaymentMethod;

import java.util.List;
import java.util.Optional;

public interface PaymentMethodDao {

    // add new method
    long create(PaymentMethod method);

    // list all methods of a user
    List<PaymentMethod> findByUserId(long userId);

    // find one method
    Optional<PaymentMethod> findById(long id);
}
