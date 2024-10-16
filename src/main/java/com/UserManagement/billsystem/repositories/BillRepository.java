package com.UserManagement.billsystem.repositories;


import com.UserManagement.account.Entity.User;
import com.UserManagement.billsystem.dto.KebeleCategoryBillReportDTO;
import com.UserManagement.billsystem.entities.Bill;
import com.UserManagement.billsystem.entities.SoldBillReport;
import com.UserManagement.enums.BillPaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Repository
public interface BillRepository extends JpaRepository<Bill, Long> {
    @Query("SELECT COALESCE(SUM(b.amount), 0.0) FROM Bill b WHERE b.status = BillPaymentStatus.UNSOLD")
    double sumUnpaidBills();

    @Query("SELECT COALESCE(SUM(b.amount), 0.0) FROM Bill b WHERE b.status = BillPaymentStatus.SOLD")
    double sumCollectedAmount();


    @Query("SELECT COUNT(b) FROM Bill b WHERE b.dueDate BETWEEN CURRENT_DATE AND :endDate AND b.status = BillPaymentStatus.UNSOLD")
    double findUpcomingDueBills(@Param("endDate") LocalDate endDate);

    @Query("SELECT COUNT(b) FROM Bill b WHERE b.dueDate < CURRENT_DATE AND b.status = BillPaymentStatus.UNSOLD")
    double findOverdueBills();

    List<Bill> findByStatusAndSoldDateBetween(BillPaymentStatus sold, LocalDate startDate, LocalDate endDate);
    List<Bill> findBySoldDateBetween(LocalDate startDate, LocalDate endDate);

    List<Bill> findBySoldByAndSoldDateBetween(User cashier, LocalDate startDate, LocalDate endDate);
    List<Bill> findBySoldByAndSoldByIsNotNullAndSoldDateIsNotNullAndSoldDateBetween(User cashier, LocalDate startDate, LocalDate endDate);

    List<Bill> findByStatusAndInvoiceDateBetween(BillPaymentStatus billPaymentStatus, LocalDate startDate, LocalDate endDate);

    /*
    @Query("SELECT new com.UserManagement.billsystem.dto.KebeleCategoryBillReportDTO("
            + "c.kebele, c.customerCategory, "
            + "COUNT(b), MIN(b.amount), MAX(b.amount), "
            + "(SELECT b2.billNumber FROM Bill b2 WHERE b2.amount = MAX(b.amount)), "
            + "SUM(b.meterRent), SUM(b.totalBill), SUM(b.serviceChange), SUM(b.penalty), "
            + "(SUM(b.meterRent) + SUM(b.totalBill) + SUM(b.serviceChange) + SUM(b.penalty))"
            + ") "
            + "FROM Bill b "
            + "JOIN b.customer c "
            + "GROUP BY c.kebele, c.customerCategory"
    ) */
@Query("SELECT new com.UserManagement.billsystem.dto.KebeleCategoryBillReportDTO("
        + "c.kebele, c.customerCategory, "
        + "COUNT(b), MIN(b.amount), MAX(b.amount), "
        + "(SELECT MIN(b2.billNumber) FROM Bill b2 WHERE b2.amount = MAX(b.amount)), "
        + "SUM(b.meterRent), SUM(b.amount), SUM(b.serviceCharge), SUM(b.penalty), "
        + "(SUM(b.meterRent) + SUM(b.totalBill) + SUM(b.serviceCharge) + SUM(b.penalty))"
        + ") "
        + "FROM Bill b "
        + "JOIN b.customer c "
        + "WHERE b.status != com.UserManagement.enums.BillPaymentStatus.VOIDED "
        + "GROUP BY c.kebele, c.customerCategory"
)
List<KebeleCategoryBillReportDTO> getGroupedBillReport();

    // Method to find bills by bill number and status
    Page<Bill> findByBillNumberContainingAndStatus(String billNumber, BillPaymentStatus status, Pageable pageable);

    // Method to find bills by bill number only (when status is not provided)
    Page<Bill> findByBillNumberContaining(String billNumber, Pageable pageable);

    // Method to find bills by status only (when bill number is not provided)
    Page<Bill> findByStatus(BillPaymentStatus status, Pageable pageable);

    // Method to get all bills without filtering
    Page<Bill> findAll(Pageable pageable);

    @Query("SELECT b FROM Bill b WHERE YEAR(b.invoiceDate) = :year and b.status = :status")
    List<Bill> findByPaymentStatusAndYear(@Param("status") BillPaymentStatus status, @Param("year") int year);

    List<Bill> findByReport(SoldBillReport report);

    List<Bill> findBySoldByAndSoldByIsNotNullAndReportIsNullAndSoldDateIsNotNullAndSoldDateBetween(User cashier, LocalDate startDate, LocalDate endDate);

    List<Bill> findBySoldByAndSoldByIsNotNullAndReportIsNullAndSoldDateIsNotNull(User user);
}

