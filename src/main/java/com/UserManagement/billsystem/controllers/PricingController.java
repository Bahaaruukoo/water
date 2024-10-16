package com.UserManagement.billsystem.controllers;

import com.UserManagement.billsystem.entities.Pricing;
import com.UserManagement.billsystem.services.PricingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/pricing")
public class PricingController {

    @Autowired
    private PricingService pricingService;

    @GetMapping
    public String listPricing(Model model) {
        List<Pricing> pricingList = pricingService.findAll();
        model.addAttribute("pricingList", pricingList);
        model.addAttribute("pageTitle", "Pricing Settings");
        model.addAttribute("content", "pricing/list"); // Reference to a Thymeleaf fragment

        return "admins";
    }

    //@GetMapping("/add")
    public String addPricingForm(Model model) {
        model.addAttribute("pricing", new Pricing());
        model.addAttribute("content", "pricing/add"); // Reference to a Thymeleaf fragment
        return "admins"; // Returns the template
    }

    //@PostMapping("/add")
    public String addPricing(@Valid @ModelAttribute Pricing pricing, BindingResult result) {
        if (result.hasErrors()) {
            return "pricing/add"; // Return to the form if there are errors
        }
        pricingService.save(pricing);
        return "redirect:/pricing"; // Redirect to the list after saving
    }

    @GetMapping("/edit/{id}")
    public String editPricingForm(@PathVariable Long id, Model model) {
        Pricing pricing = pricingService.findById(id);
        if (pricing == null) {
            // Handle the case where the pricing entry is not found (e.g., return to the list)
            return "redirect:/pricing";
        }
        model.addAttribute("content", "pricing/edit"); // Reference to a Thymeleaf fragment
        model.addAttribute("pricing", pricing);
        return "admins"; // Returns the edit template
    }

    @PostMapping("/edit/{id}")
    public String editPricing(@PathVariable Long id, @Valid @ModelAttribute Pricing pricing, BindingResult result) {
        if (result.hasErrors()) {
            return "pricing/edit"; // Return to the form if there are errors
        }
        pricing.setId(id); // Set the ID for updating
        pricingService.save(pricing);
        pricingService.updateGlobalPricing();
        return "redirect:/pricing"; // Redirect to the list after saving
    }

    //@GetMapping("/delete/{id}")
    public String deletePricing(@PathVariable Long id) {
        pricingService.deleteById(id);
        return "redirect:/pricing"; // Redirect to the list after deleting
    }
}
