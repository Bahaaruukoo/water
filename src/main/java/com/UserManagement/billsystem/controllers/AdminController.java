package com.UserManagement.billsystem.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/dashboard")
public class AdminController {

    @GetMapping
    //@PreAuthorize( hasrole("admin") )
    public String adminDash(Model model){
        model.addAttribute("content", "landing-page"); // Reference to a Thymeleaf fragment
        return "admins";
    }
}
