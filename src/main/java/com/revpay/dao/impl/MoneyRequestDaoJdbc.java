package com.revpay.dao.impl;

import com.revpay.dao.MoneyRequestDao;
import com.revpay.model.MoneyRequest;
import com.revpay.util.DBConnection;

import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MoneyRequestDaoJdbc implements MoneyRequestDao {

    // ---------------- CREATE ----------------
    @Override
    public long create(MoneyRequest r) {

        String sql = """
                INSERT INTO money_requests
                (request_uuid, from_user_id, to_user_id, amount, currency, status, note)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, r.getRequestUuid());
            ps.setLong(2, r.getFromUserId());
            ps.setLong(3, r.getToUserId());
            ps.setBigDecimal(4, r.getAmount());
            ps.setString(5, r.getCurrency());
            ps.setString(6, r.getStatus());
            ps.setString(7, r.getNote());

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


    // ---------------- INCOMING ----------------
    @Override
    public List<MoneyRequest> findIncoming(long userId) {

        String sql = "SELECT * FROM money_requests WHERE to_user_id = ? ORDER BY created_at DESC";

        return findMany(sql, userId);
    }


    // ---------------- OUTGOING ----------------
    @Override
    public List<MoneyRequest> findOutgoing(long userId) {

        String sql = "SELECT * FROM money_requests WHERE from_user_id = ? ORDER BY created_at DESC";

        return findMany(sql, userId);
    }


    // ---------------- FIND BY ID ----------------
    @Override
    public Optional<MoneyRequest> findById(long id) {

        String sql = "SELECT * FROM money_requests WHERE id = ?";

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


    // ---------------- UPDATE STATUS ----------------
    @Override
    public boolean updateStatus(long id, String status) {

        String sql = "UPDATE money_requests SET status=?, acted_at=NOW() WHERE id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setLong(2, id);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    // ---------------- COMMON FIND ----------------
    private List<MoneyRequest> findMany(String sql, long userId) {

        List<MoneyRequest> list = new ArrayList<>();

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


    // ---------------- MAP RESULTSET ----------------
    private MoneyRequest mapRow(ResultSet rs) throws SQLException {

        MoneyRequest r = new MoneyRequest();

        r.setId(rs.getLong("id"));
        r.setRequestUuid(rs.getString("request_uuid"));
        r.setFromUserId(rs.getLong("from_user_id"));
        r.setToUserId(rs.getLong("to_user_id"));
        r.setAmount(rs.getBigDecimal("amount"));
        r.setCurrency(rs.getString("currency"));
        r.setStatus(rs.getString("status"));
        r.setNote(rs.getString("note"));

        Timestamp created = rs.getTimestamp("created_at");
        if (created != null) r.setCreatedAt(created.toInstant());

        Timestamp acted = rs.getTimestamp("acted_at");
        if (acted != null) r.setActedAt(acted.toInstant());

        return r;
    }
}
