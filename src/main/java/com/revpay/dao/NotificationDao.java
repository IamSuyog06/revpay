package com.revpay.dao;

import com.revpay.model.Notification;

import java.util.List;

public interface NotificationDao {

    void create(Notification notification);

    List<Notification> findByUser(long userId);

    void markAllRead(long userId);
}
