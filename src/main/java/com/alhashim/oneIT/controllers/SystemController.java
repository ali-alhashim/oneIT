package com.alhashim.oneIT.controllers;


import com.alhashim.oneIT.models.Employee;
import com.alhashim.oneIT.models.Role;
import com.alhashim.oneIT.repositories.EmployeeRepository;
import com.alhashim.oneIT.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/system")
public class SystemController {



    @Autowired
    RoleRepository roleRepository;
    @Autowired
    EmployeeRepository employeeRepository;

    @GetMapping("/roles")
    public String rolesList(Model model)
    {
        List<Role> roles = roleRepository.findAll();

        model.addAttribute("roles", roles);

        return "/system/rolesList";
    }

    @GetMapping("/rolesDetail")
    public String rolesDetail(Model model, @RequestParam Long id, @RequestParam(required = false) String keyword, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size)
    {
        Page<Employee> employeePage;
        Role role = roleRepository.findById(id).orElse(null);
       if(role ==null)
       {
           return "/404";
       }
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
       employeePage = employeeRepository.findByRoles_RoleName(role.getRoleName(),pageable );

       List<Employee> employees = employeeRepository.findAll();


        model.addAttribute("employeePage", employeePage.getContent());
        model.addAttribute("employees", employees);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", employeePage.getTotalPages());
        model.addAttribute("pageSize", size);
        model.addAttribute("totalItems", employeePage.getTotalElements());
        model.addAttribute("pageTitle","Role Members List");
        model.addAttribute("role", role);

        return "system/rolesDetail";
    }
}
