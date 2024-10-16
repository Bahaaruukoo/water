package com.UserManagement.billsystem.rest.controllers;

import com.UserManagement.billsystem.entities.Bill;
import com.UserManagement.billsystem.entities.SoldBillReport;
import com.UserManagement.billsystem.rest.Services.BillRestService;
import com.UserManagement.billsystem.services.BillService;
import com.UserManagement.billsystem.services.SoldBillReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class BillRestController {

    @Autowired
    private SoldBillReportService soldBillReportService;
    @Autowired
    private BillRestService billService;

    // API to fetch all bills
    @GetMapping("/bills")
    public List<Bill> getAllBills() {
        return billService.getAllBills();
    }
    @GetMapping("/reports/{id}")
    public ResponseEntity<SoldBillReport> getReports(@PathVariable Long id){
        return new ResponseEntity<>(soldBillReportService.findById(id), HttpStatus.OK);
    }
}
