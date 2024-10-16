package com.UserManagement.billsystem.controllers;

import com.UserManagement.billsystem.entities.Meter;
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

@Controller
@RequestMapping("/meters")
public class MeterController {

    @Autowired
    private MeterService meterService;

    @GetMapping()
    public String getMeters(Model model,
                            @RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "10") int size,
                            @RequestParam(name = "meterNumber", required = false) String meterNumber) {

        if (page < 0) {
            page = 0; // Ensure page number is not negative
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<Meter> meterPage;

        // Check if meterNumber filter is provided, if yes, filter by it
        if (meterNumber != null && !meterNumber.isEmpty()) {
            meterPage = meterService.findByMeterNumber(meterNumber, pageable);
        } else {
            meterPage = meterService.getPaginatedMeters(pageable);
        }

        model.addAttribute("meterPage", meterPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", meterPage.getTotalPages());
        model.addAttribute("meterNumber", meterNumber);  // Pass the meterNumber back to the view for display in the search field
        model.addAttribute("content", "meter/meter-list"); // Reference to a Thymeleaf fragment

        return "admins";
    }


    @GetMapping("/new")
    public String showNewMeterForm(Model model) {
        model.addAttribute("meter", new Meter());
        model.addAttribute("meterStatuses", MeterStatus.values()); // Pass MeterStatus enum values
        model.addAttribute("content", "meter/meter-form"); // Reference to a Thymeleaf fragment
        return "admins";
    }

    @PostMapping("/save")
    public String saveMeter(@Valid @ModelAttribute("meter") Meter meter, BindingResult result, Model model) {
        boolean meterExists = meterService.getByMeterNumber(meter.getMeterNumber());

            if (meterExists ){//!= null && existingCustomer.getPhoneNumber() != null && !existingCustomer.getPhoneNumber().isEmpty()) {
                result.rejectValue("meterNumber", null,
                        "There is already a meter registered with this number");
            }
            if (result.hasErrors()) {
                model.addAttribute("meter", meter);
                model.addAttribute("meterStatuses", MeterStatus.values()); // Pass MeterStatus enum values
                model.addAttribute("content", "meter/meter-form"); // Reference to a Thymeleaf fragment

                return "admins";
            }

        meterService.saveMeter(meter);
        return "redirect:/meters";
    }

    @GetMapping("/edit/{id}")
    public String showEditMeterForm(@PathVariable Long id, Model model) {
        Meter meter = meterService.getMeterById(id);
        model.addAttribute("meter", meter);
        model.addAttribute("meterStatuses", MeterStatus.values()); // Pass MeterStatus enum values
        model.addAttribute("content", "meter/meter-edit-form"); // Reference to a Thymeleaf fragment
        return "admins";
    }

    @PostMapping("/update")
    public String updateMeter(@ModelAttribute("meter") Meter meter) {
        meterService.update(meter);
        return "redirect:/meters";
    }

    //@GetMapping("/delete/{id}")
    public String deleteMeter(@PathVariable Long id) {
        meterService.deleteMeter(id);
        return "redirect:/meters";
    }
}
