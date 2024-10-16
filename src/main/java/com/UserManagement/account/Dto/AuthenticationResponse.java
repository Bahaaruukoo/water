package com.UserManagement.account.Dto;

import lombok.Data;

@Data
public class AuthenticationResponse {
    private String accessToken;
    private String refreshToken; // Add this field

    public AuthenticationResponse(String accessToken) {
        this.accessToken = accessToken;
    }

    public AuthenticationResponse(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken; // Set refresh token
    }

    // Getters and Setters
}

/*
public class AuthenticationResponse {
    private final String jwt;

    public AuthenticationResponse(String jwt) {
        this.jwt = jwt;
    }

    public String getJwt() {
        return jwt;
    }

}
*/