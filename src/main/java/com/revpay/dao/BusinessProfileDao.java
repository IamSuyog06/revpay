package com.revpay.dao;

import com.revpay.model.BusinessProfile;

import java.util.Optional;

public interface BusinessProfileDao {

    void create(BusinessProfile profile);

    Optional<BusinessProfile> findByUserId(long userId);
}
