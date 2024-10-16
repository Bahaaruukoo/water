package com.UserManagement.billsystem.rest.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReadingResponseDto {

    private double mReading;
    private String meterId;
    private String customerName;
    private LocalDateTime readingDatetime;

}
