package com.UserManagement.billsystem.services;

import org.springframework.stereotype.Service;

@Service
public class HomeService {

    private final BillService billService;
    private final CustomerService customerService;
    private final MeterService meterService;

    public HomeService(BillService billService, CustomerService customerService, MeterService meterService) {
        this.billService = billService;
        this.customerService = customerService;
        this.meterService = meterService;
    }

    // You can define additional methods here if needed
}
