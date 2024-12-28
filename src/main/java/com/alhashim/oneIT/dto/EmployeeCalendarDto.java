package com.alhashim.oneIT.dto;

import com.alhashim.oneIT.models.EmployeeCalendar;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.time.LocalTime;

public class EmployeeCalendarDto {

    @JsonProperty("dayDate")
    private String dayDate;

    @JsonProperty("checkIn")
    private String checkIn;

    @JsonProperty("checkOut")
    private String checkOut;

    @JsonProperty("totalMinutes")
    private String totalMinutes;

    public EmployeeCalendarDto(EmployeeCalendar calendar) {
        this.dayDate = calendar.getDayDate().toString();
        this.checkIn = calendar.getCheckIn().toString();
        this.checkOut = (calendar.getCheckOut() != null) ? calendar.getCheckOut().toString() : "N/A";
        this.totalMinutes =  String.valueOf(calendar.getTotalMinutes());
    }

    // Getters (for Jackson to serialize)
    public String getDayDate() {
        return dayDate;
    }

    public String getCheckIn() {
        return checkIn;
    }

    public String getCheckOut() {
        return checkOut;
    }

    public String getTotalMinutes() {
        return totalMinutes;
    }


    @Override
    public String toString() {
        return "EmployeeCalendarDto{" +
                "dayDate='" + dayDate + '\'' +
                ", checkIn='" + checkIn + '\'' +
                ", checkOut='" + checkOut + '\'' +
                ", totalMinutes='" + totalMinutes + '\'' +
                '}';
    }

}
