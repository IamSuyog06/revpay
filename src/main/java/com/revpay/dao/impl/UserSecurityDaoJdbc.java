package com.revpay.dao.impl;

import com.revpay.dao.UserSecurityDao;
import com.revpay.util.DBConnection;

import java.sql.*;
import java.util.Optional;

public class UserSecurityDaoJdbc implements UserSecurityDao {

    @Override
    public void savePin(long userId, String pinHash) {

        String sql = """
                INSERT INTO user_security (user_id, transaction_pin_hash)
                VALUES (?, ?)
                ON DUPLICATE KEY UPDATE transaction_pin_hash = VALUES(transaction_pin_hash)
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, userId);
            ps.setString(2, pinHash);

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<String> getPinHash(long userId) {

        String sql = "SELECT transaction_pin_hash FROM user_security WHERE user_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, userId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return Optional.ofNullable(rs.getString(1));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return Optional.empty();
    }
}
