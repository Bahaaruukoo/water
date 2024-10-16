package com.UserManagement.billsystem.repositories;


import com.UserManagement.billsystem.entities.Customer;
import com.UserManagement.enums.ServiceStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    List<Customer> findByServiceStatus(String serviceStatus);

    // Method to find by phone number, meter number, and status

    // Method to find by phone number, meter number, and status
    @Query("SELECT c FROM Customer c WHERE "
            + "(:phoneNumber IS NULL OR c.phoneNumber LIKE %:phoneNumber%) AND "
            + "(:meterNumber IS NULL OR c.meter.meterNumber LIKE %:meterNumber%) AND "
            + "(:status IS NULL OR c.serviceStatus = :status)")
    Page<Customer> findFilteredCustomers(
            @Param("phoneNumber") String phoneNumber,
            @Param("meterNumber") String meterNumber,
            @Param("status") ServiceStatus status,
            Pageable pageable);

    Optional<Customer> findByPhoneNumber(String phoneNumber);
    Optional<Customer> findByBillNumber( String billNum);
}
