package com.alhashim.oneIT.dto;

import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PurchaseOrderDto {

    private Long vendorId;
    private Long contactId;
    private BigDecimal totalVAT;
    private BigDecimal totalPriceWithVAT;
    private String status;

    private String deliveryAddress;
    private String paymentTerms;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date deadLine;
    private String documentRef;
    private List<PurchaseOrderLineDto> lines = new ArrayList<>();

    // Getters and Setters


    public Long getVendorId() {
        return vendorId;
    }

    public void setVendorId(Long vendorId) {
        this.vendorId = vendorId;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getDeadLine() {
        return deadLine;
    }

    public void setDeadLine(Date deadLine) {
        this.deadLine = deadLine;
    }

    public String getDocumentRef() {
        return documentRef;
    }

    public void setDocumentRef(String documentRef) {
        this.documentRef = documentRef;
    }

    public List<PurchaseOrderLineDto> getLines() {
        return lines;
    }

    public void setLines(List<PurchaseOrderLineDto> lines) {
        this.lines = lines;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public Long getContactId() {
        return contactId;
    }

    public void setContactId(Long contactId) {
        this.contactId = contactId;
    }

    public String getPaymentTerms() {
        return paymentTerms;
    }

    public void setPaymentTerms(String paymentTerms) {
        this.paymentTerms = paymentTerms;
    }
}


