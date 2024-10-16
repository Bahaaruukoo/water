package com.UserManagement.account.security;

import com.UserManagement.account.Entity.User;
import com.UserManagement.account.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserService userService;

    public CustomAuthenticationSuccessHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        //User user = (User) authentication.getPrincipal();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        // Retrieve your custom User entity from the database using the email (or username)
        User user = userService.findUserByEmail(userDetails.getUsername());

        // If it's the user's first login, redirect to change-password page
        if (user.isFirstLogin()) {
            getRedirectStrategy().sendRedirect(request, response, "/change-password"); // Redirect to password change
            //System.out.println("###############################");
        } else {
            getRedirectStrategy().sendRedirect(request, response, "/home"); // Normal redirect on login
        }
    }
}

