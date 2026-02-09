package com.revpay.service;

import com.revpay.dao.BusinessProfileDao;
import com.revpay.dao.impl.BusinessProfileDaoJdbc;
import com.revpay.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class BusinessProfileService {

    private final BusinessProfileDao dao = new BusinessProfileDaoJdbc();


    public void upgradeToBusiness(long userId, String name, String type) {

        dao.create(new com.revpay.model.BusinessProfile() {{
            setUserId(userId);
            setBusinessName(name);
            setBusinessType(type);
        }});

        // update account type
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "UPDATE users SET account_type='BUSINESS' WHERE id=?")) {

            ps.setLong(1, userId);
            ps.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
