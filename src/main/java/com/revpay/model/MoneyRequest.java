package com.revpay.model;

import java.math.BigDecimal;
import java.time.Instant;

public class MoneyRequest {

    private long id;
    private String requestUuid;

    private long fromUserId; // requester
    private long toUserId;   // payer

    private BigDecimal amount;
    private String currency;

    private String status; // PENDING, ACCEPTED, DECLINED, CANCELLED
    private String note;

    private Instant createdAt;
    private Instant actedAt;

    public MoneyRequest() {
    }

    // -------- getters & setters --------

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getRequestUuid() {
        return requestUuid;
    }

    public void setRequestUuid(String requestUuid) {
        this.requestUuid = requestUuid;
    }

    public long getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(long fromUserId) {
        this.fromUserId = fromUserId;
    }

    public long getToUserId() {
        return toUserId;
    }

    public void setToUserId(long toUserId) {
        this.toUserId = toUserId;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public Instant getActedAt() {
        return actedAt;
    }

    public void setActedAt(Instant actedAt) {
        this.actedAt = actedAt;
    }

    @Override
    public String toString() {
        return "MoneyRequest{" +
                "uuid='" + requestUuid + '\'' +
                ", from=" + fromUserId +
                ", to=" + toUserId +
                ", amount=" + amount +
                ", status='" + status + '\'' +
                '}';
    }
}
