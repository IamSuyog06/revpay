package com.revpay.dao.impl;

import com.revpay.dao.InvoiceDao;
import com.revpay.model.Invoice;
import com.revpay.util.DBConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InvoiceDaoJdbc implements InvoiceDao {

    // ---------------- CREATE ----------------
    @Override
    public long create(Invoice i) {

        String sql = """
                INSERT INTO invoices
                (invoice_number, business_user_id, customer_name, customer_contact,
                 total_amount, currency, status, issued_at, due_date)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, i.getInvoiceNumber());
            ps.setLong(2, i.getBusinessUserId());
            ps.setString(3, i.getCustomerName());
            ps.setString(4, i.getCustomerContact());
            ps.setBigDecimal(5, i.getTotalAmount());
            ps.setString(6, i.getCurrency());
            ps.setString(7, i.getStatus());
            ps.setDate(8, Date.valueOf(i.getIssuedAt()));
            ps.setDate(9, Date.valueOf(i.getDueDate()));

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
    public Optional<Invoice> findById(long id) {

        String sql = "SELECT * FROM invoices WHERE id=?";

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


    // ---------------- LIST BY BUSINESS ----------------
    @Override
    public List<Invoice> findByBusinessUser(long businessUserId) {

        String sql = "SELECT * FROM invoices WHERE business_user_id=? ORDER BY created_at DESC";

        List<Invoice> list = new ArrayList<>();

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


    // ---------------- UPDATE TOTAL ----------------
    @Override
    public void updateTotal(long invoiceId, BigDecimal total) {

        String sql = "UPDATE invoices SET total_amount=? WHERE id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setBigDecimal(1, total);
            ps.setLong(2, invoiceId);

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    // ---------------- UPDATE STATUS ----------------
    @Override
    public void updateStatus(long invoiceId, String status) {

        String sql = "UPDATE invoices SET status=?, paid_at=NOW() WHERE id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setLong(2, invoiceId);

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    // ---------------- MAP ----------------
    private Invoice mapRow(ResultSet rs) throws SQLException {

        Invoice i = new Invoice();

        i.setId(rs.getLong("id"));
        i.setInvoiceNumber(rs.getString("invoice_number"));
        i.setBusinessUserId(rs.getLong("business_user_id"));
        i.setCustomerName(rs.getString("customer_name"));
        i.setCustomerContact(rs.getString("customer_contact"));
        i.setTotalAmount(rs.getBigDecimal("total_amount"));
        i.setCurrency(rs.getString("currency"));
        i.setStatus(rs.getString("status"));

        i.setIssuedAt(rs.getDate("issued_at").toLocalDate());
        i.setDueDate(rs.getDate("due_date").toLocalDate());

        return i;
    }
}
