package com.alhashim.oneIT.controllers;

import com.alhashim.oneIT.repositories.DepartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/department")
public class DepartmentController {

    @Autowired
    private DepartmentRepository departmentRepository;

    @GetMapping("/list")
    public String departmentList(Model model)
    {
        model.addAttribute("pageTitle","Department List");
        return "department/list";
    }
}
