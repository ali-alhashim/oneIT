package com.alhashim.oneIT.controllers;

import com.alhashim.oneIT.dto.SalaryDto;
import com.alhashim.oneIT.models.Employee;
import com.alhashim.oneIT.models.Salary;
import com.alhashim.oneIT.models.SalaryLine;
import com.alhashim.oneIT.models.SystemLog;
import com.alhashim.oneIT.repositories.EmployeeRepository;
import com.alhashim.oneIT.repositories.SalaryRepository;
import com.alhashim.oneIT.repositories.SystemLogRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/salaries")
public class SalariesController {

    @Autowired
    EmployeeRepository employeeRepository;
    @Autowired
    SalaryRepository salaryRepository;

    @Autowired
    SystemLogRepository systemLogRepository;


    @PostMapping("/addSalary")
    public String addSalary(@Valid @ModelAttribute SalaryDto salaryDto, BindingResult result, Model model)
    {
        if(result.hasErrors())
        {
            model.addAttribute("message", result.getAllErrors());
            return "/404";
        }
        //http://localhost:8080/salaries/addSalary
        Salary salary = new Salary();
        Employee employee = employeeRepository.findByBadgeNumber(salaryDto.getBadgeNumber()).orElse(null);
        if(employee == null)
        {
            return "/404";
        }
        salary.setEmployee(employee);
        salary.setCreatedAt(LocalDateTime.now());
        salary.setGrossDeduction(salaryDto.getGrossDeduction());
        salary.setGrossEarning(salaryDto.getGrossEarning());
        salary.setNetPay(salaryDto.getNetPay());

        List<SalaryLine> salaryLines = salaryDto.getLines().stream().map(salaryLineDto -> {
            SalaryLine line = new SalaryLine();
            line.setSalary(salary);
            line.setPayDescription(salaryLineDto.getPayDescription());
            line.setToDeduct(salaryLineDto.getToDeduct());
            line.setToPay(salaryLineDto.getToPay());
            return line;
        }).collect(Collectors.toList());

        salary.setLines(salaryLines);
        salaryRepository.save(salary);


        // Log the  action
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Employee currentUser = employeeRepository.findByBadgeNumber(authentication.getName()).orElse(null);
        SystemLog systemLog = new SystemLog();
        systemLog.setCreatedAt(LocalDateTime.now());
        systemLog.setEmployee(currentUser);
        systemLog.setDescription("Add Salary for  : "+employee.getName());
        systemLogRepository.save(systemLog);

        return "redirect:/employee/detail?badgeNumber="+salaryDto.getBadgeNumber();
    }
}
