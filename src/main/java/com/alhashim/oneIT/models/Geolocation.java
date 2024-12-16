package com.alhashim.oneIT.models;

import jakarta.persistence.*;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Entity
public class Geolocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private String areaName;

    @Column(nullable = false)
    private Double latitudeA;

    @Column(nullable = false)
    private Double longitudeA;

    @Column(nullable = false)
    private Double latitudeB;

    @Column(nullable = false)
    private Double longitudeB;

    @Column(nullable = false)
    private Double latitudeC;

    @Column(nullable = false)
    private Double longitudeC;

    @Column(nullable = false)
    private Double latitudeD;

    @Column(nullable = false)
    private Double longitudeD;


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

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public Double getLatitudeA() {
        return latitudeA;
    }

    public void setLatitudeA(Double latitudeA) {
        this.latitudeA = latitudeA;
    }

    public Double getLongitudeA() {
        return longitudeA;
    }

    public void setLongitudeA(Double longitudeA) {
        this.longitudeA = longitudeA;
    }

    public Double getLatitudeB() {
        return latitudeB;
    }

    public void setLatitudeB(Double latitudeB) {
        this.latitudeB = latitudeB;
    }

    public Double getLongitudeB() {
        return longitudeB;
    }

    public void setLongitudeB(Double longitudeB) {
        this.longitudeB = longitudeB;
    }

    public Double getLatitudeC() {
        return latitudeC;
    }

    public void setLatitudeC(Double latitudeC) {
        this.latitudeC = latitudeC;
    }

    public Double getLongitudeC() {
        return longitudeC;
    }

    public void setLongitudeC(Double longitudeC) {
        this.longitudeC = longitudeC;
    }

    public Double getLatitudeD() {
        return latitudeD;
    }

    public void setLatitudeD(Double latitudeD) {
        this.latitudeD = latitudeD;
    }

    public Double getLongitudeD() {
        return longitudeD;
    }

    public void setLongitudeD(Double longitudeD) {
        this.longitudeD = longitudeD;
    }
}
