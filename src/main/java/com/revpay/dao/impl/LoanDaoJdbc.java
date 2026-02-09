package com.revpay.dao.impl;

import com.revpay.dao.LoanDao;
import com.revpay.model.Loan;
import com.revpay.util.DBConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LoanDaoJdbc implements LoanDao {

    // ---------------- CREATE (APPLY) ----------------
    @Override
    public long create(Loan l) {

        String sql = """
                INSERT INTO loans
                (business_user_id, application_uuid, requested_amount,
                 approved_amount, status, outstanding_amount)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setLong(1, l.getBusinessUserId());
            ps.setString(2, l.getApplicationUuid());
            ps.setBigDecimal(3, l.getRequestedAmount());

            ps.setBigDecimal(4, l.getApprovedAmount());
            ps.setString(5, l.getStatus());
            ps.setBigDecimal(6, l.getOutstandingAmount());

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


    // ---------------- LIST ----------------
    @Override
    public List<Loan> findByBusinessUser(long businessUserId) {

        String sql = "SELECT * FROM loans WHERE business_user_id=? ORDER BY created_at DESC";

        List<Loan> list = new ArrayList<>();

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, businessUserId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(mapRow(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return list;
    }


    // ---------------- FIND ONE ----------------
    @Override
    public Optional<Loan> findById(long id) {

        String sql = "SELECT * FROM loans WHERE id=?";

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


    // ---------------- APPROVE ----------------
    @Override
    public void approve(long id, BigDecimal approvedAmount) {

        String sql = """
                UPDATE loans
                SET approved_amount=?,
                    outstanding_amount=?,
                    status='APPROVED'
                WHERE id=?
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setBigDecimal(1, approvedAmount);
            ps.setBigDecimal(2, approvedAmount);
            ps.setLong(3, id);

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    // ---------------- UPDATE OUTSTANDING ----------------
    @Override
    public void updateOutstanding(long id, BigDecimal amount) {

        String sql = "UPDATE loans SET outstanding_amount=? WHERE id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setBigDecimal(1, amount);
            ps.setLong(2, id);

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    // ---------------- UPDATE STATUS ----------------
    @Override
    public void updateStatus(long id, String status) {

        String sql = "UPDATE loans SET status=? WHERE id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setLong(2, id);

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    // ---------------- MAP ----------------
    private Loan mapRow(ResultSet rs) throws SQLException {

        Loan l = new Loan();

        l.setId(rs.getLong("id"));
        l.setBusinessUserId(rs.getLong("business_user_id"));
        l.setApplicationUuid(rs.getString("application_uuid"));
        l.setRequestedAmount(rs.getBigDecimal("requested_amount"));
        l.setApprovedAmount(rs.getBigDecimal("approved_amount"));
        l.setOutstandingAmount(rs.getBigDecimal("outstanding_amount"));
        l.setStatus(rs.getString("status"));

        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) {
            l.setCreatedAt(ts.toInstant());
        }

        return l;
    }
}
