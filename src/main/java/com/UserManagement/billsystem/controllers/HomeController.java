package com.UserManagement.billsystem.controllers;

import com.UserManagement.billsystem.entities.Bill;
import com.UserManagement.billsystem.services.BillService;
import com.UserManagement.billsystem.services.CustomerService;
import com.UserManagement.billsystem.services.MeterService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    private final BillService billService;
    private final CustomerService customerService;
    private final MeterService meterService;

    public HomeController(BillService billService, CustomerService customerService, MeterService meterService) {
        this.billService = billService;
        this.customerService = customerService;
        this.meterService = meterService;
    }

    @GetMapping("/home")
    public String home(Model model) {
        // Fetch relevant data for the home page
        long totalCustomers = customerService.countTotalCustomers();
        long totalMeters = meterService.countTotalMeters();
        double totalUnpaidBills = billService.getTotalUnpaidBills();
        double totalCollectedAmount = billService.getTotalCollectedAmount();
        double totalUpcomingDueBills = billService.getUpcomingDueBills();

        model.addAttribute("totalUpcomingDueBills", totalUpcomingDueBills);
        model.addAttribute("totalCustomers", totalCustomers);
        model.addAttribute("totalMeters", totalMeters);
        model.addAttribute("totalUnpaidBills", totalUnpaidBills);
        model.addAttribute("totalCollectedAmount", totalCollectedAmount);
        model.addAttribute("overdueBills", billService.getOverdueBills());

        // Add any other necessary data
        return "home";
    }
}
