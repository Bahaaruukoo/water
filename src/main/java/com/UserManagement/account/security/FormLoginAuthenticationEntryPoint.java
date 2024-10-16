package com.UserManagement.account.security;

import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
public class FormLoginAuthenticationEntryPoint extends LoginUrlAuthenticationEntryPoint {

    public FormLoginAuthenticationEntryPoint() {
        super("/login");  // Redirect to the login page if not authenticated
    }
}
