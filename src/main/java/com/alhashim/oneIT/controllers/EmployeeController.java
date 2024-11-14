package com.alhashim.oneIT.controllers;


import com.alhashim.oneIT.models.Employee;
import com.alhashim.oneIT.repositories.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeRepository employeeRepository;
    @GetMapping("/list")
    public String employeeList(Model model)
    {

        List<Employee> employees = employeeRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
        model.addAttribute("employees", employees);

        return "employee/list";
    }
}
