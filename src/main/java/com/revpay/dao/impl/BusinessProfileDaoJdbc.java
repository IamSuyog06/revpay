package com.revpay.dao.impl;

import com.revpay.dao.BusinessProfileDao;
import com.revpay.model.BusinessProfile;
import com.revpay.util.DBConnection;

import java.sql.*;
import java.util.Optional;

public class BusinessProfileDaoJdbc implements BusinessProfileDao {

    @Override
    public void create(BusinessProfile p) {

        String sql = """
                INSERT INTO business_profiles (user_id, business_name, business_type)
                VALUES (?, ?, ?)
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, p.getUserId());
            ps.setString(2, p.getBusinessName());
            ps.setString(3, p.getBusinessType());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<BusinessProfile> findByUserId(long userId) {

        String sql = "SELECT * FROM business_profiles WHERE user_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, userId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                BusinessProfile p = new BusinessProfile();
                p.setUserId(userId);
                p.setBusinessName(rs.getString("business_name"));
                p.setBusinessType(rs.getString("business_type"));
                return Optional.of(p);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return Optional.empty();
    }
}
