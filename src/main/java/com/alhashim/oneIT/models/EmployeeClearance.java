package com.alhashim.oneIT.models;


import jakarta.persistence.*;
import org.springframework.data.annotation.LastModifiedDate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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
    private String employeeSignatureFileName;

    private LocalDate lastDay;
    private LocalDate hireDate;

    private String articleNumber;
    private int totalDays;

    private BigDecimal actualWage;

    private String contractType;

    private BigDecimal rewardAmount;

    private String status; //waiting clearances from departments, processing the payment, Done

    @OneToOne
    private Employee directManager;
    private Boolean  directManagerOk;
    private String managerSignatureFileName;


    @OneToOne
    private Employee hr;
    private Boolean hrOk;
    private String hrSignatureFileName;

    @OneToOne
    private Employee it;
    private Boolean itOk;
    private String itSignatureFileName;

    @OneToOne
    private Employee medical;
    private Boolean medicalOk;
    private String medicalSignatureFileName;

    @OneToOne
    private Employee vehicle;
    private Boolean vehicleOk;
    private String vehicleSignatureFileName;

    @OneToOne
    private Employee finance;
    private Boolean financeOk;
    private String financeSignatureFileName;


    @OneToMany(mappedBy = "employeeClearance")
    private List<EmployeeClearanceLine> lines;

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

    public String getEmployeeSignatureFileName() {
        return employeeSignatureFileName;
    }

    public void setEmployeeSignatureFileName(String employeeSignatureFileName) {
        this.employeeSignatureFileName = employeeSignatureFileName;
    }

    public String getArticleNumber() {
        return articleNumber;
    }

    public void setArticleNumber(String articleNumber) {
        this.articleNumber = articleNumber;
    }

    public int getTotalDays() {
        return totalDays;
    }

    public void setTotalDays(int totalDays) {
        this.totalDays = totalDays;
    }

    public BigDecimal getRewardAmount() {
        return rewardAmount;
    }

    public void setRewardAmount(BigDecimal rewardAmount) {
        this.rewardAmount = rewardAmount;
    }

    public String getManagerSignatureFileName() {
        return managerSignatureFileName;
    }

    public void setManagerSignatureFileName(String managerSignatureFileName) {
        this.managerSignatureFileName = managerSignatureFileName;
    }

    public String getHrSignatureFileName() {
        return hrSignatureFileName;
    }

    public void setHrSignatureFileName(String hrSignatureFileName) {
        this.hrSignatureFileName = hrSignatureFileName;
    }

    public String getItSignatureFileName() {
        return itSignatureFileName;
    }

    public void setItSignatureFileName(String itSignatureFileName) {
        this.itSignatureFileName = itSignatureFileName;
    }

    public String getMedicalSignatureFileName() {
        return medicalSignatureFileName;
    }

    public void setMedicalSignatureFileName(String medicalSignatureFileName) {
        this.medicalSignatureFileName = medicalSignatureFileName;
    }

    public String getVehicleSignatureFileName() {
        return vehicleSignatureFileName;
    }

    public void setVehicleSignatureFileName(String vehicleSignatureFileName) {
        this.vehicleSignatureFileName = vehicleSignatureFileName;
    }

    public String getFinanceSignatureFileName() {
        return financeSignatureFileName;
    }

    public void setFinanceSignatureFileName(String financeSignatureFileName) {
        this.financeSignatureFileName = financeSignatureFileName;
    }

    public LocalDate getHireDate() {
        return hireDate;
    }

    public void setHireDate(LocalDate hireDate) {
        this.hireDate = hireDate;
    }

    public List<EmployeeClearanceLine> getLines() {
        return lines;
    }

    public void setLines(List<EmployeeClearanceLine> lines) {
        this.lines = lines;
    }

    public BigDecimal getActualWage() {
        return actualWage;
    }

    public void setActualWage(BigDecimal actualWage) {
        this.actualWage = actualWage;
    }

    public String getContractType() {
        return contractType;
    }

    public void setContractType(String contractType) {
        this.contractType = contractType;
    }
}
