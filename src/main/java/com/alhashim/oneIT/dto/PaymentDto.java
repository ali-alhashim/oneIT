package com.alhashim.oneIT.dto;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.Date;

public class PaymentDto {

    private Long invoiceId;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date ttDocumentDate;

    private BigDecimal Amount;

    private String bankName;

    private String iban;

    private MultipartFile pdfFile;

    private String paymentMethod;

    public Long getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(Long invoiceId) {
        this.invoiceId = invoiceId;
    }

    public Date getTtDocumentDate() {
        return ttDocumentDate;
    }

    public void setTtDocumentDate(Date ttDocumentDate) {
        this.ttDocumentDate = ttDocumentDate;
    }

    public BigDecimal getAmount() {
        return Amount;
    }

    public void setAmount(BigDecimal amount) {
        Amount = amount;
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

    public MultipartFile getPdfFile() {
        return pdfFile;
    }

    public void setPdfFile(MultipartFile pdfFile) {
        this.pdfFile = pdfFile;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}
