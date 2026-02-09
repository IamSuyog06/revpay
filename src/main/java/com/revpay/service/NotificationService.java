package com.revpay.service;

import com.revpay.dao.NotificationDao;
import com.revpay.dao.impl.NotificationDaoJdbc;
import com.revpay.model.Notification;

import java.util.List;

public class NotificationService {

    private final NotificationDao dao = new NotificationDaoJdbc();


    // ---------------- CREATE ----------------
    public void notify(long userId, String type, String message) {

        Notification n = new Notification();

        n.setUserId(userId);
        n.setType(type);
        n.setPayload(message);

        dao.create(n);
    }


    // ---------------- LIST ----------------
    public List<Notification> list(long userId) {
        return dao.findByUser(userId);
    }


    // ---------------- MARK READ ----------------
    public void markAllRead(long userId) {
        dao.markAllRead(userId);
    }
}
