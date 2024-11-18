package com.alhashim.oneIT.models;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String subject;

    private String description;

    @ManyToMany(mappedBy = "notifications")
    private Set<Employee> employees = new HashSet<>();

    private LocalDateTime createdAt;

    private LocalDateTime readAt;
}
