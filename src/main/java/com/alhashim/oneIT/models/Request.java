package com.alhashim.oneIT.models;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name="requests")
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "requested_by", nullable = false)
    private Employee requestedBy;



    private String category;
    private String justification;
    private String status;

    private Boolean requiredManagerApproval;
    private Boolean requiredAdminApproval;
    private Boolean requiredHRApproval;

    private Boolean managerApproval;
    private Boolean adminApproval;
    private Boolean hrApproval;


    @OneToMany(mappedBy = "request", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments;

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

    public Employee getRequestedBy() {
        return requestedBy;
    }

    public void setRequestedBy(Employee requestedBy) {
        this.requestedBy = requestedBy;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getJustification() {
        return justification;
    }

    public void setJustification(String justification) {
        this.justification = justification;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getRequiredManagerApproval() {
        return requiredManagerApproval;
    }

    public void setRequiredManagerApproval(Boolean requiredManagerApproval) {
        this.requiredManagerApproval = requiredManagerApproval;
    }

    public Boolean getRequiredAdminApproval() {
        return requiredAdminApproval;
    }

    public void setRequiredAdminApproval(Boolean requiredAdminApproval) {
        this.requiredAdminApproval = requiredAdminApproval;
    }

    public Boolean getRequiredHRApproval() {
        return requiredHRApproval;
    }

    public void setRequiredHRApproval(Boolean requiredHRApproval) {
        this.requiredHRApproval = requiredHRApproval;
    }

    public Boolean getManagerApproval() {
        return managerApproval;
    }

    public void setManagerApproval(Boolean managerApproval) {
        this.managerApproval = managerApproval;
    }

    public Boolean getAdminApproval() {
        return adminApproval;
    }

    public void setAdminApproval(Boolean adminApproval) {
        this.adminApproval = adminApproval;
    }

    public Boolean getHrApproval() {
        return hrApproval;
    }

    public void setHrApproval(Boolean hrApproval) {
        this.hrApproval = hrApproval;
    }
}
