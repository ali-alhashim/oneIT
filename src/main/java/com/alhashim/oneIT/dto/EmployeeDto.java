package com.alhashim.oneIT.dto;

import jakarta.validation.constraints.NotEmpty;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

public class EmployeeDto {

    @NotEmpty(message = "The name is required")
    private String name;

    private String badgeNumber;

    private String ar_name;

    private String workEmail;

    private String personalEmail;

    private String officeLocation;

    private Long department;


    private MultipartFile imageFile;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date birthDate;

    private String gender;

    private String status;

    @NotEmpty(message = "set password is required")
    private String password;

    private boolean is_USER;
    private boolean is_MANAGER;
    private boolean is_SUPPORT;
    private boolean is_SUPERADMIN;


    private String personalMobile;
    private String workMobile;

    public @NotEmpty(message = "The name is required") String getName() {
        return name;
    }

    public void setName(@NotEmpty(message = "The name is required") String name) {
        this.name = name;
    }

    public String getBadgeNumber() {
        return badgeNumber;
    }

    public void setBadgeNumber(String badgeNumber) {
        this.badgeNumber = badgeNumber;
    }

    public String getAr_name() {
        return ar_name;
    }

    public void setAr_name(String ar_name) {
        this.ar_name = ar_name;
    }

    public String getWorkEmail() {
        return workEmail;
    }

    public void setWorkEmail(String workEmail) {
        this.workEmail = workEmail;
    }

    public String getPersonalEmail() {
        return personalEmail;
    }

    public void setPersonalEmail(String personalEmail) {
        this.personalEmail = personalEmail;
    }

    public String getOfficeLocation() {
        return officeLocation;
    }

    public void setOfficeLocation(String officeLocation) {
        this.officeLocation = officeLocation;
    }

    public MultipartFile getImageFile() {
        return imageFile;
    }

    public void setImageFile(MultipartFile imageFile) {
        this.imageFile = imageFile;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public @NotEmpty(message = "set password is required") String getPassword() {
        return password;
    }

    public void setPassword(@NotEmpty(message = "set password is required") String password) {
        this.password = password;
    }

    public boolean isIs_USER() {
        return is_USER;
    }

    public void setIs_USER(boolean is_USER) {
        this.is_USER = is_USER;
    }

    public boolean isIs_MANAGER() {
        return is_MANAGER;
    }

    public void setIs_MANAGER(boolean is_MANAGER) {
        this.is_MANAGER = is_MANAGER;
    }

    public boolean isIs_SUPPORT() {
        return is_SUPPORT;
    }

    public void setIs_SUPPORT(boolean is_SUPPORT) {
        this.is_SUPPORT = is_SUPPORT;
    }

    public boolean isIs_SUPERADMIN() {
        return is_SUPERADMIN;
    }

    public void setIs_SUPERADMIN(boolean is_SUPERADMIN) {
        this.is_SUPERADMIN = is_SUPERADMIN;
    }

    public String getPersonalMobile() {
        return personalMobile;
    }

    public void setPersonalMobile(String personalMobile) {
        this.personalMobile = personalMobile;
    }

    public String getWorkMobile() {
        return workMobile;
    }

    public void setWorkMobile(String workMobile) {
        this.workMobile = workMobile;
    }

    public Long getDepartment() {
        return department;
    }

    public void setDepartment(Long department) {
        this.department = department;
    }
}
