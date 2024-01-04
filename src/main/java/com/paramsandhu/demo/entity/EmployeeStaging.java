package com.paramsandhu.demo.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;


@Entity
@Data
@NoArgsConstructor
public class EmployeeStaging {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long employeeId;

    private String name;
    private double salary;
    private boolean married;

    @Column(nullable = false)
    private Instant createdTimestamp;

    @Column(nullable = false)
    private boolean isEnriched = false;

    @Column(nullable = false)
    private boolean isTaxCalculated = false;

    @Column(nullable = false)
    private boolean isAccountNumberGenerated = false;

    private int enrichTries = 0;

    private double taxDue;

    private String accountNumber;

    private Instant lastEnrichedTimestamp;



    @PrePersist
    protected void onCreate() {
        createdTimestamp = Instant.now(); // Set to current UTC timestamp
    }


    // Getters and setters
}

