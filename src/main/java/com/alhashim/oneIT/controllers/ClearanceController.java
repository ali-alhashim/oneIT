package com.alhashim.oneIT.controllers;

import com.alhashim.oneIT.models.EmployeeClearance;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/clearance")
public class ClearanceController {

    @GetMapping("/list")
    public String clearanceList()
    {
        Page<EmployeeClearance> employeeClearancePage;
        return "/clearance/list";
    }
}
