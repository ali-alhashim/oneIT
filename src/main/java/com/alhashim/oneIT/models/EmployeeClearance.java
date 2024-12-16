package com.alhashim.oneIT.models;


import jakarta.persistence.*;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
public class EmployeeClearance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;


    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    private LocalDate lastDay;

    private String status;

    @OneToOne
    private Employee directManager;
    private Boolean  directManagerOk;


    @OneToOne
    private Employee hr;
    private Boolean hrOk;

    @OneToOne
    private Employee it;
    private Boolean itOk;

    @OneToOne
    private Employee medical;
    private Boolean medicalOk;

    @OneToOne
    private Employee vehicle;
    private Boolean vehicleOk;

    @OneToOne
    private Employee finance;
    private Boolean financeOk;

    private String note;

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

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public LocalDate getLastDay() {
        return lastDay;
    }

    public void setLastDay(LocalDate lastDay) {
        this.lastDay = lastDay;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Employee getDirectManager() {
        return directManager;
    }

    public void setDirectManager(Employee directManager) {
        this.directManager = directManager;
    }

    public Boolean getDirectManagerOk() {
        return directManagerOk;
    }

    public void setDirectManagerOk(Boolean directManagerOk) {
        this.directManagerOk = directManagerOk;
    }

    public Employee getHr() {
        return hr;
    }

    public void setHr(Employee hr) {
        this.hr = hr;
    }

    public Boolean getHrOk() {
        return hrOk;
    }

    public void setHrOk(Boolean hrOk) {
        this.hrOk = hrOk;
    }

    public Employee getIt() {
        return it;
    }

    public void setIt(Employee it) {
        this.it = it;
    }

    public Boolean getItOk() {
        return itOk;
    }

    public void setItOk(Boolean itOk) {
        this.itOk = itOk;
    }

    public Employee getMedical() {
        return medical;
    }

    public void setMedical(Employee medical) {
        this.medical = medical;
    }

    public Boolean getMedicalOk() {
        return medicalOk;
    }

    public void setMedicalOk(Boolean medicalOk) {
        this.medicalOk = medicalOk;
    }

    public Employee getVehicle() {
        return vehicle;
    }

    public void setVehicle(Employee vehicle) {
        this.vehicle = vehicle;
    }

    public Boolean getVehicleOk() {
        return vehicleOk;
    }

    public void setVehicleOk(Boolean vehicleOk) {
        this.vehicleOk = vehicleOk;
    }

    public Employee getFinance() {
        return finance;
    }

    public void setFinance(Employee finance) {
        this.finance = finance;
    }

    public Boolean getFinanceOk() {
        return financeOk;
    }

    public void setFinanceOk(Boolean financeOk) {
        this.financeOk = financeOk;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
