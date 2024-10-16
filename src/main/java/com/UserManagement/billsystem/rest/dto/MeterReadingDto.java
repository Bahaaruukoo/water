package com.UserManagement.billsystem.rest.dto;

import java.time.LocalDate;

public class MeterReadingDto {

    private double mReading;
    private String meterId;

    public double getmReading() {
        return mReading;
    }

    public void setmReading(double mReading) {
        this.mReading = mReading;
    }

    public String getMeterId() {
        return meterId;
    }

    public void setMeterId(String meterId) {
        this.meterId = meterId;
    }

    @Override
    public String toString() {
        return "MeterReadingDto{" +
                "mReading=" + mReading +
                ", meterId='" + meterId + '\'' +
                '}';
    }
}
