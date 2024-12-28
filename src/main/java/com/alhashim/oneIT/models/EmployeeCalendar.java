package com.alhashim.oneIT.models;

import jakarta.persistence.*;
import org.springframework.data.annotation.LastModifiedDate;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;


@Entity
public class EmployeeCalendar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @ManyToOne
    @JoinColumn(name = "geolocation_id", referencedColumnName = "id")
    private Geolocation geolocation;


    private String mobileOS; // android or IOS
    private String mobileModel; //iphone15 iphone16 samsung galaxy 24 ...

    @ManyToOne
    @JoinColumn(name = "geolocationOUT_id", referencedColumnName = "id")
    private Geolocation geolocationOUT;

    private String mobileOSOUT; // android or IOS
    private String mobileModelOUT; //iphone15 iphone16 samsung galaxy 24 ...

    @Column(nullable = true)
    private LocalTime checkIn;

    @Column(nullable = true)
    private LocalTime checkOut;

    private int totalMinutes;

    @Column(nullable = false)
    private LocalDate dayDate;


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

    public Geolocation getGeolocation() {
        return geolocation;
    }

    public void setGeolocation(Geolocation geolocation) {
        this.geolocation = geolocation;
    }

    public LocalTime getCheckIn() {
        return checkIn;
    }

    public void setCheckIn(LocalTime checkIn) {
        this.checkIn = checkIn;
    }

    public LocalTime getCheckOut() {
        return checkOut;
    }

    public void setCheckOut(LocalTime checkOut) {
        this.checkOut = checkOut;
    }

    public LocalDate getDayDate() {
        return dayDate;
    }

    public void setDayDate(LocalDate dayDate) {
        this.dayDate = dayDate;
    }

    public String getMobileOS() {
        return mobileOS;
    }

    public void setMobileOS(String mobileOS) {
        this.mobileOS = mobileOS;
    }

    public String getMobileModel() {
        return mobileModel;
    }

    public void setMobileModel(String mobileModel) {
        this.mobileModel = mobileModel;
    }

    public int getTotalMinutes() {
        return totalMinutes;
    }

    public void setTotalMinutes(int totalMinutes) {
        this.totalMinutes = totalMinutes;
    }

    public Geolocation getGeolocationOUT() {
        return geolocationOUT;
    }

    public void setGeolocationOUT(Geolocation geolocationOUT) {
        this.geolocationOUT = geolocationOUT;
    }

    public String getMobileOSOUT() {
        return mobileOSOUT;
    }

    public void setMobileOSOUT(String mobileOSOUT) {
        this.mobileOSOUT = mobileOSOUT;
    }

    public String getMobileModelOUT() {
        return mobileModelOUT;
    }

    public void setMobileModelOUT(String mobileModelOUT) {
        this.mobileModelOUT = mobileModelOUT;
    }
}
