package com.UserManagement.billsystem.entities;

import com.UserManagement.enums.CustomerCategory;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Pricing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(unique = true)
    private CustomerCategory customerCategory;

    @DecimalMin("0.0")
    private double blockOne;

    @DecimalMin("0.0")
    private double blockTwo;

    @DecimalMin("0.0")
    private double blockThree;

    @DecimalMin("0.0")
    private double blockFour;

    @DecimalMin("0.0")
    private double blockFive;

    @DecimalMin("0.0")
    private double blockSix;

}
