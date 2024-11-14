package com.alhashim.oneIT.models;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tasks")
public class EmployeeTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private String description;

    @ManyToMany(mappedBy = "tasks")
    private Set<Employee> employees = new HashSet<>();
}
