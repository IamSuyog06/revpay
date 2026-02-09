package com.revpay.dao.impl;

import com.revpay.dao.NotificationDao;
import com.revpay.model.Notification;
import com.revpay.util.DBConnection;

import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class NotificationDaoJdbc implements NotificationDao {

    // ---------------- CREATE ----------------
    @Override
    public void create(Notification n) {

        String sql = """
                INSERT INTO notifications
                (user_id, type, payload, is_read)
                VALUES (?, ?, ?, 0)
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, n.getUserId());
            ps.setString(2, n.getType());
            ps.setString(3, n.getPayload());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    // ---------------- LIST ----------------
    @Override
    public List<Notification> findByUser(long userId) {

        String sql = """
                SELECT * FROM notifications
                WHERE user_id=?
                ORDER BY created_at DESC
                """;

        List<Notification> list = new ArrayList<>();

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, userId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                Notification n = new Notification();

                n.setId(rs.getLong("id"));
                n.setUserId(rs.getLong("user_id"));
                n.setType(rs.getString("type"));
                n.setPayload(rs.getString("payload"));
                n.setRead(rs.getBoolean("is_read"));

                Timestamp ts = rs.getTimestamp("created_at");
                if (ts != null) {
                    n.setCreatedAt(ts.toInstant());
                }

                list.add(n);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return list;
    }


    // ---------------- MARK ALL READ ----------------
    @Override
    public void markAllRead(long userId) {

        String sql = "UPDATE notifications SET is_read=1 WHERE user_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, userId);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
