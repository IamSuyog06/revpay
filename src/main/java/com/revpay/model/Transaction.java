package com.revpay.model;

import java.math.BigDecimal;
import java.time.Instant;

public class Transaction {

    private long id;
    private String txnUuid;

    private Long fromUserId;
    private Long toUserId;

    private Long walletFromId;
    private Long walletToId;

    private String type;
    private String status;

    private BigDecimal amount;
    private String currency;

    private String note;

    private Instant createdAt;

    private String fromName;

    private String toName;

    public Transaction() {
    }

    // -------- getters & setters --------

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTxnUuid() {
        return txnUuid;
    }

    public void setTxnUuid(String txnUuid) {
        this.txnUuid = txnUuid;
    }

    public Long getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(Long fromUserId) {
        this.fromUserId = fromUserId;
    }

    public Long getToUserId() {
        return toUserId;
    }

    public void setToUserId(Long toUserId) {
        this.toUserId = toUserId;
    }

    public Long getWalletFromId() {
        return walletFromId;
    }

    public void setWalletFromId(Long walletFromId) {
        this.walletFromId = walletFromId;
    }

    public Long getWalletToId() {
        return walletToId;
    }

    public void setWalletToId(Long walletToId) {
        this.walletToId = walletToId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {

        String from = fromName != null ? fromName : "-";
        String to = toName != null ? toName : "-";

        return type +
                " | ₹" + amount +
                " | " + from +
                " → " + to +
                " | " + status +
                " | " + createdAt;
    }

    public String getFromName() { return fromName; }

    public void setFromName(String fromName) { this.fromName = fromName; }

    public String getToName() { return toName; }

    public void setToName(String toName) { this.toName = toName; }
}
