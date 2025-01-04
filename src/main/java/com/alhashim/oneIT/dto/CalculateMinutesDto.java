package com.alhashim.oneIT.dto;

import java.math.BigDecimal;

public class CalculateMinutesDto {

    private BigDecimal deductedBasicSalary;
    private int totalMM;

    public CalculateMinutesDto(BigDecimal deductedBasicSalary, int totalMM) {
        this.deductedBasicSalary = deductedBasicSalary;
        this.totalMM = totalMM;
    }

    public CalculateMinutesDto() {

    }

    public BigDecimal getDeductedBasicSalary() {
        return deductedBasicSalary;
    }

    public void setDeductedBasicSalary(BigDecimal deductedBasicSalary) {
        this.deductedBasicSalary = deductedBasicSalary;
    }

    public int getTotalMM() {
        return totalMM;
    }

    public void setTotalMM(int totalMM) {
        this.totalMM = totalMM;
    }
}
