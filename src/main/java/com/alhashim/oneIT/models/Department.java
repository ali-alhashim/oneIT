package com.alhashim.oneIT.models;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "departments")
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    private String ar_name;

    private LocalDateTime createdAt;


    //-------- the Relationship filed-----------
    @OneToMany(mappedBy = "department")
    private Set<Employee> employees = new HashSet<>();


}
