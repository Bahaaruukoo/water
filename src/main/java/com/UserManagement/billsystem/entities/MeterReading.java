package com.UserManagement.billsystem.entities;

import com.UserManagement.account.Entity.User;
import com.UserManagement.enums.BillGenerationStatus;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.envers.Audited;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "meter_reading")
@Audited
public class MeterReading {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "reading_date")//, columnDefinition = "TIMESTAMP")
    private LocalDateTime readingDate;

    private double meterValue;

    @ManyToOne
    @JoinColumn(name = "reader_id")
    private User reader; // Relationship with User entity

    @ManyToOne
    @JoinColumn(name = "meter_id")
    private Meter meter; // Relationship with Meter entity

    private boolean negativeReading = false;
    //@Enumerated(EnumType.STRING) // Store enum as String in DB
    private BillGenerationStatus billGenerationStatus = BillGenerationStatus.CAPTURED;
}
