package com.UserManagement.billsystem.controllers;

import com.UserManagement.account.Entity.Role;
import com.UserManagement.account.Entity.User;
import com.UserManagement.billsystem.entities.Customer;
import com.UserManagement.billsystem.services.CustomerService;
import com.UserManagement.billsystem.services.MeterReadingService;
import com.UserManagement.billsystem.services.MeterService;
import com.UserManagement.enums.CustomerCategory;
import com.UserManagement.enums.MeterStatus;
import com.UserManagement.enums.ServiceStatus;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/customers")
public class CustomerController {
    @Autowired
    private CustomerService customerService;

    @Autowired
    private MeterService meterService;

    @GetMapping
    public String getCustomers(@RequestParam(required = false) String status,
                               @RequestParam(required = false) String phoneNumber,
                               @RequestParam(required = false) String meterNumber,
                               @RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "10") int size,
                               Model model) {

        // Convert the status String to the corresponding enum if it's provided
        ServiceStatus serviceStatus = null;
        if (status != null && !status.isEmpty()) {
            try {
                serviceStatus = ServiceStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                // Handle invalid enum value here (optional)
                model.addAttribute("error", "Invalid status value");
                model.addAttribute("content", "customer/customer-list"); // Reference to a Thymeleaf fragment

                return "admins";
            }
        }

        // Pageable setup
        Pageable pageable = PageRequest.of(page, size);

        // Fetch filtered customers
        Page<Customer> customerPage =  customerService.getFilteredCustomers(phoneNumber, meterNumber, serviceStatus, pageable);

        // Add attributes to the model
        model.addAttribute("customerPage", customerPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", customerPage.getTotalPages());
        model.addAttribute("selectedStatus", status); // Optional: For keeping selected status in the form
        model.addAttribute("selectedPhoneNumber", phoneNumber); // Optional: Keep selected phone number
        model.addAttribute("selectedMeterNumber", meterNumber); // Optional: Keep selected meter number
        model.addAttribute("content", "customer/customer-list"); // Reference to a Thymeleaf fragment

        return "admins"; // Return the Thymeleaf template
    }

    @GetMapping("/new")
    public String newCustomerForm(Model model) {
        model.addAttribute("customer", new Customer());
        model.addAttribute("meters", meterService.findAllMetersINSTACK());
        model.addAttribute("serviceStatuses", ServiceStatus.values()); // Pass MeterStatus enum values
        model.addAttribute("customerCategory", CustomerCategory.values());
        model.addAttribute("content", "customer/customer-form"); // Reference to a Thymeleaf fragment

        return "admins";
    }

    @PostMapping("/save")
    public String saveCustomer(@Valid @ModelAttribute Customer customer, BindingResult result, Model model) {

        boolean existingCustomer = customerService.findByPhone(customer.getPhoneNumber());
        if (existingCustomer ){//!= null && existingCustomer.getPhoneNumber() != null && !existingCustomer.getPhoneNumber().isEmpty()) {
            result.rejectValue("phoneNumber", null,
                    "There is already a customer registered with this phone number");
        }
        if (result.hasErrors()) {
            model.addAttribute("customer", customer);
            model.addAttribute("meters", meterService.findAllMetersINSTACK());
            model.addAttribute("serviceStatuses", ServiceStatus.values()); // Pass MeterStatus enum values
            model.addAttribute("customerCategory", CustomerCategory.values());
            model.addAttribute("content", "customer/customer-form"); // Reference to a Thymeleaf fragment

            return "admins";
        }
        customerService.save(customer);
        return "redirect:/customers";
    }

    @GetMapping("/delete/{id}")
    public String deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return "redirect:/customers";
    }
    @GetMapping("/edit/{id}")
    public String editCustomer(@PathVariable Long id, Model model) {
        Customer customer = customerService.findById(id);
        model.addAttribute("customer", customer);
        model.addAttribute("customerCategory", CustomerCategory.values());
        model.addAttribute("serviceStatuses", ServiceStatus.values()); // Pass MeterStatus enum values
        model.addAttribute("content", "customer/customer-edit"); // Reference to a Thymeleaf fragment

        return "admins"; // You need to create this template for editing
    }

    @PostMapping("/update")
    public String updateCustomer(@ModelAttribute Customer customer) {
        customerService.update(customer); // Save the updated customer
        return "redirect:/customers";
    }

}
