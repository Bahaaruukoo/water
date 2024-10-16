package com.UserManagement.billsystem.entities;

import com.UserManagement.enums.MeterStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.envers.Audited;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Data
@Entity
@Audited
public class Meter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String meterNumber;
    private String meterSize;
    private double initialReading;
    private String brand;
    private String model;
    private String meterColor;
    private LocalDate stockedDate;
    private MeterStatus status;  //INSTACK, INSTALLED, RETIRED, MAINTENANCE

    @OneToOne (mappedBy = "meter")//, fetch = FetchType.EAGER)
    @JsonIgnore
    private Customer customer;

    @Override
    public String toString(){
        return meterNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Meter meter = (Meter) o;
        return Objects.equals(id, meter.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

