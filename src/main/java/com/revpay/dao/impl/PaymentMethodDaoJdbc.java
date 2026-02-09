package com.revpay.dao.impl;

import com.revpay.dao.PaymentMethodDao;
import com.revpay.model.PaymentMethod;
import com.revpay.util.DBConnection;

import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PaymentMethodDaoJdbc implements PaymentMethodDao {

    // ---------------- CREATE ----------------
    @Override
    public long create(PaymentMethod m) {

        String sql = """
                INSERT INTO payment_methods
                (user_id, type, provider, last4,encrypted_data, is_default, is_active)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setLong(1, m.getUserId());
            ps.setString(2, m.getType());
            ps.setString(3, m.getProvider());
            ps.setString(4, m.getLast4());
            // dummy encrypted data (empty)
            ps.setBytes(5, new byte[0]);
            ps.setBoolean(6, m.isDefault());
            ps.setBoolean(7, m.isActive());

            ps.executeUpdate();

            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                return keys.getLong(1);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return -1;
    }


    // ---------------- FIND BY USER ----------------
    @Override
    public List<PaymentMethod> findByUserId(long userId) {

        String sql = "SELECT * FROM payment_methods WHERE user_id=? AND is_active=1";

        List<PaymentMethod> list = new ArrayList<>();

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, userId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(mapRow(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return list;
    }


    // ---------------- FIND BY ID ----------------
    @Override
    public Optional<PaymentMethod> findById(long id) {

        String sql = "SELECT * FROM payment_methods WHERE id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return Optional.of(mapRow(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return Optional.empty();
    }


    // ---------------- MAP RESULTSET ----------------
    private PaymentMethod mapRow(ResultSet rs) throws SQLException {

        PaymentMethod m = new PaymentMethod();

        m.setId(rs.getLong("id"));
        m.setUserId(rs.getLong("user_id"));
        m.setType(rs.getString("type"));
        m.setProvider(rs.getString("provider"));
        m.setLast4(rs.getString("last4"));
        m.setDefault(rs.getBoolean("is_default"));
        m.setActive(rs.getBoolean("is_active"));

        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) {
            m.setCreatedAt(ts.toInstant());
        }

        return m;
    }
}
