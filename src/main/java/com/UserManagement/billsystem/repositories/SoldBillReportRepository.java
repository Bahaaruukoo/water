package com.UserManagement.billsystem.repositories;

import com.UserManagement.account.Entity.User;
import com.UserManagement.billsystem.entities.SoldBillReport;
import com.UserManagement.enums.AuditStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SoldBillReportRepository extends JpaRepository<SoldBillReport, Long> {
    List<SoldBillReport> findByCashierAndStatus(User cashier, AuditStatus status);
    // Fetch all reports and sort them by startDate in descending order
    List<SoldBillReport> findAllByOrderByStartDateDesc();

    Optional<SoldBillReport> findById(Long id);

    //@Query(value = "SELECT s, b FROM SoldBillReport s JOIN Bill b ON s.id = b.report  WHERE s.id = :id ")
    @Query("SELECT s FROM SoldBillReport s LEFT JOIN FETCH s.bills WHERE s.id = :id")
    SoldBillReport findByIdWithBills(@Param("id")  Long id);
}
