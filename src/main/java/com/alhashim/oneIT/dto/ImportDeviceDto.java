package com.alhashim.oneIT.dto;

import org.springframework.web.multipart.MultipartFile;

public class ImportDeviceDto {

    private MultipartFile csvFile;

    public MultipartFile getCsvFile() {
        return csvFile;
    }

    public void setCsvFile(MultipartFile csvFile) {
        this.csvFile = csvFile;
    }
}