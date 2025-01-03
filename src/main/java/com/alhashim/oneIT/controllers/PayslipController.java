package com.alhashim.oneIT.controllers;


import com.alhashim.oneIT.dto.BenefitDto;
import com.alhashim.oneIT.dto.DepartmentEmployeesDto;
import com.alhashim.oneIT.dto.PayslipDto;
import com.alhashim.oneIT.models.Department;
import com.alhashim.oneIT.models.Employee;
import com.alhashim.oneIT.models.Payslip;
import com.alhashim.oneIT.models.Salary;
import com.alhashim.oneIT.repositories.DepartmentRepository;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.ArrayList;
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
}
