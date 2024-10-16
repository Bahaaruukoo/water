package com.UserManagement.exception;

public class CustomerExistsException extends RuntimeException{


    public CustomerExistsException(String message){
        super(message);
    }

}
