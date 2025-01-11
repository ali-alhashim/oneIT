package com.alhashim.oneIT.dto;

import org.springframework.web.multipart.MultipartFile;

public class DocumentDto {

    private MultipartFile documentFile;
    private String badgeNumber;

    private String type;

    private String description;

    public MultipartFile getDocumentFile() {
        return documentFile;
    }

    public void setDocumentFile(MultipartFile documentFile) {
        this.documentFile = documentFile;
    }

    public String getBadgeNumber() {
        return badgeNumber;
    }

    public void setBadgeNumber(String badgeNumber) {
        this.badgeNumber = badgeNumber;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
