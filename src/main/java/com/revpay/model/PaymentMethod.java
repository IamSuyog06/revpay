package com.revpay.model;

import java.time.Instant;

public class PaymentMethod {

    private long id;
    private long userId;

    private String type;      // CARD or BANK_ACCOUNT
    private String provider;  // HDFC, ICICI etc
    private String last4;

    private boolean isDefault;
    private boolean isActive;

    private Instant createdAt;

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getLast4() {
        return last4;
    }

    public void setLast4(String last4) {
        this.last4 = last4;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "PaymentMethod{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", provider='" + provider + '\'' +
                ", last4='" + last4 + '\'' +
                '}';
    }
}
