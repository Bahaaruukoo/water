package com.UserManagement.billsystem.dto;

import com.UserManagement.enums.CustomerCategory;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class KebeleCategoryBillReportDTO {
    private String kebele;
    private CustomerCategory customerCategory;
    private long billCount;
    private double minBillAmount;
    private double maxBillAmount;
    private String maxBillNumber;
    private double totalMeterRent;
    private double totalBillAmount;
    private double totalServiceCharge;
    private double totalPenalty;
    private double grandTotal;

    // Constructor, getters, and setters
}

