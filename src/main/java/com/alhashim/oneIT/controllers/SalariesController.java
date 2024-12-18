package com.alhashim.oneIT.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/salaries")
public class SalariesController {

    @PostMapping("/AddSalary")
    public String addSalary()
    {
        return "redirect:/employee/detail?badgeNumber=";
    }
}
