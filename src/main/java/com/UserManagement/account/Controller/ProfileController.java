package com.UserManagement.account.Controller;

import com.UserManagement.account.Entity.User;
import com.UserManagement.account.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ProfileController {

    @Autowired
    private UserService userService;

    // View Profile
    @GetMapping("/user/profile")
    public String viewUserProfile(Model model) {
        User loggedInUser = userService.getLoggedInUser();
        model.addAttribute("user", loggedInUser);
        model.addAttribute("content", "user/user-profile"); // Reference to a Thymeleaf fragment

        return "admins";
    }

    // Show Edit Form
    @GetMapping("/user/edit")
    public String showEditProfileForm(Model model) {
        User loggedInUser = userService.getLoggedInUser();
        model.addAttribute("user", loggedInUser);
        model.addAttribute("content", "user/user-edit"); // Reference to a Thymeleaf fragment

        return "admins";  // Thymeleaf template for editing
    }

    // Update User Profile
    @PostMapping("/user/update")
    public String updateUserProfile(@ModelAttribute("user") User user, RedirectAttributes redirectAttributes) {
        User existingUser = userService.getLoggedInUser();

        // Update only non-sensitive fields
        existingUser.setName(user.getName());
        existingUser.setPhone(user.getPhone());
        existingUser.setGender(user.getGender());
        existingUser.setAddress(user.getAddress());

        userService.updateUser(existingUser);
        redirectAttributes.addFlashAttribute("message", "Profile updated successfully!");
        return "redirect:/user/profile";
    }
}
