package com.UserManagement.billsystem.repositories;

import com.UserManagement.billsystem.entities.Pricing;
import com.UserManagement.enums.CustomerCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PricingRepository extends JpaRepository<Pricing, Long> {
    Pricing findByCustomerCategory(CustomerCategory category);
}
