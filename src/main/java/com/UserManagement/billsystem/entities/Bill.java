package com.UserManagement.billsystem.entities;

import com.UserManagement.account.Entity.User;
import com.UserManagement.enums.AuditStatus;
import com.UserManagement.enums.BillPaymentStatus;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import org.hibernate.envers.Audited;

@Data
@Entity
@Audited
//@AuditTable(value = "BILL_AUDIT") // Custom audit table name
public class Bill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private BillPaymentStatus status; // UNSOLD, PAID, VOID
    private LocalDate invoiceDate;
    private LocalDate dueDate;
    private double currentReading;
    private double previousReading;
    private double consumption;

    @ManyToOne
    @JoinColumn(name = "customer_id", updatable = false)
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "current_meter_reading_id", updatable = false)
    private MeterReading meterReading;

    @Column( updatable = false)
    private String billNumber;

    @ManyToOne // (fetch = FetchType.EAGER)
    @JoinColumn(name = "previous_meter_reading_id", updatable = false)
    private MeterReading previousMeterReading;

    private double amount;
    private double meterRent;
    private double serviceCharge;
    private double interest;
    private double penalty;
    private double totalBill;

    @ManyToOne
    @JsonIgnore
    private User soldBy;

    private AuditStatus auditStatus;
    @Column(nullable = true)
    private LocalDate soldDate;

    @Column(nullable = true)
    private LocalDate auditApprovedDate;

    @ManyToOne
    private User auditApprovedBy;

    @ManyToOne
    @JoinColumn(name = "report_id")
    @JsonIgnore
    private SoldBillReport report;

}

