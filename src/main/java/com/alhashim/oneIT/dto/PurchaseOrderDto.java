package com.alhashim.oneIT.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PurchaseOrderDto {

    private Long vendorId;
    private BigDecimal totalVAT;
    private BigDecimal totalPriceWithVAT;
    private String status;
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
}


