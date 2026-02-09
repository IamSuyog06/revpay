package com.revpay.dao.impl;

import com.revpay.dao.TransactionDao;
import com.revpay.model.Transaction;
import com.revpay.util.DBConnection;

import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class TransactionDaoJdbc implements TransactionDao {

    // ---------------- CREATE ----------------
    @Override
    public long create(Transaction txn) {

        String sql = """
                INSERT INTO transactions
                (txn_uuid, from_user_id, to_user_id, wallet_from_id, wallet_to_id,
                 type, status, amount, currency, note)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, txn.getTxnUuid());
            ps.setObject(2, txn.getFromUserId());
            ps.setObject(3, txn.getToUserId());
            ps.setObject(4, txn.getWalletFromId());
            ps.setObject(5, txn.getWalletToId());
            ps.setString(6, txn.getType());
            ps.setString(7, txn.getStatus());
            ps.setBigDecimal(8, txn.getAmount());
            ps.setString(9, txn.getCurrency());
            ps.setString(10, txn.getNote());

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


    // ---------------- FIND HISTORY ----------------
    @Override
    public List<Transaction> findByUserId(long userId) {

        String sql = """
                SELECT * FROM transactions
                WHERE from_user_id = ? OR to_user_id = ?
                ORDER BY created_at DESC
                """;

        List<Transaction> list = new ArrayList<>();

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, userId);
            ps.setLong(2, userId);

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
    private Transaction mapRow(ResultSet rs) throws SQLException {

        Transaction t = new Transaction();

        t.setId(rs.getLong("id"));
        t.setTxnUuid(rs.getString("txn_uuid"));

        Long fromUser = rs.getLong("from_user_id");
        if (rs.wasNull()) fromUser = null;

        Long toUser = rs.getLong("to_user_id");
        if (rs.wasNull()) toUser = null;

        Long walletFrom = rs.getLong("wallet_from_id");
        if (rs.wasNull()) walletFrom = null;

        Long walletTo = rs.getLong("wallet_to_id");
        if (rs.wasNull()) walletTo = null;

        t.setFromUserId(fromUser);
        t.setToUserId(toUser);

        t.setWalletFromId(walletFrom);
        t.setWalletToId(walletTo);

        t.setType(rs.getString("type"));
        t.setStatus(rs.getString("status"));
        t.setAmount(rs.getBigDecimal("amount"));
        t.setCurrency(rs.getString("currency"));
        t.setNote(rs.getString("note"));

        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) {
            t.setCreatedAt(ts.toInstant());
        }

        return t;
    }

}
