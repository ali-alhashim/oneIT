package com.alhashim.oneIT.dto;

import java.math.BigDecimal;

public class BenefitDto {

    private String payDescription;
    private BigDecimal toPay;
    private BigDecimal  toDeduct;

    public BenefitDto(String payDescription, BigDecimal toPay, BigDecimal toDeduct) {
        this.payDescription = payDescription;
        this.toPay = toPay;
        this.toDeduct = toDeduct;
    }

    public String getPayDescription() {
        return payDescription;
    }

    public void setPayDescription(String payDescription) {
        this.payDescription = payDescription;
    }

    public BigDecimal getToPay() {
        return toPay;
    }

    public void setToPay(BigDecimal toPay) {
        this.toPay = toPay;
    }

    public BigDecimal getToDeduct() {
        return toDeduct;
    }

    public void setToDeduct(BigDecimal toDeduct) {
        this.toDeduct = toDeduct;
    }
}
