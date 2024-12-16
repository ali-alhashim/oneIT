package com.alhashim.oneIT.models;


import jakarta.persistence.*;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Entity
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @ManyToOne
    private Invoice invoice;
}
