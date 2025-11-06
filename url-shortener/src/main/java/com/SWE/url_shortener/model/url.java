package com.SWE.url_shortener.model;

// defines how our database is structured and how data is stored

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import lombok.Data;

@Entity
@Table(name = "urls")
@Data
public class url {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true, nullable = false)
    private String shortCode;

    @NotBlank
    @Column(length = 2500, nullable = false)
    private String originalUrl;

    @Column(nullable = false)
    private LocalDateTime createdTime = LocalDateTime.now();

    public url() {
    }
}

