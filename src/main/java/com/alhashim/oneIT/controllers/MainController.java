package com.alhashim.oneIT.controllers;

import com.alhashim.oneIT.models.Employee;
import com.alhashim.oneIT.repositories.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller

public class MainController {

    @Autowired
    private EmployeeRepository employeeRepository;

    @GetMapping("/dashboard")
    public String dashboardPage(Model model)
    {
        String badgeNumber = SecurityContextHolder.getContext().getAuthentication().getName();
        Employee employee = employeeRepository.findByBadgeNumber(badgeNumber).orElse(null);
        System.out.println("Welcome to Dashboard Page badgeNumber: " + badgeNumber);
        assert employee != null;
        System.out.println("Welcome to Dashboard Page Name: " + employee.getName());

            model.addAttribute("badgeNumber", badgeNumber);
            model.addAttribute("userName", employee.getName());
            model.addAttribute("userArName", employee.getAr_name());
            model.addAttribute("imageFileName", employee.getImageFileName());
            return "dashboard";



    }

    @GetMapping("/login")
    public String loginPage(Model model)
    {
        return "login";
    }

    @GetMapping("/logout")
    public String logoutPage(Model model)
    {
        //clear the session
        return "logout";
    }
}
