package com.UserManagement.billsystem.rest.controllers;

import com.UserManagement.billsystem.entities.Meter;
import com.UserManagement.billsystem.entities.MeterReading;
import com.UserManagement.billsystem.rest.dto.MeterReadingDto;
import com.UserManagement.billsystem.rest.dto.ReadingResponseDto;
import com.UserManagement.billsystem.services.MeterReadingService;
import com.UserManagement.billsystem.services.MeterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class MeterReadingRestController {

    @Autowired
    MeterReadingService meterReadingService ;

    @Autowired
    MeterService meterService ;

    @PostMapping("/readings")
    public ResponseEntity<?> addMeterReading(@RequestBody MeterReadingDto meterReadingDto){
        MeterReading meterReading = new MeterReading();
        //System.out.println("Meter "+ meterReadingDto.toString());

        Meter meter = meterService.getMeterByMeterNumber(meterReadingDto.getMeterId());
        meterReading.setMeter(meter);
        meterReading.setMeterValue(meterReadingDto.getmReading());
        ReadingResponseDto response = meterReadingService.save(meterReading);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
