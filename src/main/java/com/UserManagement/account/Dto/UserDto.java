package com.UserManagement.account.Dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserDto {

    private Long id;

    @NotBlank(message = "First name should not be empty")
    @Size(min = 2, max = 50, message = "First name should be between 2 and 50 characters")
    private String firstName;

    @NotEmpty(message = "Last name should not be empty")
    private String lastName;

    @NotEmpty(message = "Email should not be empty")
    @Email
    private String email;

    @NotNull(message = "Password should not be empty")
    private String password;

    @Positive(message = "Age should be a positive number")
    @Max(value = 200, message = "Age should not exceed 200")
    private Integer age;

    @NotEmpty(message = "Phone number should not be empty")
    @Pattern(regexp = "\\d{10}", message = "Phone number should have exactly 10 numbers")
    private String phone;

    @NotEmpty(message = "Gender should not be empty")
    @Pattern(regexp = "^(Male|Female)$", message = "Gender must be either 'Male' or 'Female'")
    private String gender;

    @NotBlank(message = "Address should not be empty")
    @Size(min = 5, max = 100, message = "Address should be between 5 and 100 characters")
    private String address;

    // Changed role to a list of strings for multiple roles
    private List<String> roles = new ArrayList<>();

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
}
