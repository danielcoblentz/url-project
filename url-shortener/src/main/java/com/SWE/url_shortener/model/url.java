package com.SWE.url_shortener.model;
//defines how our dataabse is structured and how data is stored

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;


@Entity
@Table(name = "urls")
public class url {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true, nullable = false)
    private String shortCode;

    @NotBlank
    @Column(length = 2500)
    private String origionalString;

    @Column(nullable = false)
    private LocalDateTime createdTime = LocalDateTime.now();
    
}
