package com.alhashim.oneIT.controllers;

import com.alhashim.oneIT.dto.AddEmployeeToDepartmentDto;
import com.alhashim.oneIT.dto.DepartmentDto;

import com.alhashim.oneIT.models.Department;
import com.alhashim.oneIT.models.Employee;
import com.alhashim.oneIT.repositories.DepartmentRepository;
import com.alhashim.oneIT.repositories.EmployeeRepository;
import com.alhashim.oneIT.services.DepartmentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/department")
public class DepartmentController {

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private DepartmentService departmentService;


    @GetMapping("/list")
    public String departmentList(Model model)
    {
        List<Department> departments = departmentRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));


        //---------
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Employee currentUser = employeeRepository.findByBadgeNumber(authentication.getName()).orElse(null);
        String loginUser = currentUser.getBadgeNumber() +"|"+currentUser.getName();
        model.addAttribute("loginUser", loginUser);
        //--------

        model.addAttribute("pageTitle","Department List");
        model.addAttribute("departments",departments);
        return "department/list";
    }

    @GetMapping("/add")
    public String departmentAddPage(Model model)
    {

        List<Employee> employees = employeeRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));

        //---------
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Employee currentUser = employeeRepository.findByBadgeNumber(authentication.getName()).orElse(null);
        String loginUser = currentUser.getBadgeNumber() +"|"+currentUser.getName();
        model.addAttribute("loginUser", loginUser);
        //--------


        DepartmentDto departmentDto = new DepartmentDto();
        model.addAttribute("pageTitle","Add New Department");
        model.addAttribute("departmentDto", departmentDto);
        model.addAttribute("employees", employees);
        return "department/add";
    }

    @PostMapping("/add")
    public String addDepartment(@Valid @ModelAttribute DepartmentDto departmentDto, BindingResult result ,Model model)
    {
        List<Employee> employees = employeeRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
        model.addAttribute("employees", employees);

        if(result.hasErrors())
        {
            result.addError(new FieldError("departmentDto", "name", result.getFieldErrors().toString()));
            return "/department/add";
        }

        Employee departmentManager = employeeRepository.findByBadgeNumber(departmentDto.getManager()).orElse(null);


        Department department = new Department();
        department.setName(departmentDto.getName());
        department.setArName(departmentDto.getArName());
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
            return "/department/add";
        }


        return "redirect:/department/list";
    }

    @GetMapping("/detail")
    public String departmentDetail(Model model, @RequestParam long id, @RequestParam(required = false) String keyword)
    {
        Set<Employee> departmentEmployees;

        AddEmployeeToDepartmentDto addEmpDepartmentDto = new AddEmployeeToDepartmentDto();

        List<Employee> employees = employeeRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));

        Department department =  departmentRepository.findById(id).orElse(null);

        if(keyword ==null || keyword.isEmpty())
        {
            assert department != null;
            departmentEmployees = department.getEmployees();
        }
        else
        {

            System.out.println("Search for employee with keyword : "+keyword);
            departmentEmployees = departmentService.searchEmployeesInDepartment(department.getId(),keyword);
        }


        //---------
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Employee currentUser = employeeRepository.findByBadgeNumber(authentication.getName()).orElse(null);
        String loginUser = currentUser.getBadgeNumber() +"|"+currentUser.getName();
        model.addAttribute("loginUser", loginUser);
        //--------


        model.addAttribute("employees", employees);
        model.addAttribute("departmentEmployees", departmentEmployees);


       if(department !=null)
       {
           addEmpDepartmentDto.setDepartmentId(department.getId()); // Set departmentId
           model.addAttribute("pageTitle","Department Detail");
           model.addAttribute("department", department);
           model.addAttribute("addEmpDepartmentDto", addEmpDepartmentDto);
           return "department/detail";
       }

        return "department/list";
    }

    @PostMapping("/add-employee")
    public String AddEmployeeToDepartment(@Valid @ModelAttribute AddEmployeeToDepartmentDto addEmpDepartmentDto, BindingResult result ,Model model)
    {
        Employee employee = employeeRepository.findByBadgeNumber(addEmpDepartmentDto.getBadgeNumber()).orElse(null);
        Department department = departmentRepository.findById(addEmpDepartmentDto.getDepartmentId()).orElse(null);
        if(employee !=null && department !=null)
        {
            employee.setDepartment(department);
            employee.setUpdatedAt(LocalDateTime.now());
            employeeRepository.save(employee);
        }
        return "redirect:/department/detail?id="+addEmpDepartmentDto.getDepartmentId().toString();
    }
}
