package com.revpay.dao.impl;

import com.revpay.dao.WalletDao;
import com.revpay.model.Wallet;
import com.revpay.util.DBConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.time.Instant;
import java.util.Optional;

public class WalletDaoJdbc implements WalletDao {

    // ---------------- CREATE ----------------
    @Override
    public long create(Wallet wallet) {

        String sql = """
                INSERT INTO wallets (user_id, currency, balance)
                VALUES (?, ?, ?)
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setLong(1, wallet.getUserId());
            ps.setString(2, wallet.getCurrency());
            ps.setBigDecimal(3, wallet.getBalance());

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


    // ---------------- FIND BY USER ID ----------------
    @Override
    public Optional<Wallet> findByUserId(long userId) {

        String sql = "SELECT * FROM wallets WHERE user_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, userId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return Optional.of(mapRow(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return Optional.empty();
    }


    // ---------------- FIND BY ID ----------------
    @Override
    public Optional<Wallet> findById(long id) {

        String sql = "SELECT * FROM wallets WHERE id = ?";

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


    // ---------------- UPDATE BALANCE ----------------
    @Override
    public boolean updateBalance(long walletId, BigDecimal newBalance) {

        String sql = "UPDATE wallets SET balance = ? WHERE id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setBigDecimal(1, newBalance);
            ps.setLong(2, walletId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    // ---------------- MAP RESULTSET ----------------
    private Wallet mapRow(ResultSet rs) throws SQLException {

        Wallet wallet = new Wallet();

        wallet.setId(rs.getLong("id"));
        wallet.setUserId(rs.getLong("user_id"));
        wallet.setCurrency(rs.getString("currency"));
        wallet.setBalance(rs.getBigDecimal("balance"));

        Timestamp ts = rs.getTimestamp("updated_at");
        if (ts != null) {
            wallet.setUpdatedAt(ts.toInstant());
        }

        return wallet;
    }
}
