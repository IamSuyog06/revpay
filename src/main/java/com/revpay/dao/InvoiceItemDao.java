package com.revpay.dao;

import com.revpay.model.InvoiceItem;

import java.util.List;

public interface InvoiceItemDao {

    long create(InvoiceItem item);

    List<InvoiceItem> findByInvoiceId(long invoiceId);
}
