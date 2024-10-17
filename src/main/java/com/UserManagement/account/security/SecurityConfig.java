package com.UserManagement.account.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @Autowired
    @Lazy
    private CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler; // Custom Success Handler

    @Autowired
    CustomAuthenticationSuccessHandler_2 forwardAdminToHisPAge;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();  // Define your PasswordEncoder bean
    }

    // Security filter chain for API mobile users
    @Bean
    public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .securityMatcher("/api/**")
                .authorizeHttpRequests(authorize -> authorize
                        // API authentication for mobile users
                        .requestMatchers("/api/authenticate", "/api/token/refresh").permitAll()
                        .requestMatchers("/api/reports/**").permitAll()
                        .requestMatchers("/api/mobile/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/test/**").hasRole("ADMIN")
                        .requestMatchers("/api/mobile/user/**").hasAnyRole("USER", "ADMIN")
                        // All other API requests require authentication
                        .anyRequest().authenticated()
                )
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint) // Handle API authentication exceptions
                )
                .sessionManagement(sessionManagement -> sessionManagement
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Stateless sessions for API
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class); // Add JWT filter for API requests

        return http.build();
    }

    // Security filter chain for Thymeleaf web users
    @Bean
    public SecurityFilterChain webSecurityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authorize -> authorize
                        // Permit access to these URLs for everyone
                        .requestMatchers("/", "/login", "/webjars/**", "/register/**", "/forgot/**", "/index").permitAll()
                        // Protect web application endpoints
                        .requestMatchers("system/setup/**").permitAll()
                        .requestMatchers("/users/**", "/edit/**", "/add/**", "/delete/**","/settings/**").hasRole("ADMIN")
                        .requestMatchers("/billing/void/**").hasAnyRole("MANAGER")
                        .requestMatchers("/billing", "/billing/view/**").hasAnyRole("ADMIN", "CASHIER", "MANAGER")
                        .requestMatchers("/billing/generate/**").hasAnyRole("ADMIN", "CASHIER")
                        .requestMatchers("/customers/**").hasAnyRole("ADMIN", "USER")
                        .requestMatchers("/meters/**").hasAnyRole("ADMIN", "USER")
                        .requestMatchers("/readings/**").hasAnyRole("ADMIN", "READER", "CASHIER")
                        .requestMatchers("/api/**").permitAll()
                        .requestMatchers("/dashboard/**").authenticated()
                        // All other requests require authentication
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .successHandler(customAuthenticationSuccessHandler) // Custom Success Handler for first login
                        .successHandler(forwardAdminToHisPAge)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                        .permitAll()
                ).exceptionHandling()
                .authenticationEntryPoint((request, response, authException) -> {
                    response.sendRedirect("/login?error=true");
                })
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    response.sendRedirect("/access-denied");
                });
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

}
