package com.alhashim.oneIT.controllers;

import com.alhashim.oneIT.dto.DepartmentDto;

import com.alhashim.oneIT.models.Department;
import com.alhashim.oneIT.models.Employee;
import com.alhashim.oneIT.repositories.DepartmentRepository;
import com.alhashim.oneIT.repositories.EmployeeRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/department")
public class DepartmentController {

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private EmployeeRepository employeeRepository;


    @GetMapping("/list")
    public String departmentList(Model model)
    {
        List<Department> departments = departmentRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));

        model.addAttribute("pageTitle","Department List");
        model.addAttribute("departments",departments);
        return "department/list";
    }

    @GetMapping("/add")
    public String departmentAddPage(Model model)
    {

        List<Employee> employees = employeeRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));


        DepartmentDto departmentDto = new DepartmentDto();
        model.addAttribute("pageTitle","Add New Department");
        model.addAttribute("departmentDto", departmentDto);
        model.addAttribute("employees", employees);
        return "department/add";
    }

    @PostMapping("/add")
    public String addDepartment(@Valid @ModelAttribute DepartmentDto departmentDto, BindingResult result ,Model model)
    {
        if(result.hasErrors())
        {
            List<Employee> employees = employeeRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
            model.addAttribute("employees", employees);
            return "department/add";
        }

        Employee departmentManager = employeeRepository.findByBadgeNumber(departmentDto.getManager()).orElse(null);


        Department department = new Department();
        department.setName(departmentDto.getName());
        department.setAr_name(departmentDto.getAr_name());
        department.setDescription(departmentDto.getDescription());
        department.setCreatedAt(LocalDateTime.now());
        if(departmentManager !=null)
        {
            department.setManager(departmentManager);
        }

        try
        {
            departmentRepository.save(department);
        }
        catch (Exception e)
        {
            result.addError(new FieldError("departmentDto", "name", e.getMessage()));
        }


        return "department/list";
    }

    @GetMapping("/detail")
    public String departmentDetail(Model model, @RequestParam long id)
    {
       Department department =  departmentRepository.findById(id).orElse(null);

       if(department !=null)
       {
           model.addAttribute("pageTitle","Department Detail");
           model.addAttribute("department", department);
           return "department/detail";
       }

        return "department/list";
    }
}
