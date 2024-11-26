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

    private String description;

    private BigDecimal purchasePrice;

    private String invoiceNumber;

    private String poNumber;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date acquisitionDate;
}
