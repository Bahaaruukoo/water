package com.UserManagement.exception;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.sql.SQLIntegrityConstraintViolationException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleGlobalException(Exception ex, Model model) {
        model.addAttribute("errorMessage", ex.getMessage());
        model.addAttribute("content", "error/500");
        return "admins";  // Return a generic error page
    }
/*
    // Handle specific exceptions, e.g., resource not found
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleResourceNotFound(ResourceNotFoundException ex, Model model) {
        model.addAttribute("errorMessage", ex.getMessage());
        return "error/404";  // Return a 404 error page
    }
///
    // Handle generic exceptions

    @ExceptionHandler(CustomerExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public String handleCustomerExistsException( CustomerExistsException ex, Model model){
        model.addAttribute("errorMessage", "Customer exists with this phone number or bill number");
        return "error/500";
    }

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public String handleDuplicateKeyException( SQLIntegrityConstraintViolationException ex, Model model){
        model.addAttribute("errorMessage",  ex.getMessage());
        model.addAttribute("content", "error/500");
        return "admins";
    }


*/
    // Add more exception handlers as needed
}
