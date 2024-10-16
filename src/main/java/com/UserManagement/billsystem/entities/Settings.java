package com.UserManagement.billsystem.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.Getter;

@Data
@Entity
@Getter
public class Settings {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @DecimalMin("0.0")
    @Column(nullable = false)
    private int dueDateAfter = 10;

    @DecimalMin("0.0")
    @Column(nullable = false)
    private double meterRentalPrice = 10;

    @DecimalMin("0.0")
    @Column(nullable = false)
    private double latePaymentFee = 0;

    @DecimalMin("0.0")
    @Column(nullable = false)
    private double serviceCharge = 0;

    private boolean rejectLessReading = false;

    private boolean manualBillGeneration = true;

    // bill generation manual or automatic
    private String reportGenerationTimeHour = "17";
    private String reportGenerationTimeMinute = "0";
    private String reportGenerationFrequency = "Daily";

    private String billGenerationFrequency = "5";
}
