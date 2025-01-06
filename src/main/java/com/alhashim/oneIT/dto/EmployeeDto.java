package com.alhashim.oneIT.dto;

import jakarta.validation.constraints.NotEmpty;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

public class EmployeeDto {

    @NotEmpty(message = "The name is required")
    private String name;

    private String badgeNumber;

    private String arName;

    private String workEmail;

    private String personalEmail;

    private String officeLocation;

    private Long department;


    private MultipartFile imageFile;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date birthDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date hireDate;


    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date terminationDate;

    private String govId;

    private String gender;

    private String status;

    private String businessUnit;

    private String maritalStatus;

    private String sponsorName;
    private String arJobTitle;
    private String jobTitle;
    private String citizenship;


    private String password;

    private boolean is_USER;
    private boolean is_MANAGER;
    private boolean is_SUPPORT;
    private boolean is_ADMIN;
    private boolean is_HR;
    private Boolean is_PROCUREMENT;


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

    public String getArName() {
        return arName;
    }

    public void setArName(String arName) {
        this.arName = arName;
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

    public  String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isIs_USER() {
        return is_USER;
    }

    public Boolean getIs_PROCUREMENT() {
        return is_PROCUREMENT;
    }

    public void setIs_PROCUREMENT(Boolean is_PROCUREMENT) {
        this.is_PROCUREMENT = is_PROCUREMENT;
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

    public boolean isIs_ADMIN() {
        return is_ADMIN;
    }

    public void setIs_ADMIN(boolean is_ADMIN) {
        this.is_ADMIN = is_ADMIN;
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

    public boolean isIs_HR() {
        return is_HR;
    }

    public void setIs_HR(boolean is_HR) {
        this.is_HR = is_HR;
    }

    @Override
    public String toString()
    {
        return "{BadgeNumber : "+this.getBadgeNumber() +","
                +"Name:" + this.getName() +","
                +"ArName:" + this.getArName() +","
                +"}";
    }

    public Date getHireDate() {
        return hireDate;
    }

    public void setHireDate(Date hireDate) {
        this.hireDate = hireDate;
    }

    public Date getTerminationDate() {
        return terminationDate;
    }

    public void setTerminationDate(Date terminationDate) {
        this.terminationDate = terminationDate;
    }

    public String getGovId() {
        return govId;
    }

    public void setGovId(String govId) {
        this.govId = govId;
    }

    public String getBusinessUnit() {
        return businessUnit;
    }

    public void setBusinessUnit(String businessUnit) {
        this.businessUnit = businessUnit;
    }

    public String getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(String maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public String getSponsorName() {
        return sponsorName;
    }

    public void setSponsorName(String sponsorName) {
        this.sponsorName = sponsorName;
    }

    public String getArJobTitle() {
        return arJobTitle;
    }

    public void setArJobTitle(String arJobTitle) {
        this.arJobTitle = arJobTitle;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getCitizenship() {
        return citizenship;
    }

    public void setCitizenship(String citizenship) {
        this.citizenship = citizenship;
    }
}
