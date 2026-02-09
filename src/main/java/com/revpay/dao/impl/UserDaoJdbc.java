package com.revpay.dao.impl;

import com.revpay.dao.UserDao;
import com.revpay.model.User;
import com.revpay.util.DBConnection;

import java.sql.*;
import java.time.Instant;
import java.util.Optional;

public class UserDaoJdbc implements UserDao {

    // ---------------- CREATE ----------------
    @Override
    public long create(User user) {

        String sql = """
                INSERT INTO users 
                (username, full_name, email, phone, password_hash, account_type, is_locked)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, user.getUsername());
            ps.setString(2, user.getFullName());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getPhone());
            ps.setString(5, user.getPasswordHash());
            ps.setString(6, user.getAccountType());
            ps.setBoolean(7, user.isLocked());

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


    // ---------------- FIND BY ID ----------------
    @Override
    public Optional<User> findById(long id) {

        String sql = "SELECT * FROM users WHERE id = ?";

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


    // ---------------- FIND BY USERNAME ----------------
    @Override
    public Optional<User> findByUsername(String username) {

        String sql = "SELECT * FROM users WHERE username = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return Optional.of(mapRow(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return Optional.empty();
    }


    // ---------------- FIND BY EMAIL OR PHONE ----------------
    @Override
    public Optional<User> findByEmailOrPhone(String value) {

        String sql = "SELECT * FROM users WHERE email = ? OR phone = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, value);
            ps.setString(2, value);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return Optional.of(mapRow(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return Optional.empty();
    }


    // ---------------- UPDATE ----------------
    @Override
    public boolean update(User user) {

        String sql = """
                UPDATE users 
                SET username=?, full_name=?, email=?, phone=?, password_hash=?, account_type=?, is_locked=? 
                WHERE id=?
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, user.getUsername());
            ps.setString(2, user.getFullName());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getPhone());
            ps.setString(5, user.getPasswordHash());
            ps.setString(6, user.getAccountType());
            ps.setBoolean(7, user.isLocked());
            ps.setLong(8, user.getId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    // ---------------- DELETE ----------------
    @Override
    public boolean delete(long id) {

        String sql = "DELETE FROM users WHERE id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    // ---------------- RESULTSET → USER ----------------
    private User mapRow(ResultSet rs) throws SQLException {

        User user = new User();

        user.setId(rs.getLong("id"));
        user.setUsername(rs.getString("username"));
        user.setFullName(rs.getString("full_name"));
        user.setEmail(rs.getString("email"));
        user.setPhone(rs.getString("phone"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setAccountType(rs.getString("account_type"));
        user.setLocked(rs.getBoolean("is_locked"));

        Timestamp created = rs.getTimestamp("created_at");
        if (created != null) user.setCreatedAt(created.toInstant());

        Timestamp updated = rs.getTimestamp("updated_at");
        if (updated != null) user.setUpdatedAt(updated.toInstant());

        return user;
    }
}
