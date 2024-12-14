package com.alhashim.oneIT.dto;

import java.math.BigDecimal;

public class InvoiceLineDto {

    private String description;
    private int quantity;
    private BigDecimal unitPrice;
    private String percentageVAT; //5% 10% 15%..
    private BigDecimal totalPrice;
    private BigDecimal unitVAT;
    private BigDecimal totalVAT;
    private BigDecimal totalPriceWithVAT;

    // Getters and Setters


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getPercentageVAT() {
        return percentageVAT;
    }

    public void setPercentageVAT(String percentageVAT) {
        this.percentageVAT = percentageVAT;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public BigDecimal getUnitVAT() {
        return unitVAT;
    }

    public void setUnitVAT(BigDecimal unitVAT) {
        this.unitVAT = unitVAT;
    }

    public BigDecimal getTotalVAT() {
        return totalVAT;
    }

    public void setTotalVAT(BigDecimal totalVAT) {
        this.totalVAT = totalVAT;
    }

    public BigDecimal getTotalPriceWithVAT() {
        return totalPriceWithVAT;
    }

    public void setTotalPriceWithVAT(BigDecimal totalPriceWithVAT) {
        this.totalPriceWithVAT = totalPriceWithVAT;
    }
}
