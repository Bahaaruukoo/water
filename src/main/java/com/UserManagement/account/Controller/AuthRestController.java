package com.UserManagement.account.Controller;

import com.UserManagement.account.Dto.AuthenticationRequest;
import com.UserManagement.account.Dto.AuthenticationResponse;
import com.UserManagement.account.Dto.RefreshTokenRequest;
import com.UserManagement.account.security.CustomUserDetailsService;
import com.UserManagement.account.security.JwtTokenUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class AuthRestController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @GetMapping("/test")
    @PreAuthorize("hasRole('USER')")
    public String test(){
        return "We got a response";
    }

    @PostMapping("/authenticate")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) throws Exception {

        authenticate(authenticationRequest.getEmail(), authenticationRequest.getPassword());

        final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getEmail());

        final String username = userDetails.getUsername(); // Extract the username
        final String token = jwtTokenUtil.generateToken(userDetails); // Pass the username to generateToken
        final String refreshToken = jwtTokenUtil.generateRefreshToken(userDetails);

        //final String token = jwtTokenUtil.generateToken(userDetails);

        return ResponseEntity.ok(new AuthenticationResponse(token, refreshToken));
    }

    private void authenticate(String username, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }
    }
    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshAccessToken(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        String refreshToken = refreshTokenRequest.getRefreshToken();


        // Extract username from refresh token (assuming you store it)
        String username = jwtTokenUtil.getUsernameFromToken(refreshToken);

        // Load user details
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        // Validate the refresh token
        if (refreshToken == null || !jwtTokenUtil.validateRefreshToken(refreshToken, userDetails)) {
            return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED).body("Invalid refresh token");
        }

        // Generate a new access token
        String newAccessToken = jwtTokenUtil.generateToken(userDetails);

        // Generate a new refresh token
        String newRefreshToken = jwtTokenUtil.generateRefreshToken(userDetails);

        // Return the new access token
        return ResponseEntity.ok(new AuthenticationResponse(newAccessToken, refreshToken));
    }

}