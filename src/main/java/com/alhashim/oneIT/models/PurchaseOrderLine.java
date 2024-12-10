package com.alhashim.oneIT.models;


import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "Purchase_orders_lines")
public class PurchaseOrderLine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL)
    private PurchaseOrder purchaseOrder;

    private String description;
    private int quantity;
    private BigDecimal unitPrice;
    private String percentageVAT; //5% 10% 15%..
    private BigDecimal totalPrice;
    private BigDecimal unitVAT;
    private BigDecimal totalVAT;
    private BigDecimal totalPriceWithVAT;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PurchaseOrder getPurchaseOrder() {
        return purchaseOrder;
    }

    public void setPurchaseOrder(PurchaseOrder purchaseOrder) {
        this.purchaseOrder = purchaseOrder;
    }

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
