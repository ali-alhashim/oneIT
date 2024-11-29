package com.alhashim.oneIT.models;

import jakarta.persistence.*;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "assets")
public class Asset {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "device_id", nullable = false)
    private Device device;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(unique = true)
    private String code;


    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    private Date handoverDate;
    private Date receivedDate;


    private boolean confirmReceived;

    private LocalDateTime confirmationDate;

    private String signatureFileName;

    private boolean confirmHandover;

    private String note;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
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

    public Date getHandoverDate() {
        return handoverDate;
    }

    public void setHandoverDate(Date handoverDate) {
        this.handoverDate = handoverDate;
    }

    public Date getReceivedDate() {
        return receivedDate;
    }

    public void setReceivedDate(Date receivedDate) {
        this.receivedDate = receivedDate;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public boolean isConfirmReceived() {
        return confirmReceived;
    }

    public void setConfirmReceived(boolean confirmReceived) {
        this.confirmReceived = confirmReceived;
    }

    public boolean isConfirmHandover() {
        return confirmHandover;
    }

    public void setConfirmHandover(boolean confirmHandover) {
        this.confirmHandover = confirmHandover;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getSignatureFileName() {
        return signatureFileName;
    }

    public void setSignatureFileName(String signatureFileName) {
        this.signatureFileName = signatureFileName;
    }

    public LocalDateTime getConfirmationDate() {
        return confirmationDate;
    }

    public void setConfirmationDate(LocalDateTime confirmationDate) {
        this.confirmationDate = confirmationDate;
    }
}
