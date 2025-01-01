package com.alhashim.oneIT.models;


import jakarta.persistence.*;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

@Entity
public class ShiftSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    private String name;

    private LocalTime startTime;
    private LocalTime endTime;

    private Boolean sundayWork;
    private Boolean mondayWork;
    private Boolean tuesdayWork;
    private Boolean wednesdayWork;
    private Boolean thursdayWork;
    private Boolean fridayWork;

    @OneToMany(mappedBy = "shiftSchedule")
    private Set<Employee> employees = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public Boolean getSundayWork() {
        return sundayWork;
    }

    public void setSundayWork(Boolean sundayWork) {
        this.sundayWork = sundayWork;
    }

    public Boolean getMondayWork() {
        return mondayWork;
    }

    public void setMondayWork(Boolean mondayWork) {
        this.mondayWork = mondayWork;
    }

    public Boolean getTuesdayWork() {
        return tuesdayWork;
    }

    public void setTuesdayWork(Boolean tuesdayWork) {
        this.tuesdayWork = tuesdayWork;
    }

    public Boolean getWednesdayWork() {
        return wednesdayWork;
    }

    public void setWednesdayWork(Boolean wednesdayWork) {
        this.wednesdayWork = wednesdayWork;
    }

    public Boolean getThursdayWork() {
        return thursdayWork;
    }

    public void setThursdayWork(Boolean thursdayWork) {
        this.thursdayWork = thursdayWork;
    }

    public Boolean getFridayWork() {
        return fridayWork;
    }

    public void setFridayWork(Boolean fridayWork) {
        this.fridayWork = fridayWork;
    }

    public Set<Employee> getEmployees() {
        return employees;
    }

    public void setEmployees(Set<Employee> employees) {
        this.employees = employees;
    }
}
