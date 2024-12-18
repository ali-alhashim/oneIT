package com.alhashim.oneIT.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class SalaryDto {
    private String badgeNumber;
    private BigDecimal grossEarning;

    private BigDecimal grossDeduction;
    private BigDecimal netPay;

    private List<SalaryLineDto> lines = new ArrayList<>();

    public String getBadgeNumber() {
        return badgeNumber;
    }

    public void setBadgeNumber(String badgeNumber) {
        this.badgeNumber = badgeNumber;
    }

    public BigDecimal getGrossEarning() {
        return grossEarning;
    }

    public void setGrossEarning(BigDecimal grossEarning) {
        this.grossEarning = grossEarning;
    }

    public List<SalaryLineDto> getLines() {
        return lines;
    }

    public void setLines(List<SalaryLineDto> lines) {
        this.lines = lines;
    }

    public BigDecimal getGrossDeduction() {
        return grossDeduction;
    }

    public void setGrossDeduction(BigDecimal grossDeduction) {
        this.grossDeduction = grossDeduction;
    }

    public BigDecimal getNetPay() {
        return netPay;
    }

    public void setNetPay(BigDecimal netPay) {
        this.netPay = netPay;
    }
}
