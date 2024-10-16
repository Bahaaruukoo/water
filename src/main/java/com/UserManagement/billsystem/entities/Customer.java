package com.UserManagement.billsystem.entities;

import com.UserManagement.enums.CustomerCategory;
import com.UserManagement.enums.ServiceStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.envers.Audited;

import java.time.LocalDate;
import java.util.List;

@Data
@Entity
@Audited
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String kebele;
    private String houseNumber;
    private String ordinantNumber;

    @Column(nullable = false, unique = true, length = 10)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    private CustomerCategory customerCategory;

    @Column(nullable = false, unique = true, updatable = false)
    private String billNumber;
    private ServiceStatus serviceStatus;
    private LocalDate registrationDate;

    @OneToOne
    @JoinColumn(name = "meter_id")
    private Meter meter;

    @OneToMany(mappedBy = "customer", fetch = FetchType.EAGER)
    @JsonIgnore
    private List<Bill> bills;

}
