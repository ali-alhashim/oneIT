package com.alhashim.oneIT.dto;

import jakarta.validation.constraints.NotEmpty;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.Date;

public class DeviceDto {

    @NotEmpty(message = "The Serial Number is required")
    private String serialNumber;

    private MultipartFile imageFile;

    private String manufacture;

    private String status;

    private String category;

    private String model;

    private String description;

    private BigDecimal purchasePrice;

    private String invoiceNumber;

    private String badgeNumber;

    private String poNumber;

    private Long deviceVendor;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date acquisitionDate;

    public @NotEmpty(message = "The Serial Number is required") String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(@NotEmpty(message = "The Serial Number is required") String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public MultipartFile getImageFile() {
        return imageFile;
    }

    public void setImageFile(MultipartFile imageFile) {
        this.imageFile = imageFile;
    }

    public String getManufacture() {
        return manufacture;
    }

    public void setManufacture(String manufacture) {
        this.manufacture = manufacture;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(BigDecimal purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getPoNumber() {
        return poNumber;
    }

    public void setPoNumber(String poNumber) {
        this.poNumber = poNumber;
    }

    public Date getAcquisitionDate() {
        return acquisitionDate;
    }

    public void setAcquisitionDate(Date acquisitionDate) {
        this.acquisitionDate = acquisitionDate;
    }

    public String getBadgeNumber() {
        return badgeNumber;
    }

    public void setBadgeNumber(String badgeNumber) {
        this.badgeNumber = badgeNumber;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Long getDeviceVendor() {
        return deviceVendor;
    }

    public void setDeviceVendor(Long deviceVendor) {
        this.deviceVendor = deviceVendor;
    }
}
