package com.alhashim.oneIT.models;


import jakarta.persistence.*;
import org.springframework.data.annotation.LastModifiedDate;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.util.*;

@Entity
@Table(name = "employees")
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String badgeNumber; //this is the username

    private String name;

    private String arName;


    private String workEmail;


    private String personalEmail;

    private String emergencyContactName;
    private String emergencyContactMobile;


    private String workMobile;


    private String personalMobile;

    private String imageFileName;

    private String status;

    private String iban;
    private String bankName;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    private String educationTitle;


    private Date birthDate;

    private String password;

    private String officeLocation;

    private String gender;

    private String citizenship;

    private String jobTitle;

    private String arJobTitle;

    private String sponsorName;

    private Date terminationDate;

    private String govId;

    private String refId;

    private Date hireDate;

    private String businessUnit;

    private int annualLeaveBalance;

    private String maritalStatus; // M / D / V / C


    //for MFA with Google auth app or similar Microsoft auth
    private String otpCode;

    private Boolean isOtpEnabled;

    private Boolean isActive;



//-------- the Relationship filed-----------



    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "department_id")
    private Department department;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "shiftSchedule_id")
    private ShiftSchedule shiftSchedule;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Salary> salaries;


    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "employee_roles",
            joinColumns = @JoinColumn(name = "employee_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();


    public Boolean isAdmin() {
        return this.getRoles().stream().anyMatch(role -> role.getRoleName().equalsIgnoreCase("ADMIN"));
    }



    public Boolean isHR() {
        return this.getRoles().stream().anyMatch(role -> role.getRoleName().equalsIgnoreCase("HR"));
    }

    public Boolean isSupport() {
        return this.getRoles().stream().anyMatch(role -> role.getRoleName().equalsIgnoreCase("SUPPORT"));
    }

    public Boolean isProcurement()
    {
        return this.getRoles().stream().anyMatch(role -> role.getRoleName().equalsIgnoreCase("PROCUREMENT"));
    }

    public Boolean isMedical()
    {
        return this.getRoles().stream().anyMatch(role -> role.getRoleName().equalsIgnoreCase("MEDICAL"));
    }

    public Boolean isVehicle()
    {
        return this.getRoles().stream().anyMatch(role -> role.getRoleName().equalsIgnoreCase("VEHICLE"));
    }

    public Boolean isFinance()
    {
        return this.getRoles().stream().anyMatch(role -> role.getRoleName().equalsIgnoreCase("FINANCE"));
    }


    @OneToMany(mappedBy = "employee")
    private List<Notification> notifications;

    @OneToMany(mappedBy = "employee")
    private List<Payslip> payslips;

    @OneToMany(mappedBy = "employee")
    private List<EmployeeCalendar> calendars;


    @OneToMany(mappedBy = "createdBy")
    private List<PurchaseOrder> purchaseOrders;

    @OneToMany(mappedBy = "employee")
    private List<EmployeeEducation> educations;


    @OneToMany(mappedBy = "user")
    private List<Device> devices; // Multiple devices per employee


    @OneToMany(mappedBy = "requestedBy")
    private List<Request> requests; // Multiple request per employee


    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Asset> assets;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Holiday> holidays;


    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SystemLog> systemLogs;


    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Document> documents; // Associated documents

    //--------------


    public List<Asset> getAssets() {
        return assets;
    }

    public void setAssets(List<Asset> assets) {
        this.assets = assets;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBadgeNumber() {
        return badgeNumber;
    }

    public void setBadgeNumber(String badgeNumber) {
        this.badgeNumber = badgeNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArName() {
        return arName;
    }

    public void setArName(String ar_name) {
        this.arName = ar_name;
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

    public String getWorkMobile() {
        return workMobile;
    }

    public void setWorkMobile(String workMobile) {
        this.workMobile = workMobile;
    }

    public String getPersonalMobile() {
        return personalMobile;
    }

    public void setPersonalMobile(String personalMobile) {
        this.personalMobile = personalMobile;
    }

    public String getImageFileName() {
        return imageFileName;
    }

    public void setImageFileName(String imageFileName) {
        this.imageFileName = imageFileName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }



    public String getOfficeLocation() {
        return officeLocation;
    }

    public void setOfficeLocation(String officeLocation) {
        this.officeLocation = officeLocation;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getOtpCode() {
        return otpCode;
    }

    public void setOtpCode(String otpCode) {
        this.otpCode = otpCode;
    }


    public List<Device> getDevices() {
        return devices;
    }

    public void setDevices(List<Device> devices) {
        this.devices = devices;
    }


    public List<Document> getDocuments() {
        return documents;
    }

    public void setDocuments(List<Document> documents) {
        this.documents = documents;
    }

    public List<Request> getRequests() {
        return requests;
    }

    public void setRequests(List<Request> requests) {
        this.requests = requests;
    }

    public List<SystemLog> getSystemLogs() {
        return systemLogs;
    }

    public void setSystemLogs(List<SystemLog> systemLogs) {
        this.systemLogs = systemLogs;
    }

    public List<Notification> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<Notification> notifications) {
        this.notifications = notifications;
    }

    public Boolean isOtpEnabled() {
        return isOtpEnabled;
    }

    public void setOtpEnabled(Boolean otpEnabled) {
        this.isOtpEnabled = otpEnabled;
    }

    public List<PurchaseOrder> getPurchaseOrders() {
        return purchaseOrders;
    }

    public void setPurchaseOrders(List<PurchaseOrder> purchaseOrders) {
        this.purchaseOrders = purchaseOrders;
    }


    public String getCitizenship() {
        return citizenship;
    }

    public void setCitizenship(String citizenship) {
        this.citizenship = citizenship;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getArJobTitle() {
        return arJobTitle;
    }

    public void setArJobTitle(String arJobTitle) {
        this.arJobTitle = arJobTitle;
    }

    public String getSponsorName() {
        return sponsorName;
    }

    public void setSponsorName(String sponsorName) {
        this.sponsorName = sponsorName;
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

    public Date getHireDate() {
        return hireDate;
    }

    public void setHireDate(Date hireDate) {
        this.hireDate = hireDate;
    }

    public String getBusinessUnit() {
        return businessUnit;
    }

    public void setBusinessUnit(String businessUnit) {
        this.businessUnit = businessUnit;
    }

    public int getAnnualLeaveBalance() {
        return annualLeaveBalance;
    }

    public void setAnnualLeaveBalance(int annualLeaveBalance) {
        this.annualLeaveBalance = annualLeaveBalance;
    }

    public String getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(String maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public Boolean getOtpEnabled() {
        return isOtpEnabled;
    }

    public List<Salary> getSalaries() {
        return salaries;
    }

    public void setSalaries(List<Salary> salaries) {
        this.salaries = salaries;
    }

    public List<EmployeeCalendar> getCalendars() {
        return calendars;
    }

    public void setCalendars(List<EmployeeCalendar> calendars) {
        this.calendars = calendars;
    }

    public List<EmployeeEducation> getEducations() {
        return educations;
    }

    public void setEducations(List<EmployeeEducation> educations) {
        this.educations = educations;
    }


    public List<Payslip> getPayslips() {
        return payslips;
    }

    public void setPayslips(List<Payslip> payslips) {
        this.payslips = payslips;
    }

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public ShiftSchedule getShiftSchedule() {
        return shiftSchedule;
    }

    public void setShiftSchedule(ShiftSchedule shiftSchedule) {
        this.shiftSchedule = shiftSchedule;
    }

    public List<Holiday> getHolidays() {
        return holidays;
    }

    public void setHolidays(List<Holiday> holidays) {
        this.holidays = holidays;
    }

    public Boolean getActive() {
        return this.isActive;
    }

    public void setActive(Boolean active) {
        this.isActive = active;
    }


    public Salary getCurrentSalary() {
        return salaries.stream()
                .sorted((s1, s2) -> s2.getCreatedAt().compareTo(s1.getCreatedAt()))  // Sort by createdAt DESC
                .findFirst()
                .orElse(null);  // Return the most recent or null if no salary exists
    }

    public String getEmergencyContactName() {
        return emergencyContactName;
    }

    public void setEmergencyContactName(String emergencyContactName) {
        this.emergencyContactName = emergencyContactName;
    }

    public String getEmergencyContactMobile() {
        return emergencyContactMobile;
    }

    public void setEmergencyContactMobile(String emergencyContactMobile) {
        this.emergencyContactMobile = emergencyContactMobile;
    }

    public String getRefId() {
        return refId;
    }

    public void setRefId(String refId) {
        this.refId = refId;
    }

    public String getEducationTitle() {
        return educationTitle;
    }

    public void setEducationTitle(String educationTitle) {
        this.educationTitle = educationTitle;
    }

    public int getAge()
    {
        if(this.birthDate !=null)
        {
             try
             {
                 // Convert java.util.Date to java.time.LocalDate
                 LocalDate birthDateLocal = birthDate.toInstant()
                         .atZone(ZoneId.systemDefault())
                         .toLocalDate();


                 return Period.between(birthDateLocal, LocalDate.now()).getYears();
             }
             catch (Exception e)
             {
                 System.out.println("Can't get Age Date Problem: "+e.getMessage());
                 return 0;
             }

        }
        return 0;
    }
}
