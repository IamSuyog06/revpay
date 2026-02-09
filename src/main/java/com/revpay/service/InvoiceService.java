package com.revpay.service;

import com.revpay.dao.InvoiceDao;
import com.revpay.dao.InvoiceItemDao;
import com.revpay.dao.impl.InvoiceDaoJdbc;
import com.revpay.dao.impl.InvoiceItemDaoJdbc;
import com.revpay.model.Invoice;
import com.revpay.model.InvoiceItem;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class InvoiceService {

    private final InvoiceDao invoiceDao = new InvoiceDaoJdbc();
    private final InvoiceItemDao itemDao = new InvoiceItemDaoJdbc();
    private final TransactionService transactionService = new TransactionService();


    // ---------------- CREATE INVOICE ----------------
    public long createInvoice(long businessUserId,
                              String customerName,
                              String customerContact,
                              LocalDate dueDate) {

        Invoice i = new Invoice();

        i.setInvoiceNumber(UUID.randomUUID().toString().substring(0, 8));
        i.setBusinessUserId(businessUserId);
        i.setCustomerName(customerName);
        i.setCustomerContact(customerContact);

        i.setTotalAmount(BigDecimal.ZERO);
        i.setCurrency("INR");
        i.setStatus("DRAFT");

        i.setIssuedAt(LocalDate.now());
        i.setDueDate(dueDate);

        return invoiceDao.create(i);
    }


    // ---------------- ADD ITEM ----------------
    public void addItem(long invoiceId,
                        String desc,
                        int qty,
                        BigDecimal price) {

        BigDecimal lineTotal = price.multiply(BigDecimal.valueOf(qty));

        InvoiceItem item = new InvoiceItem();
        item.setInvoiceId(invoiceId);
        item.setDescription(desc);
        item.setQuantity(qty);
        item.setUnitPrice(price);
        item.setLineTotal(lineTotal);

        itemDao.create(item);

        // recalc total
        List<InvoiceItem> items = itemDao.findByInvoiceId(invoiceId);

        BigDecimal total = items.stream()
                .map(InvoiceItem::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        invoiceDao.updateTotal(invoiceId, total);
    }


    // ---------------- LIST ----------------
    public List<Invoice> list(long businessUserId) {
        return invoiceDao.findByBusinessUser(businessUserId);
    }


    public List<InvoiceItem> getItems(long invoiceId) {
        return itemDao.findByInvoiceId(invoiceId);
    }


    // ---------------- PAY ----------------
    public boolean payInvoice(long invoiceId, long customerUserId) {

        Optional<Invoice> opt = invoiceDao.findById(invoiceId);

        if (opt.isEmpty()) return false;

        Invoice inv = opt.get();

        if (!"DRAFT".equals(inv.getStatus())) return false;

        boolean ok = transactionService.sendMoney(
                customerUserId,
                inv.getBusinessUserId(),
                inv.getTotalAmount()
        );

        if (!ok) return false;

        invoiceDao.updateStatus(invoiceId, "PAID");

        return true;
    }
}
