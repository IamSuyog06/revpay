package com.revpay.dao.impl;

import com.revpay.dao.InvoiceItemDao;
import com.revpay.model.InvoiceItem;
import com.revpay.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InvoiceItemDaoJdbc implements InvoiceItemDao {

    @Override
    public long create(InvoiceItem item) {

        String sql = """
                INSERT INTO invoice_items
                (invoice_id, descriptionn, quantity, unit_price, line_total)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setLong(1, item.getInvoiceId());
            ps.setString(2, item.getDescription());
            ps.setInt(3, item.getQuantity());
            ps.setBigDecimal(4, item.getUnitPrice());
            ps.setBigDecimal(5, item.getLineTotal());

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


    @Override
    public List<InvoiceItem> findByInvoiceId(long invoiceId) {

        String sql = "SELECT * FROM invoice_items WHERE invoice_id=?";

        List<InvoiceItem> list = new ArrayList<>();

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, invoiceId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                InvoiceItem item = new InvoiceItem();

                item.setId(rs.getLong("id"));
                item.setInvoiceId(rs.getLong("invoice_id"));
                item.setDescription(rs.getString("descriptionn"));
                item.setQuantity(rs.getInt("quantity"));
                item.setUnitPrice(rs.getBigDecimal("unit_price"));
                item.setLineTotal(rs.getBigDecimal("line_total"));

                list.add(item);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return list;
    }
}
