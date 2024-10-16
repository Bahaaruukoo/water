package com.UserManagement.billsystem.repositories;

import com.UserManagement.billsystem.entities.Meter;
import com.UserManagement.enums.MeterStatus;
import lombok.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MeterRepository extends JpaRepository<Meter, Long> {
    List<Meter> findByStatus(MeterStatus meterStatus);

    @Query("SELECT m FROM Meter m WHERE m.customer IS NOT NULL AND m.status = :meterStatus")
    List<Meter> findMetersByStatusAndCustomerNotNull(@Param("meterStatus") MeterStatus meterStatus);

    Optional<Meter> findByMeterNumber(String meterId);

    Page<Meter> findByMeterNumberContainingIgnoreCase(String meterNumber, Pageable pageable);

}
