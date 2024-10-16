package com.UserManagement.billsystem.entities;

import com.UserManagement.account.Entity.User;
import com.UserManagement.enums.AuditStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import org.hibernate.envers.Audited;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Data
@Entity
@Getter
@Audited
public class SoldBillReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate startDate;
    private LocalDate endDate;

    @ManyToOne
    private User cashier;

    @Enumerated(EnumType.STRING)
    private AuditStatus status; // SUBMITTED, AUDITED, REJECTED

    @OneToMany(mappedBy = "report", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    //@JsonIgnore
    //@JsonManagedReference // Prevents recursion
    private List<Bill> bills;

    // Additional fields (e.g., total amount, notes, etc.)
    private double totalInterest;
    private double totalPenalty;
    private double totalMeterRental;
    private double ConsumptionAmount;
    private double totalCollection;

    //@Temporal(TemporalType.TIMESTAMP)
    private LocalDate auditedDate;

    @ManyToOne
    private User auditor;

    // getters and setters
}
