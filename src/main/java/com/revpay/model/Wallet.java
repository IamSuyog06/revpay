package com.revpay.model;

import java.math.BigDecimal;
import java.time.Instant;

public class Wallet {

    private long id;
    private long userId;
    private String currency;
    private BigDecimal balance;
    private Instant updatedAt;

    public Wallet() {}

    public Wallet(long userId, String currency) {
        this.userId = userId;
        this.currency = currency;
        this.balance = BigDecimal.ZERO;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "Wallet{" +
                "id=" + id +
                ", userId=" + userId +
                ", currency='" + currency + '\'' +
                ", balance=" + balance +
                '}';
    }
}
