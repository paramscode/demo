package com.paramsandhu.demo.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;


@Entity
@Data
@NoArgsConstructor
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private double salary;
    private boolean married;

    @Column(nullable = false)
    private Instant createdTimestamp;

    @Column(nullable = false)
    private boolean isProcessed = false;

    @Column(nullable = false)
    private boolean isEnriched = false;

    @Column(nullable = false)
    private boolean isJsonException = false;


    //private int enrichTries = 0;

    //private Instant lastEnrichedTimestamp;

    @Column(columnDefinition = "TEXT")
    private String payload;


    @PrePersist
    protected void onCreate() {
        createdTimestamp = Instant.now(); // Set to current UTC timestamp
    }


    // Getters and setters
}

