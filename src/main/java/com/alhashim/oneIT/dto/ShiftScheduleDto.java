package com.alhashim.oneIT.dto;

import java.time.LocalTime;

public class ShiftScheduleDto {

    private String name;

    private LocalTime startTime;
    private LocalTime endTime;

    private Boolean sundayWork;
    private Boolean mondayWork;
    private Boolean tuesdayWork;
    private Boolean wednesdayWork;
    private Boolean thursdayWork;
    private Boolean fridayWork;
    private Boolean saturdayWork;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public Boolean getSundayWork() {
        return sundayWork;
    }

    public void setSundayWork(Boolean sundayWork) {
        this.sundayWork = sundayWork;
    }

    public Boolean getMondayWork() {
        return mondayWork;
    }

    public void setMondayWork(Boolean mondayWork) {
        this.mondayWork = mondayWork;
    }

    public Boolean getTuesdayWork() {
        return tuesdayWork;
    }

    public void setTuesdayWork(Boolean tuesdayWork) {
        this.tuesdayWork = tuesdayWork;
    }

    public Boolean getWednesdayWork() {
        return wednesdayWork;
    }

    public void setWednesdayWork(Boolean wednesdayWork) {
        this.wednesdayWork = wednesdayWork;
    }

    public Boolean getThursdayWork() {
        return thursdayWork;
    }

    public void setThursdayWork(Boolean thursdayWork) {
        this.thursdayWork = thursdayWork;
    }

    public Boolean getFridayWork() {
        return fridayWork;
    }

    public void setFridayWork(Boolean fridayWork) {
        this.fridayWork = fridayWork;
    }

    public Boolean getSaturdayWork() {
        return saturdayWork;
    }

    public void setSaturdayWork(Boolean saturdayWork) {
        this.saturdayWork = saturdayWork;
    }
}
