package com.revpay.dao;

import com.revpay.model.Invoice;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface InvoiceDao {

    long create(Invoice invoice);

    Optional<Invoice> findById(long id);

    List<Invoice> findByBusinessUser(long businessUserId);

    void updateTotal(long invoiceId, BigDecimal total);

    void updateStatus(long invoiceId, String status);
}
