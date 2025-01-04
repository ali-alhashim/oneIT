package com.alhashim.oneIT.controllers;


import com.alhashim.oneIT.dto.BenefitDto;
import com.alhashim.oneIT.dto.CalculateMinutesDto;
import com.alhashim.oneIT.dto.DepartmentEmployeesDto;
import com.alhashim.oneIT.dto.PayslipDto;
import com.alhashim.oneIT.models.*;
import com.alhashim.oneIT.repositories.DepartmentRepository;
import com.alhashim.oneIT.repositories.EmployeeCalendarRepository;
import com.alhashim.oneIT.repositories.EmployeeRepository;
import com.alhashim.oneIT.repositories.PayslipRepository;
import com.alhashim.oneIT.services.SalaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.time.DayOfWeek;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/payslip")
public class PayslipController {

    @Autowired
    PayslipRepository payslipRepository;

    @Autowired
    DepartmentRepository departmentRepository;

    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    EmployeeCalendarRepository employeeCalendarRepository;

    @Autowired
    private SalaryService salaryService;

    @GetMapping("/list")
    public String payslipList(Model model, @RequestParam(required = false) String keyword, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size)
    {
        Page<Payslip> payslipPage;
        if(keyword !=null && !keyword.isEmpty())
        {
            // Implement a paginated search query in your repository
            payslipPage = payslipRepository.findByKeyword(keyword, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id")));
        }
        else
        {
            // Fetch all  with pagination
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
            payslipPage = payslipRepository.findAll(pageable);
        }


        model.addAttribute("payslips", payslipPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", payslipPage.getTotalPages());
        model.addAttribute("pageSize", size);
        model.addAttribute("totalItems", payslipPage.getTotalElements());
        model.addAttribute("pageTitle","Payslip List");
        return "/payslip/list";
    }


    @GetMapping("/add")
    public String addPayslip(Model model)
    {
        PayslipDto payslipDto = new PayslipDto();

        List<Department> departments = departmentRepository.findAll();

        model.addAttribute("departments", departments);
        model.addAttribute("payslipDto", payslipDto);
        return "/payslip/add";
    }

    @GetMapping("/getDepartmentEmployees")
    public ResponseEntity<List<DepartmentEmployeesDto>> getDepartmentEmployees(@RequestParam Long departmentId) {
        System.out.println("Fetching employees for department ID: " + departmentId);

        Department department = departmentRepository.findById(departmentId).orElse(null);
        if (department == null) {
            throw new RuntimeException("Department not found");
        }

        List<Employee> employees = new ArrayList<>(department.getEmployees());

        List<DepartmentEmployeesDto> employeesDto = employees.stream()
                .map(employee -> {
                    Salary currentSalary = employee.getCurrentSalary();

                    // Get basic salary separately
                    BigDecimal basicSalary = salaryService.getBasicSalaryForSalary(currentSalary);

                    // Get all other benefits excluding basic salary
                    List<BenefitDto> benefits = salaryService.getBenefitsForSalary(currentSalary);

                    // Construct DTO
                    DepartmentEmployeesDto dto = new DepartmentEmployeesDto(
                            employee.getId(),
                            employee.getBadgeNumber(),
                            employee.getName(),
                            employee.getBankName(),
                            employee.getIban(),
                            basicSalary  // Pass the basic salary directly
                    );

                    // Set the list of benefits
                    dto.setBenefits(benefits);

                    return dto;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(employeesDto);
    }


    @GetMapping("/CalculateMinutes")
    public ResponseEntity<CalculateMinutesDto> calculateMinutes(
            @RequestParam String badgeNumber,
            @RequestParam LocalDate periodStart,
            @RequestParam LocalDate periodEnd) {

        Employee employee = employeeRepository.findByBadgeNumber(badgeNumber)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        BigDecimal basicSalary = salaryService.getBasicSalaryForSalary(employee.getCurrentSalary());
        ShiftSchedule shiftSchedule = employee.getShiftSchedule();

        if (shiftSchedule == null) {
            throw new RuntimeException("Employee without Shift Schedule!");
        }

        // Get employee attendance for the selected period
        List<EmployeeCalendar> employeeCalendars = employeeCalendarRepository.findByEmployeeFromTo(employee, periodStart, periodEnd);

        int totalMissingMinutes = 0;

        // Loop through each day in the period to calculate missing minutes
        for (LocalDate date = periodStart; !date.isAfter(periodEnd); date = date.plusDays(1)) {
            DayOfWeek dayOfWeek = date.getDayOfWeek();

            // Check if the day is a workday in the shift schedule
            if (shiftSchedule.isWorkDay(dayOfWeek)) {
                LocalDate finalDate = date;
                EmployeeCalendar calendarRecord = employeeCalendars.stream()
                        .filter(cal -> cal.getDayDate().equals(finalDate))
                        .findFirst()
                        .orElse(null);

                // If no record exists for a workday, consider it as an absence
                if (calendarRecord == null) {
                    totalMissingMinutes += 480;  // Full 8-hour day in minutes (480) for absence
                    continue;
                }

                // Check for lateness or early checkout
                int missingMinutes = calculateLatenessAndEarlyLeave(calendarRecord, shiftSchedule);
                totalMissingMinutes += missingMinutes;
            }
        }

        // Calculate deduction based on missing minutes
        BigDecimal ratePerMinute = basicSalary.divide(BigDecimal.valueOf(30 * 8 * 60), 2, RoundingMode.HALF_UP);
        BigDecimal deduction = ratePerMinute.multiply(BigDecimal.valueOf(totalMissingMinutes));
        BigDecimal deductedBasicSalary = basicSalary.subtract(deduction);

        // Prepare DTO response
        CalculateMinutesDto responseDto = new CalculateMinutesDto();
        responseDto.setDeductedBasicSalary(deductedBasicSalary);
        responseDto.setTotalMM(totalMissingMinutes);

        return ResponseEntity.ok(responseDto);
    } // end GET CalculateMinutes


    private int calculateLatenessAndEarlyLeave(EmployeeCalendar calendarRecord, ShiftSchedule shiftSchedule) {
        int missingMinutes = 0;

        // Check-in lateness
        if (calendarRecord.getCheckIn() != null) {
            int lateness = calculateLateness(calendarRecord.getCheckIn(), shiftSchedule.getStartTime());
            if (lateness > 15) {
                missingMinutes += lateness;  // Count lateness if > 15 mins
            }
        }

        // Early check-out
        if (calendarRecord.getCheckOut() != null) {
            int earlyLeave = calculateEarlyLeave(calendarRecord.getCheckOut(), shiftSchedule.getEndTime());
            if (earlyLeave > 10) {
                missingMinutes += earlyLeave;  // Count early leave if > 10 mins
            }
        }

        return missingMinutes;
    }

    private int calculateLateness(LocalTime checkIn, LocalTime shiftStart) {
        return (int) Duration.between(shiftStart, checkIn).toMinutes();
    }

    private int calculateEarlyLeave(LocalTime checkOut, LocalTime shiftEnd) {
        return (int) Duration.between(checkOut, shiftEnd).toMinutes();
    }

    // ------calculateLatenessAndEarlyLeave


    @PostMapping("/add")
    public String addPayslip(@RequestParam Map<String, String> formData) {
        // To store the results of employee data extraction
        System.out.println("******** addPayslip ***************** ");
        Map<String, Object> employeeData = new HashMap<>();

        // Extract CSRF token and other static fields first
        String csrfToken = formData.get("_csrf");
        String codeName = formData.get("codeName");
        String periodStart = formData.get("periodStart");
        String periodEnd = formData.get("periodEnd");
        String departmentId = formData.get("departmentId");

        // Store static data
        employeeData.put("csrfToken", csrfToken);
        employeeData.put("codeName", codeName);
        employeeData.put("periodStart", periodStart);
        employeeData.put("periodEnd", periodEnd);
        employeeData.put("departmentId", departmentId);

        // Now dynamically extract data for each employee by badge number
        for (String key : formData.keySet()) {
            if (key.startsWith("DeductedBasicSalary_")) {
                String badgeNumber = key.split("_")[1]; // Extract badge number (e.g. A1095)
                String deductedSalary = formData.get(key);
                String totalMM = formData.get("TotalMM_" + badgeNumber);

                // Store each employee's data by badge number
                Map<String, String> employee = new HashMap<>();
                employee.put("badgeNumber", badgeNumber);
                employee.put("deductedBasicSalary", deductedSalary);
                employee.put("totalMM", totalMM);

                employeeData.put("employee_" + badgeNumber, employee);
            }
        }

        // For testing purposes, printing out the collected data
        System.out.println(employeeData);

        return "redirect:/payslip/list";
    }


}
