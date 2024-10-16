package com.UserManagement.billsystem.repositories;

import com.UserManagement.billsystem.entities.MeterReading;
import com.UserManagement.enums.BillGenerationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MeterReadingRepository extends JpaRepository<MeterReading, Long> {
    List<MeterReading> findByBillGenerationStatus(BillGenerationStatus status);


    //@Query(value = "SELECT * FROM meter_reading WHERE meter_id = :meterId AND reading_date < :readingDateTime ORDER BY reading_date DESC LIMIT 1;")
    //MeterReading findPreviousReading(Long meterId, LocalDateTime readingDateTime);

    @Query(value = "SELECT * FROM meter_reading WHERE meter_id = :meterId AND reading_date < :readingDate ORDER BY reading_date DESC LIMIT 1", nativeQuery = true)
    MeterReading findPreviousReading(@Param("meterId") Long meterId, @Param("readingDate") LocalDateTime readingDate);

    Page<MeterReading> findAll(Specification<MeterReading> spec, Pageable pageable);
}
