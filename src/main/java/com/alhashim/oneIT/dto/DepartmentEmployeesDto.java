package com.alhashim.oneIT.dto;

import java.math.BigDecimal;
import java.util.List;
public class DepartmentEmployeesDto {
    private Long id;
    private String badgeNumber;
    private String name;
    private String bankName;
    private String iban;
    private BigDecimal basicSalary;
    private List<BenefitDto> benefits;

    public DepartmentEmployeesDto(Long id, String badgeNumber, String name, String bankName, String iban, BigDecimal basicSalary) {
        this.id = id;
        this.badgeNumber = badgeNumber;
        this.name = name;
        this.bankName = bankName;
        this.iban = iban;
        this.basicSalary = basicSalary;

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBadgeNumber() {
        return badgeNumber;
    }

    public void setBadgeNumber(String badgeNumber) {
        this.badgeNumber = badgeNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    public BigDecimal getBasicSalary() {
        return basicSalary;
    }

    public void setBasicSalary(BigDecimal basicSalary) {
        this.basicSalary = basicSalary;
    }

    public List<BenefitDto> getBenefits() {
        return benefits;
    }

    public void setBenefits(List<BenefitDto> benefits) {
        this.benefits = benefits;
    }
}
