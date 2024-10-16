package com.UserManagement.billsystem.services;

import com.UserManagement.account.Entity.Role;
import com.UserManagement.account.Entity.User;
import com.UserManagement.account.Repository.UserRepository;
import com.UserManagement.billsystem.entities.Bill;
import com.UserManagement.billsystem.entities.SoldBillReport;
import com.UserManagement.billsystem.repositories.BillRepository;
import com.UserManagement.billsystem.repositories.SoldBillReportRepository;
import com.UserManagement.enums.AuditStatus;
import com.UserManagement.enums.BillPaymentStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

@Service
public class SoldBillReportService {

    @Autowired
    private SoldBillReportRepository soldBillReportRepository;

    @Autowired
    private BillRepository billRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Scheduled(cron = "0 0 0 5,6 * *")
    public void autoSaleReportGenerator(){
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now();
        //get all Cashiers
        //check if a cashier has collected any bill
        //generate a report for a cashier and period
        //mark all bills included in the report
        List<User> users = userRepository.findAll();
        for (User user : users){
            for (Role role : user.getRoles() ){
                if (role.getName().equals("ROLE_CASHIER")){
                    //check if the cashier has any sale in the period
                    List<Bill> bills = billRepository.findBySoldByAndSoldByIsNotNullAndReportIsNullAndSoldDateIsNotNull(user);
                    if( bills.size() > 0){
                        // generate report for the cashier
                        generateReport(user, startDate, endDate, bills);
                    }
                }
            }
        }
    }

    public SoldBillReport createReport (User cashier, LocalDate startDate, LocalDate endDate){
        List<Bill> bills = billRepository.findBySoldByAndSoldByIsNotNullAndReportIsNullAndSoldDateIsNotNullAndSoldDateBetween( cashier,  startDate,  endDate);

        return generateReport( cashier, startDate, endDate, bills);
    }


    private SoldBillReport  generateReport(User cashier, LocalDate startDate, LocalDate endDate, List<Bill> bills) {
        SoldBillReport report = new SoldBillReport();
        report.setCashier(cashier);
        report.setStartDate(startDate);
        report.setEndDate(endDate);
        report.setStatus(AuditStatus.SUBMITTED);

        // Fetch all bills marked as SOLD within the period by the cashier and calculate totals
       // List<Bill> bills = billRepository.findBySoldByAndSoldDateBetween( cashier,  startDate, endDate);
   //     List<Bill> bills = billRepository.findBySoldByAndSoldByIsNotNullAndReportIsNullAndSoldDateIsNotNullAndSoldDateBetween( cashier,  startDate,  endDate);


        // Calculate totals
        double totalRental = bills.stream().mapToDouble(Bill::getMeterRent).sum();
        double totalAmount = bills.stream().mapToDouble(Bill::getAmount).sum();
        double totalInterest = bills.stream().mapToDouble(Bill::getInterest).sum();
        double totalPenalty = bills.stream().mapToDouble(Bill::getPenalty).sum();
        double totalCollection = bills.stream().mapToDouble(Bill::getTotalBill).sum();

        report.setTotalInterest(totalInterest);
        report.setTotalPenalty(totalPenalty);
        report.setTotalMeterRental(totalRental);
        report.setTotalCollection(totalCollection);
        report.setConsumptionAmount(totalAmount);

       // System.out.println(soldBillReport.toString());
        for (Bill bill: bills){
            bill.setReport(report );
        }
        //SoldBillReport soldBillReport = soldBillReportRepository.save(report);
        report.setBills(bills);
        return soldBillReportRepository.save(report);
        //billRepository.saveAll(bills);
        //return soldBillReport;
    }


    public SoldBillReport findReport(Long reportId, User auditor) {
        return soldBillReportRepository.findById(reportId).orElseThrow();
    }

    public SoldBillReport rejectReport(Long reportId, User auditor) {
        SoldBillReport report = soldBillReportRepository.findById(reportId).orElseThrow();
        report.setAuditor(auditor);
        report.setStatus(AuditStatus.REJECTED);
        report.setAuditedDate(LocalDate.now());
        for(Bill bill: report.getBills()){
            bill.setReport(null);
        }
        return soldBillReportRepository.save(report);
    }

    public SoldBillReport approveReport(Long id, User auditor) {
        SoldBillReport report = soldBillReportRepository.findById(id).orElseThrow();
        report.setAuditor(auditor);
        report.setAuditedDate(LocalDate.now());
        report.setStatus(AuditStatus.AUDITED);
        return soldBillReportRepository.save(report);
    }

    public List<SoldBillReport> findAllReports() {
        return soldBillReportRepository.findAllByOrderByStartDateDesc();//.findAll();
    }

    @Transactional
    public SoldBillReport findById(Long id) {
        return soldBillReportRepository.findById(id).orElseThrow();
        //return soldBillReportRepository.findByIdWithBills(id);
    }

    public List<SoldBillReport> filterReports(AuditStatus status, LocalDate endDate, Double minCollection, Double maxCollection) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<SoldBillReport> criteriaQuery = criteriaBuilder.createQuery(SoldBillReport.class);
        Root<SoldBillReport> root = criteriaQuery.from(SoldBillReport.class);

        // Build the conditions list
        var predicates = criteriaBuilder.conjunction(); // Start with a conjunction (AND)

        if (status != null) {
            predicates = criteriaBuilder.and(predicates, criteriaBuilder.equal(root.get("status"), status));
        }

        if (endDate != null) {
            predicates = criteriaBuilder.and(predicates, criteriaBuilder.equal(root.get("endDate"), endDate));
        }

        if (minCollection != null) {
            predicates = criteriaBuilder.and(predicates, criteriaBuilder.greaterThanOrEqualTo(root.get("totalCollection"), minCollection));
        }

        if (maxCollection != null) {
            predicates = criteriaBuilder.and(predicates, criteriaBuilder.lessThanOrEqualTo(root.get("totalCollection"), maxCollection));
        }


        criteriaQuery.where(predicates); // Apply all conditions

        // Executing the query
        TypedQuery<SoldBillReport> query = entityManager.createQuery(criteriaQuery);
        return query.getResultList();
    }
    public List<SoldBillReport> filterReportsForAUserLogedIn(AuditStatus status, LocalDate endDate, Double minCollection, Double maxCollection, User user) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<SoldBillReport> criteriaQuery = criteriaBuilder.createQuery(SoldBillReport.class);
        Root<SoldBillReport> root = criteriaQuery.from(SoldBillReport.class);

        // Build the conditions list
        var predicates = criteriaBuilder.conjunction(); // Start with a conjunction (AND)

        if (status != null) {
            predicates = criteriaBuilder.and(predicates, criteriaBuilder.equal(root.get("status"), status));
        }

        if (endDate != null) {
            predicates = criteriaBuilder.and(predicates, criteriaBuilder.equal(root.get("endDate"), endDate));
        }

        if (minCollection != null) {
            predicates = criteriaBuilder.and(predicates, criteriaBuilder.greaterThanOrEqualTo(root.get("totalCollection"), minCollection));
        }

        if (maxCollection != null) {
            predicates = criteriaBuilder.and(predicates, criteriaBuilder.lessThanOrEqualTo(root.get("totalCollection"), maxCollection));
        }
        if (user != null) {
            predicates = criteriaBuilder.and(predicates, criteriaBuilder.equal(root.get("cashier"), user));
        }


        criteriaQuery.where(predicates); // Apply all conditions

        // Executing the query
        TypedQuery<SoldBillReport> query = entityManager.createQuery(criteriaQuery);
        return query.getResultList();
    }

    public void createReportForBillsSoldSoFar(User cashier) {
        List<Bill> bills = billRepository.findBySoldByAndSoldByIsNotNullAndReportIsNullAndSoldDateIsNotNull( cashier);
        if (bills.size() == 0) throw new RuntimeException("No unaudited bills found sold by " + cashier.getName());
        generateReport(cashier, null, LocalDate.now(), bills );
    }

}
