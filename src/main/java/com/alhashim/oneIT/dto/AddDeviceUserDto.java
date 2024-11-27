package com.alhashim.oneIT.dto;

import org.springframework.format.annotation.DateTimeFormat;


import java.util.Date;

public class AddDeviceUserDto {

    private String badgeNumber;
    private String serialNumber;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date receivedDate;




    public String getBadgeNumber() {
        return badgeNumber;
    }

    public void setBadgeNumber(String badgeNumber) {
        this.badgeNumber = badgeNumber;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public Date getReceivedDate() {
        return receivedDate;
    }

    public void setReceivedDate(Date receivedDate) {
        this.receivedDate = receivedDate;
    }
}
