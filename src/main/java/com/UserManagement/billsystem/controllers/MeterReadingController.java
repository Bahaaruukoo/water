package com.UserManagement.billsystem.controllers;

import com.UserManagement.billsystem.entities.Meter;
import com.UserManagement.billsystem.entities.MeterReading;
import com.UserManagement.billsystem.services.MeterReadingService;
import com.UserManagement.billsystem.services.MeterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Controller
@RequestMapping("/readings")
public class MeterReadingController {

    @Autowired
    private MeterReadingService meterReadingService;

    @Autowired
    private MeterService meterService;

    //@GetMapping
    public String getMeterReadings(Model model,
                                   @RequestParam(defaultValue = "0") int page,
                                   @RequestParam(defaultValue = "10") int size) {
        if (page < 0 ){
            page = 0; // or redirect to the last valid page
        }
        Pageable pageable = PageRequest.of(page, size);
        Page<MeterReading> meterReadingsPage = meterReadingService.getPaginatedMeterReadings(pageable);
        model.addAttribute("meterReadingsPage", meterReadingsPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", meterReadingsPage.getTotalPages());
        model.addAttribute("content", "meter-reading/meter-reading-list"); // Reference to a Thymeleaf fragment

        return "admins";
    }
    @GetMapping//("")
    public String getMeterReadings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String meterNumber,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate readingStartDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate readingEndDate,
            @RequestParam(required = false) Boolean negativeReading,
            Model model) {

        if (page < 0 ){
            page = 0; // or redirect to the last valid page
        }

        Pageable pageable = PageRequest.of(page, size);

        // Pass filter parameters to the service layer
        Page<MeterReading> meterReadingsPage = meterReadingService.findAllWithFilters(
                meterNumber, readingStartDate, readingEndDate, negativeReading, pageable);

        // Add attributes to the model for the Thymeleaf template
        model.addAttribute("meterReadingsPage", meterReadingsPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", meterReadingsPage.getTotalPages());
        model.addAttribute("meterNumber", meterNumber);
        model.addAttribute("readingStartDate", readingStartDate);
        model.addAttribute("readingEndDate", readingEndDate);
        model.addAttribute("negativeReading", negativeReading);
        model.addAttribute("content", "meter-reading/meter-reading-list"); // Reference to a Thymeleaf fragment

        return "admins";
    }

    @GetMapping("/new")
    public String newMeterReadingForm(Model model) {
        model.addAttribute("meterReading", new MeterReading());
        model.addAttribute("meters", meterService.findAllMetersINSTALLED());
        model.addAttribute("content", "meter-reading/meter-reading-form"); // Reference to a Thymeleaf fragment
        return "admins";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute MeterReading meterReading) {
        System.out.println(meterReading.toString());
        meterReadingService.save(meterReading);
        return "redirect:/readings";
    }
    @PostMapping("/update")
    public String update(@ModelAttribute MeterReading meterReading) {
        System.out.println("Post: "+meterReading);
        meterReadingService.update(meterReading);
        return "redirect:/readings";
    }

    @GetMapping("/edit/{id}")
    public String editMeterReadingForm(@PathVariable Long id, Model model) {
        MeterReading meterReading = meterReadingService.findById(id);
        System.out.println("GET: " +meterReading);
        model.addAttribute("meterReading", meterReading);
        model.addAttribute("content", "meter-reading/meter-reading-form"); // Reference to a Thymeleaf fragment
        //model.addAttribute("meters", meterReadingService.findAllMeters());
        return "admins";
    }

    @GetMapping("/delete/{id}")
    public String deleteMeterReading(@PathVariable Long id) {
        meterReadingService.deleteById(id);
        return "redirect:/readings";
    }
}
