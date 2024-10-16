package com.UserManagement.billsystem.controllers;

import com.UserManagement.billsystem.entities.Settings;
import com.UserManagement.billsystem.services.SettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/settings")
public class SettingsController {

    @Autowired
    private SettingsService settingsService;

    @GetMapping
    public String listSettings(Model model) {
        model.addAttribute("settings", settingsService.findAll().stream().findFirst().get());
        model.addAttribute("content", "settings/settings-list"); // Reference to a Thymeleaf fragment
        return "admins";
    }

    //@GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("setting", new Settings());
        model.addAttribute("content", "settings/settings-form"); // Reference to a Thymeleaf fragment
        return "admins";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("setting", settingsService.findById(id));
        model.addAttribute("content", "settings/settings-form"); // Reference to a Thymeleaf fragment
        return "admins";
    }

    @PostMapping("/save")
    public String saveSetting(@ModelAttribute Settings setting) {
        settingsService.save(setting);
        settingsService.updateGlobalSettings();
        return "redirect:/settings";
    }

    //@GetMapping("/delete/{id}")
    public String deleteSetting(@PathVariable Long id) {
        settingsService.delete(id);
        return "redirect:/settings";
    }
    @GetMapping("/factoryReset/{id}")
    public String factoryReset(@PathVariable Long id) {
        settingsService.factoryReset(id);
        return "redirect:/settings";

    }

    @GetMapping("/schedule")
    public String scheduleTasksGet(Model model) {
        model.addAttribute("content", "settings/scheduler");
        return "admins";  // Redirect after scheduling
    }

    @PostMapping("/schedule")
    public String scheduleTasks(
                                @RequestParam("billGenerationFrequency") String billGenerationFrequency
                                ) {

        // Process the selected frequencies
        System.out.println("Selected Bill gen Frequency: " + billGenerationFrequency);

        // Add your scheduling logic here based on the selected frequencies
        settingsService.setSchedules(billGenerationFrequency);
        return "redirect:/settings";  // Redirect after scheduling
    }
}
